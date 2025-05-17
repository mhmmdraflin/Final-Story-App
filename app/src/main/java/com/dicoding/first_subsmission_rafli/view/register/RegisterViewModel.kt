package com.dicoding.first_subsmission_rafli.view.register

import androidx.lifecycle.ViewModel
import com.dicoding.first_subsmission_rafli.repository.StoryRepository

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}