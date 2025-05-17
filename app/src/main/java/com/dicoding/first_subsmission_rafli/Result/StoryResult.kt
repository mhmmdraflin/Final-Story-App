package com.dicoding.first_subsmission_rafli.Result

sealed class StoryResult<out R> private constructor() {
    data class Success<out T>(val data: T) : StoryResult<T>()
    data class Error(val error: String) : StoryResult<Nothing>()
    object Loading : StoryResult<Nothing>()
}