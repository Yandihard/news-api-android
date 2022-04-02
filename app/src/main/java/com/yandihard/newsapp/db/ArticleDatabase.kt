package com.yandihard.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yandihard.newsapp.model.ArticlesItem
import com.yandihard.newsapp.util.Converter

@Database(entities = [ArticlesItem::class], version = 1)
@TypeConverters(Converter::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {

        @Volatile
        private var INSTANCE: ArticleDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: createDatabase(context).also { INSTANCE = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_news.db"
            ).build()
    }
}