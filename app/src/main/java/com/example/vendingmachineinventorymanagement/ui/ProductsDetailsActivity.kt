package com.example.vendingmachineinventorymanagement.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.ActivityDashboardBinding
import com.example.vendingmachineinventorymanagement.databinding.ActivityProductsDetailsBinding
import com.example.vendingmachineinventorymanagement.models.Product

class ProductsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsDetailsBinding

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

            // You may also need to set the product image if you have a URL or resource
            // Example: if the product image is a drawable, you can set it like this
            binding.productImageView.setImageResource(R.drawable.baseline_image_24) // Replace with actual image logic
        }
    }
}