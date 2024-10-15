package com.example.newsapp.ui.breaking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.newsapp.repository.NewsRepository

class BreakingNewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    fun getBreakingNews() = newsRepository.getBreakingNews().cachedIn(viewModelScope)
}