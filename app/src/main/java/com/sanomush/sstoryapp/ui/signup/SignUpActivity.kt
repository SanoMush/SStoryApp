package com.sanomush.sstoryapp.ui.signup

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanomush.sstoryapp.databinding.ActivitySignUpBinding
import com.sanomush.sstoryapp.factory.ViewModelFactory
import kotlinx.coroutines.launch
import com.sanomush.sstoryapp.utils.Result



class SignUpActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        observeRegister()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                name.isEmpty() -> {
                    binding.nameEditText.error = "Nama tidak boleh kosong"
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    binding.emailEditText.error = "Email tidak boleh kosong"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Password tidak boleh kosong"
                    return@setOnClickListener
                }
                password.length < 8 -> {
                    binding.passwordEditText.error = "Password minimal 8 karakter"
                    return@setOnClickListener
                }
                else -> {
                    lifecycleScope.launch {
                        viewModel.register(name, email, password)
                    }
                }
            }
        }
    }

    private fun observeRegister() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                showLoading(isLoading)
            }
        }

        lifecycleScope.launch {
            viewModel.registerResult.collect { result ->
                result?.let {
                    when (it) {
                        is Result.Success -> {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Akun berhasil dibuat",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetRegisterResult()
                            finish()
                        }
                        is Result.Error -> {
                            val errorMessage = if (it.error.contains("Email is already taken")) {
                                "Email sudah digunakan, silahkan gunakan email lain"
                            } else {
                                it.error
                            }
                            Toast.makeText(
                                this@SignUpActivity,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetRegisterResult()
                        }
                        is Result.Loading -> {
                            // Handle loading if needed
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            signupButton.isEnabled = !isLoading
            nameEditText.isEnabled = !isLoading
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}