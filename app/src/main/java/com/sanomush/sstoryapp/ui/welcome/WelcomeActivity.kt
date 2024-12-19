package com.sanomush.sstoryapp.ui.welcome

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.sanomush.sstoryapp.databinding.ActivityWelcomeBinding
import com.sanomush.sstoryapp.ui.login.LoginActivity
import com.sanomush.sstoryapp.ui.signup.SignUpActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        animateImage()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun animateImage() {

        val imageView = binding.imageView

        val translationX = ObjectAnimator.ofFloat(imageView, "translationX", -500f, 0f)
        translationX.duration = 1200
        translationX.interpolator = OvershootInterpolator()
        translationX.start()
    }
}
