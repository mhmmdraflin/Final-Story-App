package com.dicoding.first_subsmission_rafli.di

import android.content.Context
import com.dicoding.first_subsmission_rafli.data.preference.StoryPreference
import com.dicoding.first_subsmission_rafli.data.preference.dataStore
import com.dicoding.first_subsmission_rafli.data.retrofit.ApiConfig
import com.dicoding.first_subsmission_rafli.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = StoryPreference.getInstance(context.dataStore)
        val story = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(story.token)
        return StoryRepository.getInstance(apiService, pref)
    }
}