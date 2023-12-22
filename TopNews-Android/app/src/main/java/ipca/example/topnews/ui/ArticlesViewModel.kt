package ipca.example.topnews.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.swagger.client.models.Articles
import ipca.example.topnews.repository.Repository
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


data class ArticlesUiState(
    val articles: Articles? = null
)

class ArticlesViewModelFactory(
    private val country: String,
    private val category: String): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ArticlesViewModel(country, category) as T
}


class ArticlesViewModel(
    private val country: String,
    private val category: String) : ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch (Dispatchers.IO) {
            updateArticles()
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
            }.onError {

            }.onNetworkError {

            }
            delay(5000) // Update every second
        }
    }

}