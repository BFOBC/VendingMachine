package com.example.vendingmachineinventorymanagement.ui

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.TBL_PRODUCTS
import com.example.vendingmachineinventorymanagement.databinding.ActivityAddProdutcsBinding
import com.example.vendingmachineinventorymanagement.databinding.ActivityLoginBinding
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.utils.singleClickListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProdutcsBinding
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private val STORAGE_PERMISSION_CODE = 101
    private val storageReference = FirebaseStorage.getInstance().reference
    private val databaseReference = FirebaseDatabase.getInstance().getReference(TBL_PRODUCTS)
    private var imageUrl: String=""
    private lateinit var progressDialog: ProgressDialog // Declare ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inflate the layout using view binding
        binding = ActivityAddProdutcsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.btnChooseImage.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestStoragePermission()
            }
        }

        binding.apply {
            btnSubmitProduct.setOnClickListener {
                if (isValid()) {
                    uploadImageAndSaveProduct()
                }
            }
        }
    }
    private fun uploadImageAndSaveProduct() {
        if (imageUri != null) {
            // Show the progress dialog
            progressDialog = ProgressDialog(this).apply {
                setMessage("Uploading product...")
                setCancelable(false) // Prevent canceling the dialog
                show() // Show the dialog
            }

            // Generate a unique ID for the product image
            val imageId = UUID.randomUUID().toString()
            val imageRef = storageReference.child("product_images/$imageId.jpg")

            // Upload image to Firebase Storage
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Get the download URL for the image and save product data
                        val imageUrl = uri.toString()
                        saveProductToDatabase(imageUrl)
                    }
                }
                .addOnFailureListener {
                    progressDialog.dismiss() // Dismiss the dialog
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProductToDatabase(imageUrl: String) {
        // Generate a unique product ID
        val productId = UUID.randomUUID().toString()

        // Create Product object with the required data
        val product = Product(
            productId = productId,
            productName = binding.etProductName.text.toString().trim(),
            productSellingPrice = binding.etSellPrice.text.toString().trim(),
            productCostPrice = binding.etCostPrice.text.toString().trim(),
            slotNumber = binding.etSlotNumber.text.toString().trim().toInt(),
            productDescription = binding.etDescription.text.toString().trim(),
            maxQuantity = binding.etMaxQuantity.text.toString().trim().toInt(),
            availableQuantity = binding.etAvailableQuantity.text.toString().trim().toInt(),
            productImage = imageUrl // Save image URL
        )

        // Save the product to Firebase Realtime Database
        databaseReference.child(productId).setValue(product)
            .addOnSuccessListener {
                progressDialog.dismiss() // Dismiss the dialog
                Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show()
                // Clear the form after successful submission
                clearForm()
            }
            .addOnFailureListener {
                progressDialog.dismiss() // Dismiss the dialog
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearForm() {
        binding.apply {
            etProductName.text?.clear()
            etSellPrice.text?.clear()
            etCostPrice.text?.clear()
            etSlotNumber.text?.clear()
            etDescription.text?.clear()
            etMaxQuantity.text?.clear()
            etAvailableQuantity.text?.clear()
            productImageView.setImageURI(null)
            imageUri = null
        }
    }
    // Validation function that checks all fields
    private fun isValid(): Boolean {
        var isValid = true
        binding.apply {
            // Check Product Name
            if (etProductName.text.toString().trim().isEmpty()) {
                etProductName.error = "Product name is required"
                isValid = false
            } else {
                etProductName.error = null
            }
            // Check Sell Price
            if (etSellPrice.text.toString().trim().isEmpty()) {
                etSellPrice.error = "Sell price is required"
                isValid = false
            } else {
                etSellPrice.error = null
            }
            // Check Cost Price
            if (etCostPrice.text.toString().trim().isEmpty()) {
                etCostPrice.error = "Cost price is required"
                isValid = false
            } else {
                etCostPrice.error = null
            }
            // Check Slot Number
            if (etSlotNumber.text.toString().trim().isEmpty()) {
                etSlotNumber.error = "Slot number is required"
                isValid = false
            } else {
                etSlotNumber.error = null
            }
            // Check Description
            if (etDescription.text.toString().trim().isEmpty()) {
                etDescription.error = "Description is required"
                isValid = false
            } else {
                etDescription.error = null
            }
            // Check Max Quantity
            if (etMaxQuantity.text.toString().trim().isEmpty()) {
                etMaxQuantity.error = "Max quantity is required"
                isValid = false
            } else {
                etMaxQuantity.error = null
            }
            // Check Available Quantity
            if (etAvailableQuantity.text.toString().trim().isEmpty()) {
                etAvailableQuantity.error = "Available quantity is required"
                isValid = false
            } else {
                etAvailableQuantity.error = null
            }
        }
        return isValid
    }


    // Check if permission is already granted
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request storage permission
    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Open the gallery for image selection
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.productImageView.setImageURI(imageUri)
        }
    }
}