package ipca.example.topnews.repository

import android.content.Context
import io.swagger.client.apis.NewsApi
import io.swagger.client.models.Article
import io.swagger.client.models.Articles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ipca.example.topnews.Globals.NEWS_API_KEY
import ipca.example.topnews.repository.localdb.AppDatabase
import ipca.example.topnews.repository.localdb.ArticleCache
import ipca.example.topnews.repository.localdb.fromJson
import ipca.example.topnews.repository.localdb.toJsonString
import retrofit2.HttpException

import java.io.IOException
import javax.inject.Inject

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

class Repository @Inject constructor(
    private val newsApi: NewsApi,
    private val db:AppDatabase
){

    private suspend fun getCachedArticles(category: String) : Articles {
        val articles = Articles()

        val totalArticlesCached = db.articleCacheDao().getAll(category)
        val articlesLocal : MutableList<Article> = arrayListOf()
        totalArticlesCached.let {
            for (articleCached in it) {
                val article = Article().fromJson(articleCached.jsonString)
                article?.let { it1 -> articlesLocal.add(it1) }
            }
        }

        articles.articles = articlesLocal.toTypedArray()
        articles.status = "local"
        articles.totalResults = articlesLocal.size

        return articles
    }

    fun fetchArticles(country : String, category: String) : Flow<ResultWrapper<Articles>> =
       flow {
           try {
               emit(ResultWrapper.Loading())
               var articles : Articles = getCachedArticles(category)
               emit(ResultWrapper.Success(articles))

               articles = newsApi.topHeadlinesGet(country, category, NEWS_API_KEY)
               articles.articles?.forEach { article ->
                   article.url?.let { url ->
                       val articleCache = ArticleCache(url, article.toJsonString(), category)
                       db.articleCacheDao().insert(articleCache)
                   }
               }
               emit(ResultWrapper.Success(articles))
           }catch (e : HttpException){
               emit(ResultWrapper.Error(e.localizedMessage?:"Unexpected Error"))
           }catch (e: IOException){
               emit(ResultWrapper.Error("No internet connection"))
           }
       }.flowOn(Dispatchers.IO)


}

