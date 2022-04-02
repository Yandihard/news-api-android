package com.yandihard.newsapp.ui.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.yandihard.newsapp.R
import com.yandihard.newsapp.activity.MainActivity
import com.yandihard.newsapp.databinding.FragmentArticleBinding
import com.yandihard.newsapp.model.ArticlesItem
import com.yandihard.newsapp.viewmodel.NewsViewModel
import com.yandihard.newsapp.viewmodel.NewsViewModelProviderFactory

class ArticleFragment : Fragment() {

    private val args: ArticleFragmentArgs by navArgs()
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private var statusFavorite = false
    lateinit var viewModel: NewsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url.toString())
        }

        favoriteCheck(viewModel, article)
        setStatusFavorite(statusFavorite)
        binding.fab.setOnClickListener {
            addToFavorite(viewModel, article)
        }
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        if (statusFavorite) {
            binding.fab.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_favorite_24))
        } else {
            binding.fab.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_unfavorite_24))
        }
    }

    private fun addToFavorite(viewModel: NewsViewModel, article: ArticlesItem) {
        if (!statusFavorite) {
            viewModel.saveNews(article)
            Snackbar.make(requireView(), "Article saved successfully", Snackbar.LENGTH_SHORT).show()
            statusFavorite = !statusFavorite
            setStatusFavorite(statusFavorite)
        } else {
            viewModel.deleteNews(article)
            Snackbar.make(requireView(), "Article has been deleted", Snackbar.LENGTH_SHORT).show()
            statusFavorite = false
            setStatusFavorite(statusFavorite)
        }
    }

    private fun favoriteCheck(viewModel: NewsViewModel, article: ArticlesItem) {
        viewModel.getSavedNews()?.observe(viewLifecycleOwner, {
            for (data in it) {
                if (data.url == article.url) {
                    statusFavorite = true
                    setStatusFavorite(statusFavorite)
                }
            }
        })
    }
}