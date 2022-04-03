package com.yandihard.newsapp.ui.home

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yandihard.newsapp.R
import com.yandihard.newsapp.activity.MainActivity
import com.yandihard.newsapp.adapter.NewsAdapter
import com.yandihard.newsapp.databinding.FragmentHomeBinding
import com.yandihard.newsapp.repository.NewsRepository
import com.yandihard.newsapp.util.Constants.Companion.COUNTRY_CODE
import com.yandihard.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.yandihard.newsapp.util.Resource.*
import com.yandihard.newsapp.viewmodel.NewsViewModel
import com.yandihard.newsapp.viewmodel.NewsViewModelProviderFactory

class HomeFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsRepository: NewsRepository


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsRepository = NewsRepository(requireActivity())
        viewModel = NewsViewModel(newsRepository)
        val factory = NewsViewModelProviderFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        setupRecyclerView()
        binding.rvHome.setHasFixedSize(true)
        getAllNews(viewModel)
        goToArticle()
    }

    private fun goToArticle() {
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_navigation_home_to_articleFragment, bundle)
        }
    }

    private fun getAllNews(viewModel: NewsViewModel) {
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Success -> {
                    hideProgressBar()
                    response.data.let {
                        newsAdapter.differ.submitList(it?.articles?.toList())
                        val totalPages = it?.totalResults!! / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvHome.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Error -> {
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
                is Loading -> {
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                viewModel.getBreakingNews(COUNTRY_CODE)
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvHome.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            addOnScrollListener(this@HomeFragment.scrollListener)
        }
    }
}