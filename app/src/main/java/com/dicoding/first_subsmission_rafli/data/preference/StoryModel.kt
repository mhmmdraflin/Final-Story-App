package com.dicoding.first_subsmission_rafli.data.preference

data class StoryModel(
    val password: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
