package com.example.vendingmachineinventorymanagement.ui.customer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.ActivityWellComeBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.createSingleInstanceIntent
import com.example.vendingmachineinventorymanagement.extensionfunctions.hide
import com.example.vendingmachineinventorymanagement.extensionfunctions.setupAdminSetting
import com.example.vendingmachineinventorymanagement.extensionfunctions.showCustomToast
import com.example.vendingmachineinventorymanagement.extensionfunctions.visible
import com.example.vendingmachineinventorymanagement.ui.admin.LoginActivity
import com.example.vendingmachineinventorymanagement.ui.media.CustomMediaController
import com.example.vendingmachineinventorymanagement.ui.media.MediaItemModel
import com.example.vendingmachineinventorymanagement.utils.enums.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class WellComeActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityWellComeBinding
    private lateinit var mediaController: CustomMediaController
    private var mediaItems: List<MediaItemModel>? = null
    private var currentIndex = 0

    // Declare the permission launcher at the class level
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadMediaFromDownloadFolder()
        } else {
            // Handle permission denial appropriately
            showCustomToast("Storage permission denied")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inflate the layout using view binding
        binding = ActivityWellComeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getScreenSize()
        // Check if permission is already granted
        if (checkStoragePermission()) {
            loadMediaFromDownloadFolder()
        } else {
            requestStoragePermission()
        }
        initViews()
    }

    private fun initViews() {
        openAdminSettings()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        val navInflater: NavInflater = navController.navInflater
        val navGraph: NavGraph = navInflater.inflate(R.navigation.home_navigation_graph)
        navGraph.setStartDestination(R.id.homeFragment)
        navController.graph = navGraph
    }

    // Function to check storage permission based on Android version
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun loadMediaFromAsset(){
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
    }

    // Function to request storage permission
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // Function to load media files from the Download folder
    private fun loadMediaFromDownloadFolder() {
        val folderPath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "VendingMachineMedia"
        )

        val downloadFolder = File(folderPath.toString())
        val files = downloadFolder.listFiles()

        mediaItems = files?.filter {
            it.extension in listOf("jpg", "png", "mp4", "mkv", "avi")
        }?.map { file ->
            val uri = Uri.fromFile(file).toString()
            if (file.extension in listOf("mp4", "mkv", "avi")) {
                MediaItemModel(MediaType.VIDEO, uri)
            } else {
                MediaItemModel(MediaType.IMAGE, uri)
            }
        } ?: emptyList()
        if (mediaItems!=null && mediaItems!!.isNotEmpty()){
            createDots()
            playMedia(currentIndex)
        }else{
            loadMediaFromAsset()
        }
    }
    private fun playMedia(index: Int) {
        // Ensure mediaItems is not null and has elements
        if (mediaItems.isNullOrEmpty()) {
            showCustomToast("No media items available")
            return
        }

        // Reset the index if it exceeds the list size
        if (index >= mediaItems!!.size) currentIndex = 0 else currentIndex = index

        setActiveDot(currentIndex)

        val mediaItem = mediaItems!![currentIndex]

        with(binding) {
            when (mediaItem.type) {
                MediaType.IMAGE -> {
                    videoView.hide()
                    imageViewHome.visible()
                    imageViewHome.setImageURI(Uri.parse(mediaItem.content))

                    // Use lifecycleScope to delay and play the next media item
                    lifecycleScope.launch {
                        delay(3000) // Delay for 3 seconds before showing the next media item
                        playMedia(currentIndex + 1)
                    }
                }
                MediaType.VIDEO -> {
                    imageViewHome.hide()
                    videoView.visible()
                    prepareMediaPlayer(mediaItem.content)
                }
            }
        }
    }


    private fun prepareMediaPlayer(content: String) {
        binding.videoView.setVideoURI(Uri.parse(content))
        mediaController = CustomMediaController(this).apply {
            setAnchorView(binding.videoView)
        }

        binding.videoView.setOnPreparedListener { mediaPlayer ->
            // Ensure video scales to match the parent layout
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            // Start video playback
            binding.videoView.start()
        }
        binding.videoView.setMediaController(mediaController)

        binding.videoView.setOnCompletionListener {
            playMedia(++currentIndex)
        }
    }

    private fun setActiveDot(index: Int) {
        for (i in 0 until binding.rootViewDotsHomeScreen.childCount) {
            val dot = binding.rootViewDotsHomeScreen.getChildAt(i) as ImageView
            dot.setImageResource(if (i == index) R.drawable.ic_brown_circle else R.drawable.ic_gray_circle)
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

    private fun openAdminSettings() {
        binding.imgAdminSetting.setupAdminSetting(this@WellComeActivity) {
            val intent = createSingleInstanceIntent<LoginActivity>()
            startActivity(intent)
        }
    }
}