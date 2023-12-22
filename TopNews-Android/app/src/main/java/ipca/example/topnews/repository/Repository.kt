package ipca.example.topnews.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import io.swagger.client.apis.NewsApi
import io.swagger.client.models.Articles
import ipca.example.topnews.Globals.NEWS_API_KEY

import ipca.example.topnews.repository.localdb.AppDatabase
import ipca.example.topnews.repository.localdb.ArticleCache

import kotlinx.coroutines.Dispatchers.IO
import java.io.IOException

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

object Repository {

    suspend fun <T> wrap(apiCall: suspend () -> T): ResultWrapper<T> {
        return try {
            ResultWrapper.Success(apiCall())
        } catch (throwable: Throwable) {
            Log.e("Repository", throwable.message.toString())
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                else -> {
                    ResultWrapper.Error(0, throwable.message)
                }
            }
        }
    }

    suspend fun fetchArticles(country : String, category: String) : ResultWrapper<Articles> =
        wrap { NewsApi().topHeadlinesGet(country, category, NEWS_API_KEY) }


}

