package com.example.newsapp.di

import android.content.Context
import com.example.newsapp.data.local.room.NewsDatabase
import com.example.newsapp.data.network.api.ApiConfig
import com.example.newsapp.repository.NewsRepository

object Injection {
    fun provideRepository(context: Context): NewsRepository{
        val apiService = ApiConfig.getApiService()
        val database = NewsDatabase.getDatabase(context)
        return NewsRepository.getInstance(apiService,database)
    }
}