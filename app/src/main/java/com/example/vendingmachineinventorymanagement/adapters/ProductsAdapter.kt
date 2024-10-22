package com.example.vendingmachineinventorymanagement.adapters

import android.content.Context
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.CURRENCY_SYMBOL
import com.example.vendingmachineinventorymanagement.databinding.CustomItemCellBinding
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.utils.baseclasses.BaseAdapter
import com.example.vendingmachineinventorymanagement.extensionfunctions.loadImageIntoImageView
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.ADMIN

class ProductsAdapter(
    val type: String,
    val mContext: Context,
    var dataList: List<Product>,
    val productsItemOnClick: (Product) -> Unit
) :
    BaseAdapter<CustomItemCellBinding, Product>(
        R.layout.custom_item_cell,
        dataList
    ) {
    override fun bindView(
        binding: CustomItemCellBinding,
        item: Product,
        position: Int
    ) {
        val viewProductResponse: Product = dataList[position]
        binding.apply {
            if (type== ADMIN){
                btnOrder.setText("view")
            }else{
                btnOrder.setText("vend")
            }
            tvSlot.text = "SLOT #"+viewProductResponse.slotNumber.toString()
            tvItemName.text = viewProductResponse.productName
            tvMaxItems.text ="Max Items "+ viewProductResponse.maxQuantity.toString()
            tvAvailableItems.text ="Available Items "+ viewProductResponse.availableQuantity.toString()
            tvItemPrice.text = CURRENCY_SYMBOL+ " " +viewProductResponse.productSellingPrice
            // Load image using Glide or any other image loading library
            val placeholderImage = R.drawable.baseline_money_24
            loadImageIntoImageView(mContext, dataList[position].productImage.toString(),
                imageViewItem, placeholderImage, placeholderImage)
            // Set click listeners or other actions if needed
            itemParentView.setOnClickListener {
                productsItemOnClick(viewProductResponse)
            }
            btnOrder.setOnClickListener {
                productsItemOnClick(dataList[position])
            }
        }
    }
    fun updateProductList(newList: List<Product>) {
        dataList = newList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}