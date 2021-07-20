Mixing Android and iOS programming concepts
=============================================

Summary
-------

This project describes a guide for iOS and Android mobile app development,taking into consideration the guide lines provideded by the architecture manufacturers in current analysis.
The goal is to define a single guideline for the development of iOS and Android apps wirtten in native languages.
The focus it's on app engine of both architectures, starting by date acquisition of data via an API, presitent storage and presenting the data to the end user. THe goal it's to make easier the devolpment, support and maintaince of both applications.

This project it's writen for Android using `kotlin` but [here](https://github.com/lgleto/TopNews-iOS) the same project written for iOS using `swift`.

Introduction
------------

To do this demostration it will be used a common application as an example that exchanges information with a server through an API, and that presents that information to the user. It also store this information so that it can be accessed in offline mode. This same information should be updated whenever the application establishes connection with the server.
The app will be news app that displays a list of articles in the first screen followed by an article detail, when an article is touched. The article detail sould presente the web page in the newspaper website or picture with the title and the discription if the internet connection is off.

![App example](docs/mobile-app.png "App example")

#### API - newsapi.org

It will be used newsapi as the REST API to provide the news. The following request retrive the latest news for a particular country.

Request:
```
GET https://newsapi.org/v2/top-headlines?country=us&apiKey=<API KEY>
```

Response:
```javascript
{
  "status": "ok",
  "totalResults": 38,
  "articles": [
    {
      "source": {
        "id": "cnn",
        "name": "CNN"
      },
      "author": "Story by Reuters",
      "title": "Teenager [...]" ,
      "url": "https://www.cnn.com/2021/05/05/americas/brazil-daycare-stabbing-intl-hnk/index.html",
      "urlToImage": "https://cdn.cnn.com/cnnnext/dam/assets/210505004438-brazil-daycare-stabbing-0504-super-tease.jpg",
"publishedAt":  "2021-05-05T05:19:00Z",
      "content": null
    },
    {
      "source": {
        "id": null,
        "name": "KING5.com"
      },
      "author": "KING 5 Staff",
      "title": "SpaceX Starlink [...]",
      "description": "A University of [...]." ,
      "url": "https://www.king5.com/article/tech/science/lights-streaking-across-night-sky-in-western-washington-again/281-a4227a83-20ca-484d-8e0d-da48b4fa8f97",
      "urlToImage": "https://media.king5.com/assets/KING/images/c80a000e-32c5-46ba-8da3-dc25d0d3455e/c80a000e-32c5-46ba-8da3-dc25d0d3455e_1140x641.jpg",
"publishedAt":  "2021-05-05T05:08:00Z",
      "content": "SEATTLE It appears s[...]"
    },
 [...]
}
```

#### Open API and Swagger

In order to reduce drasticly the written code by the developer we will use OPENAPI to describe the requests and data models from the server and generate code with swagger for booth Android and iOS.

