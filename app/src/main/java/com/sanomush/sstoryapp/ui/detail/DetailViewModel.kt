package com.sanomush.sstoryapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanomush.sstoryapp.data.response.ResponseDetailStory
import com.sanomush.sstoryapp.data.response.Story
import com.sanomush.sstoryapp.repository.StoryRepository
import com.sanomush.sstoryapp.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _storyDetail = MutableLiveData<Story>()
    val storyDetail: LiveData<Story> = _storyDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getStoryDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = userRepository.getSession().first()
                val result = storyRepository.getStoryDetail(id)
                result.onSuccess { response: ResponseDetailStory ->
                    _storyDetail.value = response.story
                }.onFailure { exception: Throwable ->
                    _errorMessage.value = exception.message
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}