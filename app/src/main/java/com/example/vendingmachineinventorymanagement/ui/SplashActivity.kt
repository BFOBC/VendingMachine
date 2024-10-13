package com.example.vendingmachineinventorymanagement.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.extensionfunctions.createSingleInstanceIntent
import com.example.vendingmachineinventorymanagement.ui.admin.LoginActivity
import com.example.vendingmachineinventorymanagement.ui.customer.WellComeActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Delay transition to the login screen for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = createSingleInstanceIntent<WellComeActivity>()
            startActivity(intent)
            finish()
        }, 3000)
    }
}