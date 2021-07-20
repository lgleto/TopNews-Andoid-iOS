package ipca.example.topnews.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ipca.example.topnews.Globals
import ipca.example.topnews.repository.Repository
import ipca.example.topnews.repository.backend.models.Articles


//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

class ArticlesViewModel() : ViewModel() {

    private val _category: MutableLiveData<String> = MutableLiveData()

    val articlesForCategory: LiveData<Articles> =
        Transformations.switchMap(_category) {
            Repository.getArticles( it,Globals.COUNTRY, Globals.NEWS_API_KEY, context!!)
        }

    private var context : Context? = null

    fun setCategory(category: String, context: Context) {
        this.context = context
        if (_category.value == category) return
        _category.value = category
    }

}