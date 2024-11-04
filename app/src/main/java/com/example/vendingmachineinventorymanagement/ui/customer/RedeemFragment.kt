package com.example.vendingmachineinventorymanagement.ui.customer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.adapters.ProductsAdapter
import com.example.vendingmachineinventorymanagement.databinding.FragmentRedeemBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.hide
import com.example.vendingmachineinventorymanagement.extensionfunctions.showCustomErrorDialog
import com.example.vendingmachineinventorymanagement.extensionfunctions.visible
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.testvm.HexDataHelper
import com.example.vendingmachineinventorymanagement.testvm.SerialUtils
import com.example.vendingmachineinventorymanagement.utils.baseclasses.BaseFragment
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.CUSTOMER
import com.example.vendingmachineinventorymanagement.viewmodels.ProductViewModel


class RedeemFragment : BaseFragment<FragmentRedeemBinding>() {
    private val productViewModel: ProductViewModel by viewModels()
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var mContext: Context

    var su: SerialUtils = SerialUtils()
    var no: Int = 0

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?) {
        initViews()
        vendDataInit()

    }
    private fun vendDataInit() {
        su.openAndConnectPort(requireActivity())
    }

    private fun vendProduct(item: Product) {
        try {
            var hdhbyte = HexDataHelper.Int2Short16_2(item.slotNumber ?: -1)

            // Pad lane number to two bytes
            if (hdhbyte.size == 1) {
                val temp = hdhbyte[0]
                hdhbyte = ShortArray(2)
                hdhbyte[0] = 0
                hdhbyte[1] = temp
            }


            val data = byteArrayOf(
                0xFA.toByte(),
                0xFB.toByte(),
                0x06,
                0x05,
                getNextNo().toByte(),
                0x01,
                0x00,
                hdhbyte[0].toByte(),
                hdhbyte[1].toByte(),
                0x00
            )
            data[data.size - 1] = HexDataHelper.computerXor(data, 0, data.size - 1).toByte()
            //  writeCmd(data);
            su.vendItem(data)
        } catch (e: Exception) {
        }
    }

    private fun getNextNo(): Int {
        no++
        if (no >= 255) {
            no = 0
        }
        return no
    }

    private fun initViews() {
        mContext = requireActivity()
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
                mContext.showCustomErrorDialog(
                    "Data not found",
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
        vendProduct(item)
        Toast.makeText(requireActivity(),"Item ready to vend...",Toast.LENGTH_SHORT).show()
    }

    private fun hideLoadingViews() {
        binding.includeProductsLayout.shimmer.hide()
        binding.progressbar.hide()
    }

    override fun getResLayout(): Int {
        return R.layout.fragment_redeem
    }

}