package com.sanomush.sstoryapp.di


import android.content.Context
import com.sanomush.sstoryapp.data.pref.UserPreference
import com.sanomush.sstoryapp.data.pref.dataStore
import com.sanomush.sstoryapp.data.retrofit.ApiConfig
import com.sanomush.sstoryapp.repository.StoryRepository
import com.sanomush.sstoryapp.repository.UserRepository


object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, pref)
    }
}