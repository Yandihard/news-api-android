package com.yandihard.newsapp.activity

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandihard.newsapp.R
import com.yandihard.newsapp.databinding.ActivityMainBinding
import com.yandihard.newsapp.repository.NewsRepository
import com.yandihard.newsapp.viewmodel.NewsViewModel
import com.yandihard.newsapp.viewmodel.NewsViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NewsViewModel
    private val newsRepository = NewsRepository(Application())

//    init {
//        viewModel = NewsViewModel(newsRepository)
//    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val newsRepository = NewsRepository(this)
        viewModel = NewsViewModel(newsRepository)
        val factory = NewsViewModelProviderFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_search, R.id.navigation_favorite))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

//    override fun onBackPressed() {
//        val fragment = this.supportFragmentManager.findFragmentById(R.id.articleFragment)
//        (fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
//            super.onBackPressed()
//        }
//    }
}