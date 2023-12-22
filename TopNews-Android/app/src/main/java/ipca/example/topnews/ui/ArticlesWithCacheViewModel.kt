package ipca.example.topnews.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.swagger.client.models.Article
import io.swagger.client.models.Articles
import ipca.example.topnews.repository.Repository
import ipca.example.topnews.repository.localdb.AppDatabase
import ipca.example.topnews.repository.localdb.ArticleCache
import ipca.example.topnews.repository.localdb.fromJson
import ipca.example.topnews.repository.localdb.toJsonString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//


data class ArticlesWithCacheUiState(
    val articles: Articles? = null
)

class ArticlesWithCacheViewModelFactory(
    private val context: Context,
    private val country: String,
    private val category: String): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ArticlesWithCacheViewModel(context, country, category) as T
}


class ArticlesWithCacheViewModel(
    private val context: Context,
    private val country: String,
    private val category: String) : ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch (Dispatchers.IO) {
            updateArticles()
            val articles = getCachedArticles(context, category)
            viewModelScope.launch (Dispatchers.Main) {
                _uiState.update { currentState ->
                    currentState.copy(
                        articles = articles
                    )
                }
            }
        }
    }

    private suspend fun updateArticles() {
        while (true) {
            Repository.fetchArticles(country, category).onSuccess {
                viewModelScope.launch(Dispatchers.Main) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            articles = it
                        )
                    }
                }
                it.articles?.forEach { article ->
                    article.url?.let { url ->
                        val articleCache = ArticleCache(url, article.toJsonString(), category)
                        AppDatabase.getDatabase(context)?.articleCacheDao()?.insert(articleCache)
                    }
                }
            }.onError {

            }.onNetworkError {

            }
            delay(5000) // Update every second
        }
    }

    suspend fun getCachedArticles(context: Context, category: String) : Articles {
        val articles = Articles()
        val totalArticlesCached = AppDatabase.getDatabase(context)?.articleCacheDao()?.getAll(category)
        val articlesLocal = arrayListOf<Article>()
        totalArticlesCached?.let {
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

}