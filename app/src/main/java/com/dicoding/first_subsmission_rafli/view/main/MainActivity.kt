package com.dicoding.first_subsmission_rafli.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.first_subsmission_rafli.Result.StoryResult
import com.dicoding.first_subsmission_rafli.data.response.ListStoryItem
import com.dicoding.first_subsmission_rafli.databinding.ActivityMainBinding
import com.dicoding.first_subsmission_rafli.view.ViewModelFactory
import com.dicoding.first_subsmission_rafli.view.adapter.StoryListAdapter
import com.dicoding.first_subsmission_rafli.view.maps.MapsActivity
import com.dicoding.first_subsmission_rafli.view.upload.UploadActivity
import com.dicoding.first_subsmission_rafli.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var StoryListAdapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.show()
        viewModel.getSession().observe(this) { story ->
            if (!story.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupView()
                getData()
                setupAction()
            }
        }

        binding.fabmaps.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }


        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }

        binding.fabLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupView() {
        val layout = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layout
        val Decoration = DividerItemDecoration(this, layout.orientation)
        binding.recyclerview.addItemDecoration(Decoration)

        StoryListAdapter = StoryListAdapter()
        binding.recyclerview.adapter = StoryListAdapter
    }

    private fun setupAction() {
        lifecycleScope.launch {
            viewModel.getPaged().collectLatest { pagingData: PagingData<ListStoryItem> ->
                StoryListAdapter.submitData(pagingData)
            }
        }
    }

    private fun getData() {
        viewModel.getStories()
    }
}
