Mixing Android and iOS programming concepts
=============================================

Summary
-------

This project describes a guide for iOS and Android mobile app development, taking into consideration the guide lines provideded by the architecture manufacturers in current analysis.
The goal is to define a single guideline for the development of iOS and Android apps wirtten in native languages.
The focus it's on app engine of both architectures, starting by data acquisition via an API, presitent storage and displaying data to the end user. The goal it's to make easier the devolpment, support and maintaince of both applications.

Introduction
------------

To do this demostration it will be used a common application as an example that exchanges information with a server through a REST API, and that displays that information to the user. It also store this information so that it can be accessed in offline mode. This same information should be updated whenever the app establishes connection with the server.
The app will be news app that displays a list of articles in the first screen followed by an article detail, when an article is touched. The article detail sould presente the web page in the newspaper website or picture with the title and the discription if the mobile phone is offline.

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

In order to reduce the written code by the developer we will use OPENAPI to describe the requests and the data models from the server, and than generate code with swagger for booth Android and iOS.

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

Starting from the bottom up now it will be described the implementation of each component represented in the picture.

### Room

Android [Room](https://developer.android.com/jetpack/androidx/releases/room?gclid=Cj0KCQjwytOEBhD5ARIsANnRjVg8eMeCTZf2hnkkZja89BGmPTyCxUub-asFIK6KctvYRak8Ba8EjvQaAlj2EALw_wcB&gclsrc=aw.ds.) implements an abstraction layer to make it simple to store persitent data using SQLite.
Room allows you to create tables and fields through annotations that are placed in classes and their properties.
Room will be used in this example to store the server data. Since Swagger is used to generate data models, it will be created one more model to make that same data persistent.

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

The activity or fragment lifecycle implies that both elements can be destroyed and recreated in response to user or operating system actions. Whenever the operating system decides to destroy or recreate an activity it will also destrou all data that is hold by the activity. Because all of this work must be done ina asynchronous tasks it can be painfull to create and destrou those asyncronous task regarding the activity lifecycle.
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

In this example it's written using fragments, but the process is similar in an Activity. The first thing we need to bind the ViewModel with the controller. After the ViewModel initializatiom it must be created an observer that references the `viewLifeCycleOwner`, which in this case represents the Fragment lifecycle. Whit this done the obsever will run whenever the `viewLifeCycleOwner` stays active.

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

iOS Architecture
--------------------

Apple still stand with [MVC](https://developer.apple.com/library/archive/documentation/General/Conceptual/DevPedia-CocoaCore/MVC.html) architecture. And the main reason is because the `UIViewController` is only destroyed when ther is no references pointing to that object. To bring this implementation closer to the Android implementation, it will also be created class repository for aggregating local data and server data.

![iOS app arquitecture](docs/ios-architecture.png "iOS app arquitecture")

### Modelo - CoreData

On Xcode we can create the data model using CoreData designer and we hit `Create NSManagedObject Subclass` on `Editor`the following extension will be generated.

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

`ArticleCache` extension is created and modified by the CoreData designer, so we can modify the class `ArticleCache` to implement the CRUD operations as follows:

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

As we did in Android we will implement the repository class it's responsible to gather data from the server and store it on the local datbase withou duplicating it. When the user opens the app he will see firstly the date store on the mobile and than the data fetched from the server.

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

The `getArticles` function returns the data on `callback` function,  which is triggered whenever there is new data to present. The `callback` is triggered twice, once with the local data, and the after requesting date from the API. Data from the API is first stored on local database and then issued.

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

The data binding with the controller it is now simplified. Now we can acess to the reopsitry after database context is set. And than the UI wait's for the trigger in callbak to update it self.

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

iOS and Android operating systems have a rich documentation that help developers to design high perfomance apps. Despite all the documentation provided, it is not always possible to find a common thread that will help programmers to find methods and good programming practices to develop what we here refer as a common application.
Furthermore, a common application will make sense if it is available on both operating systems, which exacerbates the problem of gathering all the information needed to produce good applications for both operating systems.
Although the code is different for Android and iOS, it is possible to define a very similar base architecture when writing applications for both operating systems. The new programming languages ​​Swift and Kotlin also help, as they have many similarities, starting with the object-oriented programming paradigm, but also due to the new features of modern languages. Currently, the only big difference between both base architectures is the new MVVM proposed by Google that increases the stability level of Android applications.
By adopting a similar base architectureas an architecture, the development and support of applications becomes simplier. Thus, the programmer can concentrate on another fundamental part, which is the development of the application's graphical interface, that should be differnt according to guide lines of each platform, providing the best user exepirence to Android and iOS users.

License & copyright
-------------------

Copyright 2021 Lourenço Gomes

Licensed under [MIT License](LICENSE)
