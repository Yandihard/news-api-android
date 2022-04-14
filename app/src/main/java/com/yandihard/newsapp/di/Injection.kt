package com.yandihard.newsapp.di

import android.app.Application
import android.content.Context
import com.yandihard.newsapp.repository.NewsRepository

object Injection {

    fun provideApplication(): Application {
        return Application()
    }

    fun provideRepository(context: Context): NewsRepository {
        return NewsRepository(context)
    }
}