package com.sanomush.sstoryapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanomush.sstoryapp.R
import com.sanomush.sstoryapp.data.response.ListStoryItem
import com.sanomush.sstoryapp.databinding.ActivityMainBinding
import com.sanomush.sstoryapp.factory.ViewModelFactory
import com.sanomush.sstoryapp.ui.add.AddStoryActivity
import com.sanomush.sstoryapp.ui.detail.DetailActivity
import com.sanomush.sstoryapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupRecyclerView()
        setupFab()
        observeSession()
        observeStories()
    }

    private fun setupView() {
        title = getString(R.string.app_name)
        supportActionBar?.show()
    }

    private fun setupRecyclerView() {
        adapter = MainAdapter()
        adapter.setOnItemClickCallback(object : MainAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                Intent(this@MainActivity, DetailActivity::class.java).also { intent ->
                    intent.putExtra(DetailActivity.EXTRA_STORY_ID, story.id)
                    startActivity(intent)
                }
            }
        })

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupFab() {
        // Navigasi ke AddStoryActivity saat FAB diklik
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeStories() {
        viewModel.getListStories()

        viewModel.resultStories.observe(this) { stories ->
            if (stories.isNotEmpty()) {
                adapter.submitList(stories)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                // Panggil getListStories() hanya jika user sudah login
                viewModel.getListStories()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvStory.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}
