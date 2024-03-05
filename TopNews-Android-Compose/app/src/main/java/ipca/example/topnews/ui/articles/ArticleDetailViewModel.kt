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

data class ArticleState(
    var url: String? = null,
    var title: String? = null,
    val error : String = "",
    val isLoading : Boolean = false,
)

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    val repository: Repository,
    val savedStateHandle: SavedStateHandle) : ViewModel() {


    private val _state = mutableStateOf<ArticleState>(ArticleState())
    val state : State<ArticleState> = _state

}