package com.example.vendingmachineinventorymanagement.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.ActivityLoginBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.getProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.example.vendingmachineinventorymanagement.extensionfunctions.isNetworkAvailable
import com.example.vendingmachineinventorymanagement.extensionfunctions.showCustomErrorDialog

class LoginActivity : AppCompatActivity() {
    // View binding for accessing views
    private lateinit var binding: ActivityLoginBinding

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth
    private lateinit var mContext: Context
    private lateinit var progressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inflate the layout using view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Login button click listener
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Input validation
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Please enter a valid email"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                binding.etPassword.error = "Password must be at least 6 characters"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            // Perform Firebase authentication
            if (isNetworkAvailable(this)) {
                progressDialog = getProgressDialog(false)
                progressDialog.show()
                loginUser(email, password)

            } else {
                val imageResId = resources.getIdentifier(
                    "success_icon",
                    "drawable",
                    packageName
                )
                showCustomErrorDialog("No Internet",
                    "Check your internet",
                    "retry",
                    imageResId
                ) {

                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressDialog.hide()
                if (task.isSuccessful) {
                    // Login success
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    // Navigate to the next activity (e.g., MainActivity)
                    val intent = Intent(this, Dashboard::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // Login failure
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


}