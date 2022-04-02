package com.yandihard.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yandihard.newsapp.model.ArticlesItem

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticle(articlesItem: ArticlesItem): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<ArticlesItem>>

    @Delete
    suspend fun deleteArticle(articlesItem: ArticlesItem)
}