package com.example.newsapp.data.local.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "news")
@Parcelize
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String? = null,
    val description: String? = null,
    val urlToImage: String? = null,
    val publishedAt: String? = null,
    val sourceName: String? = null
): Parcelable