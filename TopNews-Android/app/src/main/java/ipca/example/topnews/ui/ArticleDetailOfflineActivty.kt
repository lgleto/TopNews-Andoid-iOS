package ipca.example.topnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.swagger.client.models.Article
import ipca.example.topnews.R

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//


class ArticleDetailOfflineActivty : AppCompatActivity() {


    companion object {
        const val ARTICLE_JSON = "article_json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail_offline)

        val textViewOffLineTitle : TextView = findViewById(R.id.textViewOffLineTitle)
        val textViewOfflineDescription : TextView = findViewById(R.id.textViewOfflineDescription)
        val imageViewOfflineImage : ImageView = findViewById(R.id.imageViewOfflineImage)

        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<Article> = moshi.adapter(Article::class.java)


        val articleJson = intent.getStringExtra(ARTICLE_JSON)

        adapter.fromJson(articleJson)?.apply {
            textViewOffLineTitle.text = title
            textViewOfflineDescription.text = description
            Glide.with(this@ArticleDetailOfflineActivty).load(urlToImage).into(imageViewOfflineImage)
        }



    }
}