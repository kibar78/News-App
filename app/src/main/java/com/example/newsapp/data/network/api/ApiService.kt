package com.example.newsapp.data.network.api

import com.example.newsapp.data.network.api.ApiConfig.Companion.API_KEY
import com.example.newsapp.data.network.response.BreakingNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode:String = "us",
        @Query("page")
        page: Int = 1,
        @Query("pageSize")
        size: Int = 20,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): BreakingNewsResponse

}