package com.example.vendingmachineinventorymanagement.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.adapters.ProductsAdapter
import com.example.vendingmachineinventorymanagement.databinding.ActivityAddProdutcsBinding
import com.example.vendingmachineinventorymanagement.databinding.ActivityViewProductsBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.hide
import com.example.vendingmachineinventorymanagement.extensionfunctions.visible
import com.example.vendingmachineinventorymanagement.models.Product

class ViewProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewProductsBinding
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var mContext:Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun setProductsAdapter(productsList: List<Product>) {
        mContext=this
        binding.includeProductsLayout.shimmer.startShimmerAnimation()
        binding.includeProductsLayout.shimmer.stopShimmerAnimation()
        binding.includeProductsLayout.shimmer.hide()
        binding.includeProductsLayout.rvProducts.visible()
        productsAdapter = ProductsAdapter(mContext, productsList, productsItemOnClick)
        binding.includeProductsLayout.rvProducts.adapter = productsAdapter
    }
    private val productsItemOnClick: (Product) -> Unit = { item ->
        navigateToDetailsScreen(item)
    }

    private fun navigateToDetailsScreen(item: Product) {

    }
}