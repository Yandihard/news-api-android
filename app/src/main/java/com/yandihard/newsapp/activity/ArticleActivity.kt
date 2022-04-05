package com.yandihard.newsapp.activity

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.yandihard.newsapp.R
import com.yandihard.newsapp.databinding.ActivityArticleBinding
import com.yandihard.newsapp.model.ArticlesItem
import com.yandihard.newsapp.repository.NewsRepository
import com.yandihard.newsapp.viewmodel.NewsViewModel
import com.yandihard.newsapp.viewmodel.NewsViewModelProviderFactory

class ArticleActivity : AppCompatActivity() {
    private val args: ArticleActivityArgs by navArgs()
    private lateinit var binding: ActivityArticleBinding
    private var statusFavorite = false
    lateinit var viewModel: NewsViewModel
    lateinit var newsRepository: NewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        newsRepository = NewsRepository(applicationContext)
        viewModel = NewsViewModel(newsRepository)
        val factory = NewsViewModelProviderFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        val article = args.article
        bindingArticle(article)

        favoriteCheck(viewModel, article)
        setStatusFavorite(statusFavorite)
        binding.imageFavorite.setOnClickListener {
            addToFavorite(viewModel, article)
        }
    }

    private fun bindingArticle(article: ArticlesItem) {
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url.toString())
        }

        binding.tvTitle.text = article.title
        binding.tvSubTitle.text = article.url
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        if (statusFavorite) {
            binding.imageFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bookmark))
        } else {
            binding.imageFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_unbookmark))
        }
    }

    private fun addToFavorite(viewModel: NewsViewModel, article: ArticlesItem) {
        if (!statusFavorite) {
            viewModel.saveNews(article)
            Snackbar.make(binding.root, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
            statusFavorite = !statusFavorite
            setStatusFavorite(statusFavorite)
        } else {
            viewModel.deleteNews(article)
            Snackbar.make(binding.root, "Article has been deleted", Snackbar.LENGTH_SHORT).show()
            statusFavorite = false
            setStatusFavorite(statusFavorite)
        }
    }

    private fun favoriteCheck(viewModel: NewsViewModel, article: ArticlesItem) {
        viewModel.getSavedNews()?.observe(this) {
            val listArticleItems: MutableList<String> = mutableListOf()
            it.forEach { item ->
                item.url?.let { listArticleItems.add(it) }

                if (listArticleItems.contains(article.url)) {
                    statusFavorite = true
                    setStatusFavorite(statusFavorite)
                }
            }
        }
    }
}