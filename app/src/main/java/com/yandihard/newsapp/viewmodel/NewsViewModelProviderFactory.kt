package com.yandihard.newsapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yandihard.newsapp.di.Injection
import com.yandihard.newsapp.repository.NewsRepository

class NewsViewModelProviderFactory private constructor(private val newsRepository: NewsRepository) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: NewsViewModelProviderFactory? = null

        fun getInstance(context: Context): NewsViewModelProviderFactory =
            instance ?: synchronized(this) {
                instance ?: NewsViewModelProviderFactory(Injection.provideRepository(context))
            }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository) as T
    }
}