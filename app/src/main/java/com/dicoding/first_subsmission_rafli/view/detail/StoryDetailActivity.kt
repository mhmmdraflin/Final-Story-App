package com.dicoding.first_subsmission_rafli.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.first_subsmission_rafli.R
import com.dicoding.first_subsmission_rafli.data.response.ListStoryItem
import com.dicoding.first_subsmission_rafli.databinding.ActivityDetailBinding
import com.dicoding.first_subsmission_rafli.extension.loadImage

@Suppress("DEPRECATION")
class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val detailstory = intent.getParcelableExtra<ListStoryItem>(StoryDetailActivity._STORYDETAIL)
            ?: throw IllegalArgumentException("Detail story not found")
        setupAction(detailstory)

        supportActionBar?.show()
        supportActionBar?.title = getString(R.string.Detail)
    }

    private fun setupAction(detailstory: ListStoryItem) {

        binding.imageDetail.loadImage(detailstory.photoUrl)


        with(binding) {
            name.text = detailstory.name
            desc.text = detailstory.description
        }
    }

    companion object {
        const val _STORYDETAIL = "Story_Detail"
    }
}
