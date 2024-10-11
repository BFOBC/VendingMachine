package com.example.vendingmachineinventorymanagement.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("slotId") var slotId: Int? = null,
    @SerializedName("slotNumber") var slotNumber: Int? = 0,
    @SerializedName("trayId") var trayId: Int? = null,
    @SerializedName("availableQuantity") var availableQuantity: Int? = null,
    @SerializedName("maxQuantity") var maxQuantity: Int? = null,
    @SerializedName("productId") var productId: String? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("productDescription") var productDescription: String? = null,
    @SerializedName("productCostPrice") var productCostPrice: Int? = null,
    @SerializedName("productSellingPrice") var productSellingPrice: Int? = null,
    @SerializedName("productImage") var productImage: String? = null,
    @SerializedName("productQuantity") var productQuantity: Int? = null,
    @SerializedName("productCategoryName") var productCategoryName: String? = null,
    @SerializedName("productCategoryId") var productCategoryId: Int? = null,
    @SerializedName("productTypeId") var productTypeId: Int? = null,
    @SerializedName("productURL") var productURL: String? = null,
) : Parcelable
