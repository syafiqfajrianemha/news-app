package com.emha.latihanrepositoryinjection.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.emha.latihanrepositoryinjection.BuildConfig
import com.emha.latihanrepositoryinjection.data.local.entity.NewsEntity
import com.emha.latihanrepositoryinjection.data.local.room.NewsDao
import com.emha.latihanrepositoryinjection.data.remote.retrofit.ApiService
import com.emha.latihanrepositoryinjection.utils.AppExecutors

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao,
    private val appExecutors: AppExecutors
) {

    private val result = MediatorLiveData<Result<List<NewsEntity>>>()

    fun getHeadlineNews(): LiveData<Result<List<NewsEntity>>> = liveData {

        emit(Result.Loading)

        try {
            val response = apiService.getNews(BuildConfig.API_KEY)
            val articles = response.articles
            val newsList = articles.map { article ->
                val isBookmarked = newsDao.isNewsBookmarked(article.title)
                NewsEntity(
                    article.title,
                    article.publishedAt,
                    article.urlToImage,
                    article.url,
                    isBookmarked
                )
            }
            newsDao.deleteAll()
            newsDao.insertNews(newsList)
        } catch (e: Exception) {
            Log.d("NewsRepository", "getHeadlineNews: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<NewsEntity>>> =
            newsDao.getNews().map { Result.Success(it) }
        emitSource(localData)
    }

    fun getBookmarkedNews(): LiveData<List<NewsEntity>> {
        return newsDao.getBookmarkedNews()
    }

    suspend fun setBookmarkedNews(news: NewsEntity, bookmarkState: Boolean) {
        news.isBookmarked = bookmarkState
        newsDao.updateNews(news)
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao,
            appExecutors: AppExecutors
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService, newsDao, appExecutors)
            }.also { instance = it }
    }
}