package com.yandihard.newsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandihard.newsapp.model.ArticlesItem
import com.yandihard.newsapp.model.NewsResponse
import com.yandihard.newsapp.repository.NewsRepository
import com.yandihard.newsapp.util.Constants.Companion.COUNTRY_CODE
import com.yandihard.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews(COUNTRY_CODE)
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                breakingNewsPage++
                if(breakingNewsResponse == null) {
                    breakingNewsResponse = it
                } else {
                    val oldNews = breakingNewsResponse?.articles
                    val newNews = it.articles
                    oldNews?.addAll(newNews)
                }
                return Resource.Success(breakingNewsResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPage++
                if(searchNewsResponse == null) {
                    searchNewsResponse = it
                } else {
                    val oldNews = searchNewsResponse?.articles
                    val newNews = it.articles
                    oldNews?.addAll(newNews)
                }
                return Resource.Success(searchNewsResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveNews(articlesItem: ArticlesItem) = viewModelScope.launch {
        newsRepository.saveNews(articlesItem)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteNews(articlesItem: ArticlesItem) = viewModelScope.launch {
        newsRepository.deleteNews(articlesItem)
    }
}