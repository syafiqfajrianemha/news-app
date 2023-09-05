package com.emha.latihanrepositoryinjection.data.remote.retrofit

import com.emha.latihanrepositoryinjection.data.remote.response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // &category=science
    @GET("top-headlines?country=us")
    suspend fun getNews(
        @Query("apiKey") apiKey: String
    ): NewsResponse
}