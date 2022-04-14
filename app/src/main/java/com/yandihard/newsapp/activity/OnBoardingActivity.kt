package com.yandihard.newsapp.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.yandihard.newsapp.R
import com.yandihard.newsapp.adapter.OnBoardingViewPagerAdapter
import com.yandihard.newsapp.databinding.ActivityOnBoardingBinding
import com.yandihard.newsapp.model.OnBoardingData

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (restorePrefData()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnBoardingVPAdapter(generateDummyBoarding())
        setTabNextSelected()
    }

    private fun setOnBoardingVPAdapter(onBoardingData: List<OnBoardingData>) {
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        binding.screenPager.adapter = onBoardingViewPagerAdapter
        binding.tabLayoutIndicator.setupWithViewPager(binding.screenPager)
    }

    private fun setTabNextSelected() {
        var position = binding.screenPager.currentItem
        binding.next.setOnClickListener {
            if (position < generateDummyBoarding().size) {
                position++
                binding.screenPager.currentItem = position
            }

            if (position == generateDummyBoarding().size) {
                savePrefData()
                if (restorePrefData()) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.tabLayoutIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) { position = tab.position }

                if (tab?.position == generateDummyBoarding().size -1) {
                    binding.next.text = "Get Started"
                } else {
                    binding.next.text = "Next"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun savePrefData() {
        preferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        editor = preferences.edit()
        editor.putBoolean("isFirstTime", true)
        editor.apply()
    }

    private fun restorePrefData() : Boolean {
        preferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return preferences.getBoolean("isFirstTime", false)
    }



    private fun generateDummyBoarding(): List<OnBoardingData> {
        val boardingData = ArrayList<OnBoardingData>()

        boardingData.add(OnBoardingData("Welcome to the news api app", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident.", R.drawable.onboarding01))
        boardingData.add(OnBoardingData("Get latest a news update", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur, Excepteur sint occaecat cupidatat non proident.", R.drawable.onboarding02))
        boardingData.add(OnBoardingData("Trusted and comprehensive", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident,", R.drawable.onboarding03))
        return boardingData
    }
}