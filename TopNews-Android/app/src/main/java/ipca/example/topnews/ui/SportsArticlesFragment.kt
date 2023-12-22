package ipca.example.topnews.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import ipca.example.topnews.*
import io.swagger.client.models.Article
import kotlinx.coroutines.launch

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

class SportsArticlesFragment : Fragment() {

    private var articles : List<Article> = arrayListOf()

    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: LinearLayoutManager? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_articles, container, false)

        mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerViewArticles)
        recyclerView.layoutManager = mLayoutManager
        mAdapter = RecyclerViewArticlesAdapter()
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter

        return root
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: ArticlesWithCacheViewModel by viewModels {
            ArticlesWithCacheViewModelFactory(requireContext(), Globals.COUNTRY, Globals.ENDPOINT_BUSINESS)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    uiState.articles?.articles?.let { articlesList ->
                        articles = articlesList.sortedByDescending {
                            it.publishedAt
                        }
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    inner class RecyclerViewArticlesAdapter : RecyclerView.Adapter<RecyclerViewArticlesAdapter.ViewHolder>() {

        inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_article, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return articles.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.v.apply {
                val textViewTitle       = findViewById<TextView>(R.id.textViewRowTitle)
                val textViewDescription = findViewById<TextView>(R.id.textViewRowDescription)
                val imageViewArticle    = findViewById<ImageView>(R.id.imageViewRowAtricle)
                val textViewRowDate     = findViewById<TextView>(R.id.textViewRowDate)
                articles[position].apply {
                    textViewTitle.text = title
                    textViewDescription.text = description
                    textViewRowDate.text = publishedAt?.toDate()?.toDateTimeString()
                    Glide.with(holder.v).load(urlToImage).into(imageViewArticle)
                }
                isClickable = true
                setOnClickListener {
                    if (isNetworkConnected(requireContext())) {
                        val intent = Intent (requireContext(), ArticleDetailActivity::class.java)
                        intent.putExtra(ArticleDetailActivity.ARTICLE_URL   , articles[position].url  )
                        intent.putExtra(ArticleDetailActivity.ARTICLE_TITLE , articles[position].title)
                        startActivity(intent)
                    }else{
                        val moshi: Moshi = Moshi.Builder().build()
                        val adapter: JsonAdapter<Article> = moshi.adapter(Article::class.java)
                        val intent = Intent (requireContext(), ArticleDetailOfflineActivty::class.java)
                        intent.putExtra(ArticleDetailOfflineActivty.ARTICLE_JSON   , adapter.toJson(articles[position]).toString()  )
                        startActivity(intent)
                    }
                }
            }
        }
    }


}