This [file](app/src/main/java/swagger.yaml) discribes the API following the [OpenAPI](https://oai.github.io) specification.

Now using [Swagger](https://swagger.io) code generator we can reduce the API call on Android to this:

```kotlin
val newsApi: NewsApi by lazy { retrofit.create(NewsApi:: class.java) }
val serverArticles = newsApi.topHeadlinesGet( country, category, apiKey)
```

```swift
NewsAPI.topHeadlinesGet(apiKey: NEWS_API_KEY, country: COUNTRY, category: category) { 
	(articles, error) in 
	[...]
}
```

Android Architecture
--------------------

Google proposes the following type of architecture represented in next picture: 

![App example](docs/android-architecture.png "App example")

The full discription of the architecture can be found [here](https://developer.android.com/guide/components/activities/activity-lifecycle).

Starting from the bottom up now it will be describe the implementation of each compnent represented in the picture.

### Room

[Room](https://developer.android.com/jetpack/androidx/releases/room?gclid=Cj0KCQjwytOEBhD5ARIsANnRjVg8eMeCTZf2hnkkZja89BGmPTyCxUub-asFIK6KctvYRak8Ba8EjvQaAlj2EALw_wcB&gclsrc=aw.ds.) implements an abstraction layer to make it simple to store persitent data using SQLite in the app.
Room allows you to create tables and fields through annotations that are placed  in classes and their properties.
Room will be used in this example to store the server data. Since Swagger is used to generate data models, it will be created one more model to make that same     data persistent.

| ArticleCache           |
|------------------------|
| - url        : String  |
| - jsonString : String? |
| - category   : String  |

In the `ArticleCahce` model the `url` will be used as primary key and the jsonString will store the `Article` in JSON string format. Finally `category` will be used to store the category of the articles, since this property is not a part of the API, but it is essential to know in what context a particular article should be shown.
The following code shows how To specify this class in Android using Room.

```kotlin
@Entity
class ArticleCache( @field:PrimaryKey 
                    var url        : String, 
                    var jsonString : String?, 
                    var category   : String) 
```

Next, it will be declared an interface with the Data Access Object (DAO) where CRUD operations are implemented using `Query` annotations on top of each function signature.
These functions are also marked as "suspend" so  that the compiler knows that the contents block the "thread" on which they are running.

```kotlin
@Dao
interface ArticleCacheDao {
 
    @Query("SELECT * FROM ArticleCache WHERE category = :category")
    suspend fun getAll(category:String): List<ArticleCache>
 
    @Query("DELETE FROM ArticleCache WHERE url = :url")
    suspend fun delete(url: String)
 
    @Query("SELECT * FROM ArticleCache WHERE url = :url")
    suspend fun getByUrl(url: String): ArticleCache
 
    @Insert(onConflict = OnConflictStrategy. REPLACE)
    suspend fun insert(articleCache: ArticleCache)
}
```

And than the initialization of our Room Database:

```kotlin
@Database(entities = [ArticleCache::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
 
    abstract fun articleCacheDao(): ArticleCacheDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
 
        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "db_topnews"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE
        }
    }
}
```

`fallbackToDestructiveMigration` garanties that the databases is rebuilted when when the datbase version is changed. That happens in most of the case that apps can retrive old data from the API.

### Remote Data Source - Backend

On the backend class it will be initialized the HTTP access to yout API using `OkHttp` and also to initialize `Retrofit` that holds the `OkHttpClient`, the API url and it is responsible to convert the JSON objects responsoses from the API into Kotlin data models.

```kotlin
class Backend {
    private val retrofit : Retrofit
    init {
        val httpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build()
        retrofit = Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GeneratedCodeConverters.converterFactory())
                .client(httpClient)
                .build()
 
    }
    // endpoints
    val newsApi: NewsApi by lazy { retrofit.create(NewsApi:: class.java) }
}
```

### Repository

The repository class it's responsible to gather data from the server and store it on the local datbase withou duplicating it. When the user opens the app he will see firstly the date store on the mobile and than the data fetched from the server.
This class is defined by the `Singleton` pattern that allows the object of this class to be instantiated only once in the application lifecycle.

The `getCachedArticles` function is will ask local database for articles of a particular category. This function is only invoked by the `getArticles` function, which is responsible for fetching the articles on the remote server. 


```kotlin
object Repository {
    private val backend = Backend()

    suspend fun getCachedArticles (context: Context, category: String) : Articles {
        val articles = Articles()

        val totalArticlesCached = AppDatabase.getDatabase(context)?
        .articleCacheDao()?
        .getAll(category)
        val articlesLocal : MutableList<Article> = arrayListOf()
        totalArticlesCached?.let {
            for (articleCached in it) {
                val article = Article().fromJson(articleCached.jsonString)
                article?.let { it1 -> articlesLocal.add(it1) }
            }
        }

        articles.articles = articlesLocal         
        articles.status = "local"         
        articles.totalResults = articlesLocal.size

        return articles     
    }
	[...]
}
```

The `getArticles` function returns a `LiveData` type object, which is an observable data class. This type of object allows you to notify another observer object type whenever the data is modified when `emit` triggered. In this case `emit` is called twice once with the local and that after fetching remote data.

```kotlin
object Repository {

     fun getArticles( category: String, 
		 country: String, 
		 apiKey: String, 
		 context: Context): LiveData<Articles> = liveData(IO) {
        emit( getCachedArticles (context, category) )
        try {
            val serverArticles = backend.newsApi.topHeadlinesGet( country, category, apiKey)
            serverArticles.articles?.forEach { article ->                 
		article.url?.let { url ->                     
		val articleCache = ArticleCache(url, article.toJsonString(), category)
                    AppDatabase.getDatabase(context)?.articleCacheDao()?.insert(articleCache)
                }
                emit( getCachedArticles (context, category) )
            }
        } catch (throwable: Throwable) {
            Log.e("Repository", throwable.toString())
        }
    }

}
```

### View Model

Activities and fragments lifecycle determines that both elements can be estroyed and recreated in response to user or operating system actions. Whenever the operating system decides to destroy or recreate an activity it will also destrou all data that is hold by the activity. Because all of this work must be done ina asynchronous tasks it can be painfull to create and destrou those asyncronous task regarding the activity lifecycle.   
To work around this problem, Google proposes the `ViewModel`, which recreates the lifecycle of the view component. This way the obtained data is saved by ViewModel, whichare  independent  of the component that created it.
In this example the  ArticlesViewModel class, which inherits from the ViewModel class is responsible for obtaining  data from the repository. `switchMap` method garanties that it only access the repository when the category value is modified.


```kotlin
class ArticlesViewModel() : ViewModel() {
 
    private val _category: MutableLiveData<String> = MutableLiveData()
 
    val articlesForCategory: LiveData<Articles> =
        Transformations.switchMap(_category) {
            Repository.getArticles( it,Globals.COUNTRY, 
                Globals.NEWS_API_KEY, context!!)
        }
 
    private var context : Context? = null
 
    fun setCategory(category: String, context: Context) {
        this.context = context
        if (_category.value == category) return
        _category.value = category
    }
 
}
```

### Controller Activity/Fragment

Neste exemplo são utilizados Fragments, mas o processo é similar ao uso de uma Activity. Para fazer o “binding” de ViewModel começa por se instanciar o ViewModel a partir de um Factory indicando a classe que contem o nosso ViewModel (ArticlesViewModel). De seguida, cria-se um observador para esse ViewModel, que tem como referência o “viewLifeCycleOwner”, que neste caso representa o ciclo de vida do Fragment. Assim, o que está dentro do observador apenas é despoletado enquanto o Fragment existir.

In this example it's Fragments are used, but the process is similar in an Activity. The last thing to be done is to bind the ViewModel with the controller. After the initializatio of the our ViewModel it must be created an observer that has the reference of `viewLifeCycleOwner`, which in this case represents the Fragment lifecycle. Thus, what is inside the observer is only triggered as long as the Fragment exists.

```kotlin
     private lateinit var mainActivityViewModel: ArticlesViewModel
 
     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
 
        mainActivityViewModel = ViewModelProvider(this, 
            ViewModelProvider.NewInstanceFactory())
            .get(ArticlesViewModel::class.java)
 
        mainActivityViewModel.articlesForCategory.observe(viewLifecycleOwner, 
            Observer { totalArticlesCached->
            totalArticlesCached?.let { fetchedArticles ->
                fetchedArticles.articles?.let { articlesList ->
                    articles = articlesList.sortedByDescending {
                        it.publishedAt
                    }
                    mAdapter?.notifyDataSetChanged()
                }
            }
        })
        mainActivityViewModel.setCategory(Globals.ENDPOINT_GENERAL, requireContext())
    }
```
Android Architecture
--------------------

A Apple ainda continua a aconselhar a arquitetura MVC. Como o UIViewController apenas é destruído ao remover todos as referências a objetos, os dados carregados pelo controlador também permanecem da mesma forma. Apesar de haver alguns programadores a implementar MVVM, isto apenas acrescenta complexidade à forma de requisitar os dados, sem trazer grandes benefícios. Para aproximar esta implementação à implementação do Android, optou-se por criar uma classe que serve de repositório para agregar os dados locais e os dados do servidor Figura 13 [7].

![iOS app arquitecture](docs/ios-architecture.png "iOS app arquitecture")

### Modelo - CoreData

Desde muito cedo que as extensões apareceram no Objective-C e trouxeram grandes vantagens a esta linguagem. As extensões permitem aumentar a funcionalidade de uma classe sem ser através de herança. O CoreData Figura 14, Framework responsável pela gestão de dados permanentes no iOS, tira vantagem a este tipo de arquitetura, pois permite criar os modelos de dados graficamente e gerar o código a ele associado.
 
Figura 14 – Interface gráfico do editor do CoreData no XCode
O desenho da tabela apresentada na imagem da Figura 14, dá origem ao código que se apresenta de seguida. E, caso mude alguma coisa no modelo de dados, o único ficheiro afetado será a extensão correspondente, neste caso a extensão ArticleCache. 

```swift
extension ArticleCache {
    @nonobjc public class func fetchRequest() -> NSFetchRequest<ArticleCache> {
        return NSFetchRequest<ArticleCache>(entityName: "ArticleCache")
    }
    @NSManaged public var url: String?
    @NSManaged public var jsonString: String?
    @NSManaged public var category: String?
}
```

A classe ArticleCache é criada uma única vez e é mantida mesmo que se façam alterações à tabela no ficheiro do CoreData. Assim, é possível fazer todas as implementações de acesso básico à base de dados na sua classe, que se mantem mesmo que as tabelas sejam alteradas numa faze posterior do projeto. 
Tal como o nome indica, e a título de exemplo, implementou-se a função “addItem” para adicionar um novo item à base de dados. Esta função primeiramente verifica se objeto que se pretende adicionar já existe. Se existir, faz a sua atualização. Caso não exista cria essa nova entidade na base de dados, onde são atribuidos os novos valores nas suas propriedades e devolvido o objeto criado.
As restantes implementações do CRUD seguem o mesmo padrão da “addItem”.

```swift
@objc(ArticleCache)
public class ArticleCache: NSManagedObject {
    
    class func addItem(url: String, jsonString: String, category: String, 
                        inManagedObjectContext  context:NSManagedObjectContext) -> ArticleCache? {
        
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ArticleCache")
        request.predicate = NSPredicate(format: "url = %@", url)
       
        if let _ = (try? context.fetch(request))?.first as? ArticleCache {
            return updateItem(url: url, jsonString: jsonString, category: category, 
                                inManagedObjectContext: context)
        }
        else if let articleCache = NSEntityDescription.insertNewObject(
                                    forEntityName: "ArticleCache", 
                                    into: context) as? ArticleCache {
            articleCache.url        = url
            articleCache.jsonString = jsonString
            articleCache.category   = category
            return articleCache
        }
        return nil
    }
    
    class func updateItem(url: String, jsonString: String, category: String, 
                            inManagedObjectContext context: NSManagedObjectContext) -> ArticleCache? {
       [...]
    }
    
    class func getAll(category: String, 
                        inManagedObjectContext context:NSManagedObjectContext) -> [ArticleCache]?  {
        [...]
    }
    
   [...]
}
```

### Repository

À semelhança do código do Android, optou-se por criar a classe Repository que tem como objetivo agregar os dados armazenados localmente e os dados obtidos através do servidor. Desta forma, ao ligar a aplicação, o utilizador vai ter um contacto imediato com os dados. Assim que forem obtidos dados remotos, os mesmos são automaticamente atualizados. 
Desta vez não é necessário criar “SingleTone” nesta classe, uma vez que a implementação do Swagger em Swift permite aceder a cada um dos endpoint, sem ter que instanciar um objeto. 
A função getCachedArticles é responsável por pedir à base de dados local as notícias de uma determinada categoria. Não é necessário sincronizar o pedido com a thread de UI pois as operações de CoreData já são feitas de modo sincronizado com a UI. Esta função apenas é invocada pela função getArticles, que é responsável por agregar as notícias que estão no servidor remoto e as notícias na base de dados local.

```swift
class Repository {
    
    class func getCachedArticles(category: String, context: NSManagedObjectContext) -> [Article] {
        var articles : [Article] = []
        if let articlesCache = ArticleCache.getAll(category: category,
                                                   inManagedObjectContext:context ) {
            for articleCache in articlesCache {
                if let articleJson = articleCache.jsonString{
                    let article = Article.fromJson(jsonString: articleJson)
                    articles.append(article)
                }
            }
            return articles
        }
        return articles
    }
    [...]
}
```

A função getArticles retorna os dados num objeto definido como “callback”, que se despoleta sempre que houver dados novos para apresentar. A “callback” é despoletada duas vezes, uma com os dados locais, e antes de fazer o pedido de dados à API, e outra depois. Os dados vindos da API são primeiro guardados localmente e depois emitidos.

```swift
class Repository {
  [...]
     class func getArticles ( category :String, 
		           context : NSManagedObjectContext, 
		  	callback : @escaping ([Article], Error?)->() ) 
    {
        var chachedArtilces : [Article] = getCachedArticles(category: category, context: context)
        callback(chachedArtilces, nil )
 
        NewsAPI.topHeadlinesGet(apiKey: NEWS_API_KEY,
                                country: COUNTRY,
                                category: category) { (articles, error) in
            if (error == nil){
                if let arts = articles?.articles {
                    for article in arts{
                        _ = ArticleCache.addItem(url: article.url ?? "",
                                             jsonString: article.toJsonString(),
                                             category: category,
                                             inManagedObjectContext: context)
                    }
                }
                (UIApplication.shared.delegate as? AppDelegate)?.saveContext()
                chachedArtilces = getCachedArticles(category: category, context: context)
                callback(chachedArtilces, nil )
            }else {
                callback(chachedArtilces, error )
                print(error.debugDescription)
            }
        }
    }
}
```

### Controller & Model

A ligação dos dados com o controlador fica bastante simplificada. Usando o método “didSet” do encapsulamento de propriedades em Swift, cria-se o acesso ao repositório e esperam-se os dados na callback do método. Assim que os dados forem recebidos, atualiza-se a UI.

```swift
class ArticlesTVC: UITableViewController {
    
    var articles : [Article] = []
    var category : String = ""
    
    private var managedObjectContext: NSManagedObjectContext? {
        didSet {
            if let context = managedObjectContext {
                 Repository.getArticles(category: self.category, context: context,callback: { articles, error in
                    if error == nil {
                        self.articles = articles
                        self.tableView.reloadData()
                    }
                })
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.managedObjectContext = (UIApplication.shared.delegate as? AppDelegate)?.persistentContainer.viewContext
    }
 ... 
}
```

Discussion
----------

Os fabricantes dos sistemas operativos iOS e Android produzem a documentação necessária para programar novas aplicações e assim acrescentar valor às respetivas plataformas. Apesar de toda a documentação fornecida, nem sempre é possível encontrar um fio condutor que ajude os programadores a encontrar métodos e boas práticas de programação para desenvolver aquilo que aqui designamos por uma aplicação comum. 
Para além disso, uma aplicação comum fará sentido se for disponibilizada em ambos os sistemas operativos, o que agrava o problema de reunir toda a informação necessária para produzir boas aplicações para ambos os sistemas operativos. 
A produção de aplicações com recurso a plataformas não nativas poderá ser uma solução, mas apenas se estas aplicações não forem uma parte fundamental do negócio. Isto porque acarretam problemas desempenho e dependem de terceiros, sejam eles empresas ou comunidades open source, representanto, por isso, um risco acrescido.
Apesar do código ser diferente para Android e iOS, é possível definir uma arquitetura de base bastante semelhante na escrita de aplicações para ambos os sistemas operativos. As novas linguagens de programação Swift e Kotlin, também ajudam, pois apresentam bastantes similaridades, começando pelo paradigma de programação orientado a objetos, mas também devido às novas características de linguagens modernas. Atualmente, a única grande diferença entre ambas as arquiteturas de base é o novo MVVM proposto pela Google que aumenta o nível de estabilidade das aplicações Android. 
Ao adotar este documento como guia de arquitetura, simplifica-se o desenvolvimento e o suporte de aplicações, pois aqui estão reunidas várias etapas de desenvolvimento, desde aquisição de dados do servidor, passando pelo seu armazenamento e apresentação ao utilizador. Assim, o programador pode concentrar-se noutra parte fundamental que é o desenvolvimento do interface gráfico da aplicação. 
Este documento não aborda a codificação da interface gráfica das aplicações, pois apesar de haver alguns pontos em comum entre ambas as plataformas, estas devem ser pensadas individualmente considerando a familiaridade do utilizador com o sistema operativo. Este problema não se resume à codificação, mas também à usabilidade da aplicação, de acordo com as linhas orientadoras de desenho de interfaces de cada sistema operativo. Este é também um ponto negativo das plataformas não nativas, que não têm em conta a usabilidade dos sistemas operativos. 

License & copyright
-------------------

Copyright 2021 Lourenço Gomes

Licensed under [MIT License](LICENSE)
