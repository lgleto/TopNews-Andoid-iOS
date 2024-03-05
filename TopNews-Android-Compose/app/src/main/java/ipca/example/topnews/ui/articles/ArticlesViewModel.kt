package ipca.example.topnews.ui.articles

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.swagger.client.models.Articles
import kotlinx.coroutines.flow.launchIn

import kotlinx.coroutines.flow.onEach
import ipca.example.topnews.repository.Repository
import ipca.example.topnews.repository.ResultWrapper
import javax.inject.Inject

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

data class ArticlesState(
    val articles: Articles? = null,
    val error : String = "",
    val isLoading : Boolean = false,
)

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    val repository: Repository,
    val savedStateHandle: SavedStateHandle) : ViewModel() {


    companion object{
        const val PARAM_COUNTRY = "country"
        const val PARAM_CATEGORY = "category"
    }

    private val _state = mutableStateOf<ArticlesState>(ArticlesState())
    val state : State<ArticlesState> = _state

    init {
        savedStateHandle.get<String>(PARAM_CATEGORY)?.let { category ->
            getArticles("us", category)
        }?:run {
            getArticles("us", "general")
        }

    }

    private fun getArticles(country :String, category: String) {
        repository.fetchArticles( country, category).onEach{ result->
            when(result){
                is ResultWrapper.Success -> {
                    _state.value = ArticlesState( articles = result.data )
                }
                is ResultWrapper.Loading ->{
                    _state.value = ArticlesState( isLoading = true)
                }
                is ResultWrapper.Error->{
                    _state.value = ArticlesState( error = result.message ?: "unexpected")
                }
            }
        }.launchIn(viewModelScope)
    }

}