package com.example.newsapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.newsapp.data.local.model.NewsEntity
import com.example.newsapp.data.local.room.NewsDatabase
import com.example.newsapp.data.mediator.NewsRemoteMediator
import com.example.newsapp.data.network.api.ApiService

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDatabase: NewsDatabase
){
    fun getBreakingNews(): LiveData<PagingData<NewsEntity>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            remoteMediator = NewsRemoteMediator(newsDatabase, apiService),
            pagingSourceFactory ={
                newsDatabase.newsDao().getAllNews()
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(
            apiService: ApiService,
            newsDatabase: NewsDatabase
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService, newsDatabase)
            }.also { instance = it }
    }
}