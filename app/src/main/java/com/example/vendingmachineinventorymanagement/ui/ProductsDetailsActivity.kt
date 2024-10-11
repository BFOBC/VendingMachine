package com.example.vendingmachineinventorymanagement.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.ActivityDashboardBinding
import com.example.vendingmachineinventorymanagement.databinding.ActivityProductsDetailsBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.isNetworkAvailable
import com.example.vendingmachineinventorymanagement.extensionfunctions.loadImageIntoImageView
import com.example.vendingmachineinventorymanagement.extensionfunctions.showCustomErrorDialog
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.utils.singleClickListener
import com.example.vendingmachineinventorymanagement.viewmodels.ProductViewModel

class ProductsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsDetailsBinding
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        val product = intent.getParcelableExtra<Product>("product_key")

        // Check if the product is not null and set the data to the layout
        product?.let {
            binding.etProductName.setText(it.productName)
            binding.etSellPrice.setText(it.productSellingPrice.toString())
            binding.etCostPrice.setText(it.productCostPrice.toString())
            binding.etSlotNumber.setText(it.slotNumber.toString())
            binding.etDescription.setText(it.productDescription)
            binding.etMaxQuantity.setText(it.maxQuantity.toString())
            binding.etAvailableQuantity.setText(it.availableQuantity.toString())

            val placeholderImage = R.drawable.baseline_image_24
            loadImageIntoImageView(this@ProductsDetailsActivity, product.productImage.toString(),
                binding.productImageView, placeholderImage, placeholderImage)
        }
        binding.btnDeleteProduct.singleClickListener {
            // Show confirmation dialog
            AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Do you want to delete this product?")
                .setPositiveButton("Yes") { dialog, _ ->
                    // Proceed with deletion if confirmed
                    if (isNetworkAvailable(this@ProductsDetailsActivity)) {
                        productViewModel.deleteDataFromFirebase(product?.productId.toString())
                        Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                    } else {
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
                            productViewModel.deleteDataFromFirebase(product?.productId.toString())
                        }
                    }
                    dialog.dismiss() // Dismiss the dialog after action
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss() // Just dismiss if user clicks "No"
                }
                .setCancelable(false) // Optional: Prevent dismissal by tapping outside
                .show() // Show the dialog
        }

        binding.btnUpdateProduct.singleClickListener {
            // Collect data from EditTexts into a Map
            val productName = binding.etProductName.text.toString()
            val productSellingPrice = binding.etSellPrice.text.toString().toDoubleOrNull() ?: 0.0
            val productCostPrice = binding.etCostPrice.text.toString().toDoubleOrNull() ?: 0.0
            val slotNumber = binding.etSlotNumber.text.toString().toIntOrNull() ?: 0
            val productDescription = binding.etDescription.text.toString()
            val maxQuantity = binding.etMaxQuantity.text.toString().toIntOrNull() ?: 0
            val availableQuantity = binding.etAvailableQuantity.text.toString().toIntOrNull() ?: 0

            // Create a map with all the product values
            val updatedProductData = mapOf(
                "productName" to productName,
                "productSellingPrice" to productSellingPrice,
                "productCostPrice" to productCostPrice,
                "slotNumber" to slotNumber,
                "productDescription" to productDescription,
                "maxQuantity" to maxQuantity,
                "availableQuantity" to availableQuantity
            )
            //call the update fun to update values in database
            if (isValid()){
                if (isNetworkAvailable(this@ProductsDetailsActivity)) {
                    productViewModel.updateDataInFirebase(product?.productId.toString(),updatedProductData)
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
                        productViewModel.updateDataInFirebase(product?.productId.toString(),updatedProductData)
                    }
                }
            }

        }
        // Observe the operation status from ViewModel
        productViewModel.operationStatus.observe(this) { result ->
            result.fold(
                onSuccess = { successMessage ->
                    // Show success message
                    val imageResId = resources.getIdentifier(
                        "ic_green_tick_icon",
                        "drawable",
                        packageName
                    )
                    showCustomErrorDialog("Updated",
                        "Product updated successfully",
                        "OK",
                        imageResId
                    ) {
                        finish()
                    }
                },
                onFailure = { exception ->
                    // Show error message
                    val imageResId = resources.getIdentifier(
                        "sad_icon",
                        "drawable",
                        packageName
                    )
                    showCustomErrorDialog("Error",
                        "Error: ${exception.localizedMessage}",
                        "Retry",
                        imageResId
                    ) {
                        productViewModel.deleteDataFromFirebase(product?.productId.toString())
                    }
                    Toast.makeText(this, "Error: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            )
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

}