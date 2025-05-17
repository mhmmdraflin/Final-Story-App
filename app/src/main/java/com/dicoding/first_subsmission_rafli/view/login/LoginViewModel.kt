package com.dicoding.first_subsmission_rafli.view.login

import androidx.lifecycle.ViewModel
import com.dicoding.first_subsmission_rafli.data.preference.StoryModel
import com.dicoding.first_subsmission_rafli.repository.StoryRepository

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    suspend fun saveSession(Model: StoryModel) {
        repository.saveSession(Model)
    }
}