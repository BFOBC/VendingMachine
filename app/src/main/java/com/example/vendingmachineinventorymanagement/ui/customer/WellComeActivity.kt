package com.example.vendingmachineinventorymanagement.ui.customer

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.ActivityAddProdutcsBinding
import com.example.vendingmachineinventorymanagement.databinding.ActivityWellComeBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.hide
import com.example.vendingmachineinventorymanagement.extensionfunctions.visible
import com.example.vendingmachineinventorymanagement.ui.media.CustomMediaController
import com.example.vendingmachineinventorymanagement.ui.media.MediaItemModel
import com.example.vendingmachineinventorymanagement.utils.enums.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WellComeActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityWellComeBinding
    private lateinit var mediaController: CustomMediaController
    private var mediaItems: List<MediaItemModel>? = null
    private var currentIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inflate the layout using view binding
        binding = ActivityWellComeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getScreenSize()
        initViews()
    }

    private fun initViews() {
        mediaItems = listOf(
            MediaItemModel(
                MediaType.VIDEO,
                "android.resource://${packageName}/${R.raw.big_buck_bunny_720p_20mb}"
            ),
            MediaItemModel(MediaType.IMAGE, R.drawable.imageview_home.toString()),
            MediaItemModel(
                MediaType.VIDEO,
                "android.resource://${packageName}/${R.raw.big_buck_bunny_720p_20mb}"
            ),
            MediaItemModel(MediaType.IMAGE, R.drawable.imageview_home.toString()),
            // Add more MediaItems as needed
        )
        playMedia(currentIndex)
        // Create dots dynamically
        createDots()
        // Set the initially active dot
        setActiveDot(0)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        val navInflater: NavInflater = navController.navInflater
        val navGraph: NavGraph = navInflater.inflate(R.navigation.home_navigation_graph)
        navGraph.setStartDestination(R.id.homeFragment)
        navController.graph = navGraph
    }
    private fun playMedia(index: Int) {
        if (currentIndex == mediaItems!!.size) {
            currentIndex = 0
        }
        // Set the initially active dot
        setActiveDot(index)
        if (index < mediaItems!!.size) {
            val mediaItem = mediaItems!![index]
            with(binding) {
                when (mediaItem.type) {
                    MediaType.IMAGE -> {
                        // Show image
                        videoView.hide()
                        imageViewHome.visible()
                        imageViewHome.setImageResource(mediaItem.content.toInt())
                        lifecycleScope.launch {
                            delay(3000)
                            playMedia(currentIndex++)
                        }
                    }

                    MediaType.VIDEO -> {
                        // Play video
                        imageViewHome.hide()
                        videoView.visible()
                        prepareMediaPlayer(mediaItem.content)
                    }
                }
            }

        }
    }

    private fun prepareMediaPlayer(content: String) {
        val videoUri = Uri.parse(content)
        binding.videoView.setVideoURI(videoUri)
        // Create a MediaController if it's null

        mediaController = CustomMediaController(this)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.start()

        binding.videoView.setOnErrorListener { _, what, extra ->
            // Handle errors here
            currentIndex++
            playMedia(currentIndex)
            false
        }
        binding.videoView.setOnCompletionListener {
            // When video playback is complete, move to the next media item
            currentIndex++
            playMedia(currentIndex)
        }
        binding.videoView.setOnPreparedListener {
            // Get the duration of the video
            val duration = binding.videoView.duration
            lifecycleScope.launch {
                // Start playing the video
                binding.videoView.start()
                delay(9000)
                binding.videoView.stopPlayback()
                playMedia(currentIndex++)
            }
        }
    }

    private fun createDots() {
        for (i in 0 until (mediaItems?.size ?: 0)) {
            val dot = ImageView(this)
            dot.setImageResource(R.drawable.ic_gray_circle) // Set the drawable for inactive dot
            dot.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0) // Adjust margins as needed
            }
            binding.rootViewDotsHomeScreen.addView(dot)
        }
    }

    private fun setActiveDot(index: Int) {
        for (i in 0 until binding.rootViewDotsHomeScreen.childCount) {
            val dot = binding.rootViewDotsHomeScreen.getChildAt(i) as ImageView
            if (i == index) {
                dot.setImageResource(R.drawable.ic_brown_circle) // Set the drawable for active dot
            } else {
                dot.setImageResource(R.drawable.ic_gray_circle) // Set the drawable for inactive dot
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        binding.videoView.stopPlayback()
        // Set the media controller to null
        mediaController?.let {
            it.hide()
            //it.setMediaPlayer(null)
        }
    }

    private fun getScreenSize() {
        val screenWidth = resources.displayMetrics.widthPixels

        val aspectRatio = 16 / 9.0
        val height = (screenWidth / aspectRatio).toInt()

        val paramsVideoView = binding.videoView.layoutParams
        paramsVideoView.height = height
        binding.videoView.layoutParams = paramsVideoView

        val paramsImageView = binding.imageViewHome.layoutParams
        paramsImageView.height = height
        binding.imageViewHome.layoutParams = paramsImageView
    }
}