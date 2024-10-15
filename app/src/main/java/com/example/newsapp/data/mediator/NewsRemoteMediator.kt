package com.example.newsapp.data.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapp.data.local.model.NewsEntity
import com.example.newsapp.data.local.room.NewsDatabase
import com.example.newsapp.data.local.room.RemoteKeys
import com.example.newsapp.data.network.api.ApiService

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val newsDatabase: NewsDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, NewsEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        try {
            val responseData =
                apiService.getBreakingNews("us", page, state.config.pageSize).articles
            Log.d("NewsRemoteMediator", "Response Data: $responseData")
            val endOfPaginationReached = responseData.isEmpty()

            newsDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    newsDatabase.remoteKeysDao().deleteRemoteKeys()
                    newsDatabase.newsDao().deleteAllNews()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemoteKeys(id = it.source.name, prevKey = prevKey, nextKey = nextKey)
                }
                Log.d("NewsRemoteMediator", "Remote Keys: $keys")
                newsDatabase.remoteKeysDao().insertAll(keys)
                val newsEntityMap = responseData
                    .filter { it.urlToImage != null && it.description != null }
                    .map {
                    with(it) {
                            NewsEntity(
                                source.name,
                                title,
                                description,
                                urlToImage,
                                publishedAt,
                                source.name
                            )
                    }
                }
                newsDatabase.newsDao().insertNews(newsEntityMap)
                Log.d("NewsRemoteMediator", "News inserted: $newsEntityMap")
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, NewsEntity>): RemoteKeys?{
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            newsDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, NewsEntity>): RemoteKeys?{
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            newsDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, NewsEntity>): RemoteKeys?{
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                newsDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}