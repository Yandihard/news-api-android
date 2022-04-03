package com.yandihard.newsapp.ui.search

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yandihard.newsapp.R
import com.yandihard.newsapp.activity.MainActivity
import com.yandihard.newsapp.adapter.NewsAdapter
import com.yandihard.newsapp.databinding.FragmentSearchBinding
import com.yandihard.newsapp.repository.NewsRepository
import com.yandihard.newsapp.util.Constants
import com.yandihard.newsapp.util.Constants.Companion.SEARCH_TIME_DELAY
import com.yandihard.newsapp.util.Resource
import com.yandihard.newsapp.viewmodel.NewsViewModel
import com.yandihard.newsapp.viewmodel.NewsViewModelProviderFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsRepository: NewsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsRepository = NewsRepository(requireActivity())
        viewModel = NewsViewModel(newsRepository)
        val factory = NewsViewModelProviderFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        setupEditableSearchNews(viewModel)

        setupRecyclerView()
        binding.rvSearch.setHasFixedSize(true)
        getAllNews(viewModel)
        goToArticle()
    }

    private fun setupEditableSearchNews(viewModel: NewsViewModel) {
        hideProgressBar()
        var job: Job? = null
        binding.edtSearch.addTextChangedListener { edit ->
            job?.cancel()
            job = MainScope().launch {
                showProgressBar()
                delay(SEARCH_TIME_DELAY)
                edit.let {
                    if (it.toString().isNotEmpty()) {
                        viewModel.searchNews(it.toString())
                        hideProgressBar()
                    } else if (it.toString().isEmpty()) {
//                        binding.rvSearch.visibility = View.INVISIBLE
                        hideProgressBar()
                    }
                }
            }
        }
    }

    private fun goToArticle() {
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_navigation_search_to_articleFragment, bundle)
        }
    }

    private fun getAllNews(viewModel: NewsViewModel) {
        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data.let {
                        newsAdapter.differ.submitList(it?.articles?.toList())
                        val totalPages = it?.totalResults!! / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvSearch.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        view?.let { it1 ->
                            Snackbar.make(
                                it1,
                                "An error occured $it",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                viewModel.searchNews(binding.edtSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}