package com.sanomush.sstoryapp.repository


import com.sanomush.sstoryapp.data.pref.UserPreference
import com.sanomush.sstoryapp.data.response.AddStoryResponse
import com.sanomush.sstoryapp.data.response.ListStoryResponse
import com.sanomush.sstoryapp.data.response.ResponseDetailStory
import com.sanomush.sstoryapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun getStories(): Result<ListStoryResponse> {
        return try {
            val session = userPreference.getSession().firstOrNull()
            if (session?.token.isNullOrEmpty()) {
                return Result.failure(Exception("Token not found"))
            }
            val response = apiService.getStories("Bearer ${session?.token}")
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStoryDetail(id: String): Result<ResponseDetailStory> {
        return try {
            val session = userPreference.getSession().firstOrNull()
            if (session?.token.isNullOrEmpty()) {
                return Result.failure(Exception("Token not found"))
            }
            val response = apiService.getDetailStory("Bearer ${session?.token}", id)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addStory(file: File, description: String, token: String): Result<AddStoryResponse> {
        return try {
            val filePart = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.addStory("Bearer $token", filePart, descriptionPart)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to upload story"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}