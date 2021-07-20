package ipca.example.topnews.repository

import ipca.example.topnews.repository.backend.apis.NewsApi
import ipca.example.topnews.repository.backend.tools.GeneratedCodeConverters
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

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

    val newsApi: NewsApi by lazy { retrofit.create(NewsApi:: class.java) }



}