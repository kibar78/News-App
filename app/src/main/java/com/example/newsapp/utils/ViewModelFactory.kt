package com.example.newsapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.di.Injection
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.ui.breaking.BreakingNewsViewModel
import com.example.newsapp.ui.favorite.FavoriteViewModel
import com.example.newsapp.ui.search.SearchViewModel

class ViewModelFactory private constructor(private val newsRepository: NewsRepository):
    ViewModelProvider.NewInstanceFactory(){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T{
            return when{
                modelClass.isAssignableFrom(BreakingNewsViewModel::class.java)->{
                    BreakingNewsViewModel(newsRepository)as T
                }
                modelClass.isAssignableFrom(FavoriteViewModel::class.java)->{
                    FavoriteViewModel(newsRepository) as T
                }
                modelClass.isAssignableFrom(SearchViewModel::class.java)->{
                    SearchViewModel(newsRepository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }
    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}