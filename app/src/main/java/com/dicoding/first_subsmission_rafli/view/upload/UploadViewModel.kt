package com.dicoding.first_subsmission_rafli.view.upload

import androidx.lifecycle.ViewModel
import com.dicoding.first_subsmission_rafli.repository.StoryRepository
import java.io.File

class UploadViewModel (private val repository: StoryRepository) : ViewModel() {
    fun uploadImage(file: File, description: String) = repository.uploadStory(file, description)

    fun uploadImageWithLocation(file: File, description: String, latitude: Double, longitude: Double) =
        repository.uploadStoryWithLocation(file, description, latitude, longitude)
}