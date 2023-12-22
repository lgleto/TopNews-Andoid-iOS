package ipca.example.topnews.repository.localdb

import androidx.room.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import io.swagger.client.models.Article
import ipca.example.topnews.repository.Repository
import java.util.Date

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

@Entity
class ArticleCache( @field:PrimaryKey var url: String, var jsonString: String?, var category: String)

@Dao
interface ArticleCacheDao {

    @Query("SELECT * FROM ArticleCache WHERE category = :category")
    suspend fun getAll(category:String): List<ArticleCache>

    @Query("DELETE FROM ArticleCache WHERE url = :url")
    suspend fun delete(url: String)

    @Query("SELECT * FROM ArticleCache WHERE url = :url")
    suspend fun getByUrl(url: String): ArticleCache

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articleCache: ArticleCache)
}

fun Article.toJsonString() : String {
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
    val adapter: JsonAdapter<Article> = moshi.adapter(Article::class.java)
    return adapter.toJson(this)

}

fun Article.fromJson(jsonString:String?) : Article? {
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
    val adapter: JsonAdapter<Article> = moshi.adapter(Article::class.java)
    return adapter.fromJson(jsonString)
}