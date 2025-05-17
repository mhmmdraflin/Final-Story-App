package com.dicoding.first_subsmission_rafli.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.first_subsmission_rafli.Result.StoryPagingSource
import com.dicoding.first_subsmission_rafli.Result.StoryResult
import com.dicoding.first_subsmission_rafli.data.preference.StoryModel
import com.dicoding.first_subsmission_rafli.data.preference.StoryPreference
import com.dicoding.first_subsmission_rafli.data.response.ErrorResponse
import com.dicoding.first_subsmission_rafli.data.response.ListStoryItem
import com.dicoding.first_subsmission_rafli.data.response.LoginResponse
import com.dicoding.first_subsmission_rafli.data.response.RegisterResponse
import com.dicoding.first_subsmission_rafli.data.response.UploadResponse
import com.dicoding.first_subsmission_rafli.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val storyPreference: StoryPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: StoryModel) {
        storyPreference.saveSession(user)
    }
    fun uploadStoryWithLocation(
        imageFile: File,
        description: String,
        latitude: Double,
        longitude: Double
    ): LiveData<StoryResult<UploadResponse>> = liveData {
        emit(StoryResult.Loading)

        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val latitudeBody = latitude.toString().toRequestBody("text/plain".toMediaType())
        val longitudeBody = longitude.toString().toRequestBody("text/plain".toMediaType())


        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        try {

            val successResponse = apiService.uploadImageWithLocation(
                multipartBody,
                requestBody,
                latitudeBody,
                longitudeBody
            )
            emit(StoryResult.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(StoryResult.Error(errorResponse.message))
        }
    }
    fun getPagedStories(): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).flow
    }
    fun getPagedStories2(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<StoryResult<RegisterResponse>> = liveData {
        emit(StoryResult.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(StoryResult.Success(response))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            val body = Gson().fromJson(error, ErrorResponse::class.java)
            emit(StoryResult.Error(body.message))
        }
    }

    fun login(email: String, password: String): LiveData<StoryResult<LoginResponse>> = liveData {
        emit(StoryResult.Loading)
        try {
            val response = apiService.login(email, password)
            emit(StoryResult.Success(response))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            val body = Gson().fromJson(error, ErrorResponse::class.java)
            emit(StoryResult.Error(body.message))
        }
    }

    fun getSession(): Flow<StoryModel> = storyPreference.getSession()

    fun getStory(): LiveData<StoryResult<List<ListStoryItem>>> = liveData {
        emit(StoryResult.Loading)
        try {
            val response = apiService.getStory()
            emit(StoryResult.Success(response.listStory))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(StoryResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(StoryResult.Error(e.message ?: "Error"))
        }
    }

    fun uploadStory(imageFile: File, description: String): LiveData<StoryResult<UploadResponse>> =
        liveData {
            emit(StoryResult.Loading)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            try {
                val successResponse = apiService.uploadImage(multipartBody, requestBody)
                emit(StoryResult.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
                emit(StoryResult.Error(errorResponse.message))
            }
        }

    suspend fun logout() {
        storyPreference.logout()
    }

    fun getStoriesWithLocation(): LiveData<StoryResult<List<ListStoryItem>>> {
        val result = MutableLiveData<StoryResult<List<ListStoryItem>>>()
        result.postValue(StoryResult.Loading)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Mendapatkan response dengan Retrofit
                val response = apiService.getStoriesWithLocation(location = 1)

                // Memeriksa apakah respons berhasil
                if (response.isSuccessful) {
                    // Memeriksa apakah error bernilai false dalam StoryResponse
                    val body = response.body()
                    if (body != null && !body.error) {
                        // Mengirimkan listStory ke LiveData jika tidak ada error
                        result.postValue(StoryResult.Success(body.listStory))
                    } else {
                        // Jika terdapat error pada response
                        result.postValue(StoryResult.Error(body?.message ?: "Unknown error"))
                    }
                } else {
                    // Jika response tidak berhasil
                    result.postValue(StoryResult.Error("Failed to load stories"))
                }
            } catch (e: Exception) {
                // Tangani error jaringan atau masalah lainnya
                result.postValue(StoryResult.Error("Network error"))
            }
        }

        return result
    }




    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun clearInstance() {
            instance = null
        }

        fun getInstance(
            apiService: ApiService,
            storyPreference: StoryPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyPreference, apiService)
            }.also { instance = it }
    }
}