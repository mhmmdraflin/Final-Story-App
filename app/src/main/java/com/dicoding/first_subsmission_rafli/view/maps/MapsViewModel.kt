package com.dicoding.first_subsmission_rafli.view.maps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.first_subsmission_rafli.Result.StoryResult
import com.dicoding.first_subsmission_rafli.data.preference.StoryPreference
import com.dicoding.first_subsmission_rafli.data.preference.dataStore
import com.dicoding.first_subsmission_rafli.data.response.ListStoryItem
import com.dicoding.first_subsmission_rafli.data.retrofit.ApiConfig
import com.dicoding.first_subsmission_rafli.data.retrofit.ApiService
import com.dicoding.first_subsmission_rafli.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    private val storyPreference = StoryPreference.getInstance(application.dataStore)

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    init {

        viewModelScope.launch {
            val userSession = storyPreference.getSession().first()
            _token.postValue(userSession.token)
        }
    }

    private val repository: StoryRepository
        get() {

            val tokenValue = _token.value ?: return StoryRepository.getInstance(
                ApiConfig.getApiService(""),
                storyPreference
            )

            return StoryRepository.getInstance(
                ApiConfig.getApiService(tokenValue),
                storyPreference
            )
        }

    fun getStoriesWithLocation(): LiveData<StoryResult<List<ListStoryItem>>> {
        return repository.getStoriesWithLocation()
    }
}