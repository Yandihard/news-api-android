package com.yandihard.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yandihard.newsapp.R
import com.yandihard.newsapp.databinding.ItemRowArticleBinding
import com.yandihard.newsapp.model.ArticlesItem

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<ArticlesItem>() {
        override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val binding = ItemRowArticleBinding.bind(holder.itemView)
        val news = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(news.urlToImage).into(binding.newsImage)
            binding.tvHeadline.text = news.title
            binding.tvContent.text = news.description
            setOnClickListener {
                onItemClickListener?.let { it(news) }
            }
        }
    }

    private var onItemClickListener: ((ArticlesItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (ArticlesItem) -> Unit) {
        onItemClickListener = listener
    }
}