package com.emha.latihanrepositoryinjection.di

import android.content.Context
import com.emha.latihanrepositoryinjection.data.NewsRepository
import com.emha.latihanrepositoryinjection.data.local.room.NewsDatabase
import com.emha.latihanrepositoryinjection.data.remote.retrofit.ApiConfig
import com.emha.latihanrepositoryinjection.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): NewsRepository {
        val apiService = ApiConfig.getApiService()
        val database = NewsDatabase.getInstance(context)
        val dao = database.newsDao()
        val appExecutors = AppExecutors()
        return NewsRepository.getInstance(apiService, dao, appExecutors)
    }
}