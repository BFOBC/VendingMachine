package com.example.vendingmachineinventorymanagement.ui.customer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.adapters.ProductsAdapter
import com.example.vendingmachineinventorymanagement.databinding.FragmentRedeemBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.hide
import com.example.vendingmachineinventorymanagement.extensionfunctions.showCustomErrorDialog
import com.example.vendingmachineinventorymanagement.extensionfunctions.visible
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.utils.baseclasses.BaseFragment
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.CUSTOMER
import com.example.vendingmachineinventorymanagement.viewmodels.ProductViewModel


class RedeemFragment : BaseFragment<FragmentRedeemBinding>() {
    private val productViewModel: ProductViewModel by viewModels()
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var mContext: Context


    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?) {
      initViews()
    }
    private fun initViews() {
        mContext=requireActivity()
        readData()
        // Set up the refresh listener
        binding.pullToRefresh.setOnRefreshListener { // Refresh your data here
            productViewModel.fetchItems()
            readData()
            // After refreshing is done, hide the refresh icon
            binding.pullToRefresh.isRefreshing = false
        }
    }
    private fun readData() {
        val imageResId = resources.getIdentifier(
            "sad_icon",
            "drawable",
            mContext.packageName
        )
        // Observe the LiveData from ViewModel
        productViewModel.items.observe(viewLifecycleOwner) { itemList ->
            binding.progressbar.hide()
            if (itemList != null && itemList.isNotEmpty()) {
                hideLoadingViews()
                setProductsAdapter(itemList)
            } else {
                mContext.showCustomErrorDialog("Data not found",
                    "No Products Available",
                    "OK",
                    imageResId
                ) {
                }
                hideLoadingViews()
                binding.tvNoProducts.visible()
            }
        }
    }
    private fun setProductsAdapter(productsList: List<Product>) {
        binding.includeProductsLayout.shimmer.startShimmerAnimation()
        binding.includeProductsLayout.shimmer.stopShimmerAnimation()
        binding.includeProductsLayout.rvProducts.visible()

        productsAdapter = ProductsAdapter(CUSTOMER, mContext, productsList, productsItemOnClick)
        binding.includeProductsLayout.rvProducts.adapter = productsAdapter
    }
    private val productsItemOnClick: (Product) -> Unit = { item ->
        //item will be start vending from here if payment done
    }
    private fun hideLoadingViews(){
        binding.includeProductsLayout.shimmer.hide()
        binding.progressbar.hide()
    }
    override fun getResLayout(): Int {
        return R.layout.fragment_redeem
    }

}