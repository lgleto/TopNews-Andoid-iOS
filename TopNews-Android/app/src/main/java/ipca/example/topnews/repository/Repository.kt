package ipca.example.topnews.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import ipca.example.topnews.repository.backend.models.Article
import ipca.example.topnews.repository.backend.models.Articles
import ipca.example.topnews.repository.localdb.AppDatabase
import ipca.example.topnews.repository.localdb.ArticleCache
import ipca.example.topnews.repository.localdb.fromJson
import ipca.example.topnews.repository.localdb.toJsonString
import kotlinx.coroutines.Dispatchers.IO

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

object Repository {
    private val backend = Backend()

    suspend fun getCachedArticles(context: Context, category: String) : Articles {
        val articles = Articles()

        val totalArticlesCached = AppDatabase.getDatabase(context)?.articleCacheDao()?.getAll(category)
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

    fun getArticles( category: String, country: String, apiKey: String, context: Context): LiveData<Articles> = liveData(IO) {
        emit( getCachedArticles(context, category) )
        try {
            val serverArticles = backend.newsApi.topHeadlinesGet( country, category, apiKey)
            serverArticles.articles?.forEach { article ->
                article.url?.let { url ->
                    val articleCache = ArticleCache(url, article.toJsonString(), category)
                    AppDatabase.getDatabase(context)?.articleCacheDao()?.insert(articleCache)
                }
                emit( getCachedArticles(context, category) )
            }
        } catch (throwable: Throwable) {
            Log.e("Repository", throwable.toString())
        }
    }

}

