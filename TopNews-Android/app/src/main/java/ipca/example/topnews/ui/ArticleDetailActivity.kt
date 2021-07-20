package ipca.example.topnews.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import ipca.example.topnews.R

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

class ArticleDetailActivity : AppCompatActivity() {

    var urlString : String? = null
    var articleTitle : String? = null

    var webView : WebView? = null

    companion object {
        const val ARTICLE_URL   = "article_url"
        const val ARTICLE_TITLE = "article_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)
        urlString = intent.getStringExtra(ARTICLE_URL)
        articleTitle = intent.getStringExtra(ARTICLE_TITLE)

        title = articleTitle

        webView = findViewById(R.id.webViewArticle)

        urlString?.let {
            webView?.loadUrl(it)
        }

        val webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            webView?.webViewClient = webViewClient
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_share -> {
                share()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_article, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun share() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, articleTitle)
        shareIntent.putExtra(Intent.EXTRA_TEXT, urlString)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }




}