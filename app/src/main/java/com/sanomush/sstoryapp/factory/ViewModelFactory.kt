package com.sanomush.sstoryapp.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sanomush.sstoryapp.di.Injection
import com.sanomush.sstoryapp.repository.StoryRepository
import com.sanomush.sstoryapp.repository.UserRepository
import com.sanomush.sstoryapp.ui.add.AddStoryViewModel
import com.sanomush.sstoryapp.ui.detail.DetailViewModel
import com.sanomush.sstoryapp.ui.login.LoginViewModel
import com.sanomush.sstoryapp.ui.main.MainViewModel
import com.sanomush.sstoryapp.ui.signup.SignUpViewModel


class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository, userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideStoryRepository(context)
                )
            }.also { INSTANCE = it }
        }
    }
}