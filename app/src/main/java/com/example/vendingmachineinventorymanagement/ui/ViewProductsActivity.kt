package com.example.vendingmachineinventorymanagement.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.vendingmachineinventorymanagement.adapters.ProductsAdapter
import com.example.vendingmachineinventorymanagement.databinding.ActivityViewProductsBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.hide
import com.example.vendingmachineinventorymanagement.extensionfunctions.visible
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.viewmodels.ProductViewModel


class ViewProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewProductsBinding
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var mContext:Context
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        // Set up the refresh listener
        binding.pullToRefresh.setOnRefreshListener { // Refresh your data here
            productViewModel.fetchItems()
            readData()
            // After refreshing is done, hide the refresh icon
            binding.pullToRefresh.isRefreshing = false
        }
    }
    private fun readData() {
        // Observe the LiveData from ViewModel
        productViewModel.items.observe(this) { itemList ->
            if (itemList != null) {
                setProductsAdapter(itemList)
            } else {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
            }
        }
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