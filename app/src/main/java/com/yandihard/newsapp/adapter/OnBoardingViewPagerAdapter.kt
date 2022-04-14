package com.yandihard.newsapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.yandihard.newsapp.R
import com.yandihard.newsapp.databinding.OnBoardingScreenLayoutBinding
import com.yandihard.newsapp.model.OnBoardingData

class OnBoardingViewPagerAdapter(private val context: Context, private var onBoardingDataList: List<OnBoardingData>) : PagerAdapter() {
    override fun getCount(): Int {
        return onBoardingDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.on_boarding_screen_layout, null)
        val binding = OnBoardingScreenLayoutBinding.bind(view)

        binding.imageUrl.setImageResource(onBoardingDataList[position].imageUrl)
        binding.tvTitle.text = onBoardingDataList[position].title
        binding.tvDesc.text = onBoardingDataList[position].desc

        container.addView(view)
        return view
    }
}