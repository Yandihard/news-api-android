package com.yandihard.newsapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
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
        binding.progressBar.max = 100

        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        newsRepository = NewsRepository(applicationContext)
        viewModel = NewsViewModel(newsRepository)
        val factory = NewsViewModelProviderFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        val article = args.article
        //show the news
        showNewsArticle(article)

        //checked has been favorited or not
        favoriteCheck(viewModel, article)
        setStatusFavorite(statusFavorite)

        //act to add favorite a news
        binding.imageFavorite.setOnClickListener { addToFavorite(viewModel, article) }

        //share a news
        binding.imageShare.setOnClickListener { actToShare(article) }
    }

    private fun actToShare(article: ArticlesItem) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        share.putExtra(Intent.EXTRA_TEXT, article.url)
        startActivity(Intent.createChooser(share, "Bagikan ke : "))
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showNewsArticle(article: ArticlesItem) {
        binding.webView.apply {
            settings.loadsImagesAutomatically = true
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
        binding.progressBar.progress = 0

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, newUrl: String): Boolean {
                view.loadUrl(newUrl)
                binding.progressBar.progress = 0
                return true
            }

            override fun onPageStarted(view: WebView, urlStart: String, favicon: Bitmap?) {
                article.url = urlStart
                invalidateOptionsMenu()
            }

            override fun onPageFinished(view: WebView, urlPage: String) {
                binding.progressBar.visibility = View.GONE
                invalidateOptionsMenu()
            }
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
                item.url.let { listArticleItems.add(it) }

                if (listArticleItems.contains(article.url)) {
                    statusFavorite = true
                    setStatusFavorite(statusFavorite)
                }
            }
        }
    }
}