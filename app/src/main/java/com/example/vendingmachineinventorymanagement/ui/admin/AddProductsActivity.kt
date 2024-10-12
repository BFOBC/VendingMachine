package com.example.vendingmachineinventorymanagement.ui.admin

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.TBL_PRODUCTS
import com.example.vendingmachineinventorymanagement.databinding.ActivityAddProdutcsBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.isNetworkAvailable
import com.example.vendingmachineinventorymanagement.extensionfunctions.showCustomErrorDialog
import com.example.vendingmachineinventorymanagement.models.Product
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
        checkAndRequestManageExternalStoragePermission()
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
                    if (isNetworkAvailable(this@AddProductsActivity)) {
                        uploadImageAndSaveProduct()
                    }else{
                        val imageResId = resources.getIdentifier(
                            "sad_icon",
                            "drawable",
                            packageName
                        )
                        showCustomErrorDialog("No Internet",
                            "Check your internet",
                            "retry",
                            imageResId
                        ) {
                            uploadImageAndSaveProduct()
                        }
                    }
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
            productSellingPrice = binding.etSellPrice.text.toString().trim().toInt(),
            productCostPrice = binding.etCostPrice.text.toString().trim().toInt(),
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
                val imageResId = resources.getIdentifier(
                    "ic_green_tick_icon",
                    "drawable",
                    packageName
                )
                showCustomErrorDialog("Uploaded",
                    "Product added successfully",
                    "OK",
                    imageResId
                ) {
                }
                // Clear the form after successful submission
                clearForm()
            }
            .addOnFailureListener {
                progressDialog.dismiss() // Dismiss the dialog
                val imageResId = resources.getIdentifier(
                    "sad_icon",
                    "drawable",
                    packageName
                )
                showCustomErrorDialog("Server Issue",
                    "Product added successfully",
                    "OK",
                    imageResId
                ) {
                }
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


    private fun checkPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 12 and below
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Request READ_MEDIA_IMAGES for Android 13 and above
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                STORAGE_PERMISSION_CODE
            )
        } else {
            // Request READ_EXTERNAL_STORAGE for older Android versions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }
    private fun checkAndRequestManageExternalStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Check if permission is granted
            if (!Environment.isExternalStorageManager()) {
                try {
                    // Open settings to grant access to all files
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivityForResult(intent, STORAGE_PERMISSION_CODE)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivityForResult(intent, STORAGE_PERMISSION_CODE)
                }
            }
        } else {
            // For Android versions below R, request traditional permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
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
         // Get the selected image URI
         imageUri = data.data!!

         // Set the image to the ImageView
         binding.productImageView.setImageURI(imageUri)
     }
 }

}