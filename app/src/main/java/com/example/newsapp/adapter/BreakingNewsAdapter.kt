package com.example.newsapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.data.local.model.NewsEntity
import com.example.newsapp.databinding.ItemNewsBinding
import com.example.newsapp.ui.detail.DetailActivity

class BreakingNewsAdapter:
    PagingDataAdapter<NewsEntity, BreakingNewsAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemNewsBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewsEntity){
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.tvSource.text = item.sourceName
            binding.tvPublishedAt.text = item.publishedAt
            Glide.with(itemView.context)
                .load(item.urlToImage)
                .into(binding.ivArticleImage)

            itemView.setOnClickListener{
                val goDetail = Intent(itemView.context, DetailActivity::class.java)
                goDetail.putExtra(DetailActivity.EXTRA_NEWS, item)
                itemView.context.startActivity(goDetail)
                Log.d("BreakingNewsAdapter","Data $goDetail")
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        if (news != null) {
            holder.bind(news)
        }else{
            Log.e("BreakingNewsAdapter", "News item at position $position is null")
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsEntity>() {
            override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}