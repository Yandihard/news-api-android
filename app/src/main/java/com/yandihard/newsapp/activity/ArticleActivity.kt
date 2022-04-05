package com.yandihard.newsapp.activity

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.yandihard.newsapp.databinding.ActivityArticleBinding
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

        setSupportActionBar(binding.toolbar)
        assert(supportActionBar != null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        newsRepository = NewsRepository(this)
        viewModel = NewsViewModel(newsRepository)
        val factory = NewsViewModelProviderFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url.toString())
        }

        binding.tvTitle.text = article.title
        binding.tvSubTitle.text = article.url
    }
}