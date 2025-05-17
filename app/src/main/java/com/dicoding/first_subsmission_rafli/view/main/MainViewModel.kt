package com.dicoding.first_subsmission_rafli.view.main

import androidx.lifecycle.LiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.first_subsmission_rafli.data.preference.StoryModel
import com.dicoding.first_subsmission_rafli.data.response.ListStoryItem
import com.dicoding.first_subsmission_rafli.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel (private val storyRepository: StoryRepository) : ViewModel() {

    val pagedStories: LiveData<PagingData<ListStoryItem>> = storyRepository.getPagedStories2().cachedIn(viewModelScope)

    fun getPaged(): Flow<PagingData<ListStoryItem>> {
        return storyRepository.getPagedStories().cachedIn(viewModelScope)
    }

    fun getSession(): LiveData<StoryModel> {
        return storyRepository.getSession().asLiveData()
    }

    fun getStories() = storyRepository.getStory()

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }
}