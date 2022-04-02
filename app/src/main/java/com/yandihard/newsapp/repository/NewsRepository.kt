package com.yandihard.newsapp.repository

import android.content.Context
import com.yandihard.newsapp.db.ArticleDao
import com.yandihard.newsapp.db.ArticleDatabase
import com.yandihard.newsapp.model.ArticlesItem
import com.yandihard.newsapp.remote.ApiConfig

class NewsRepository(context: Context) {
    private val mNewsDao: ArticleDao?

    init {
        val db = ArticleDatabase(context)
        mNewsDao = db.getArticleDao()
    }

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        ApiConfig.getApiService().getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        ApiConfig.getApiService().getSearchNews(searchQuery, pageNumber)

    suspend fun saveNews(articlesItem: ArticlesItem) = mNewsDao?.saveArticle(articlesItem)

    fun getSavedNews() = mNewsDao?.getAllArticles()

    suspend fun deleteNews(articlesItem: ArticlesItem) = mNewsDao?.deleteArticle(articlesItem)
}