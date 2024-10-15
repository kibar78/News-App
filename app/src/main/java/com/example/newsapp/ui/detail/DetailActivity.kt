package com.example.newsapp.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newsapp.data.local.model.NewsEntity
import com.example.newsapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    companion object{
        const val EXTRA_NEWS = "news"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getNews = intent.getParcelableExtra<NewsEntity>(EXTRA_NEWS)

        if (getNews != null) {
            Glide.with(this)
                .load(getNews.urlToImage)
                .into(binding.imgView)
            binding.tvTitle.text = getNews.title.toString()
            binding.tvDescription.text = getNews.description.toString()
            binding.tvAuthor.text = getNews.sourceName.toString()
            binding.tvPublishedAt.text = getNews.publishedAt.toString()
            Log.d("DetailActivity","Success: $getNews")
        }else{
            Log.d("DetailActivity","Failed: $getNews")
        }
    }
}