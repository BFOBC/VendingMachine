package com.example.vendingmachineinventorymanagement.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class Product(
    @SerializedName("slotId") var slotId: Int? = null,
    @SerializedName("slotNumber") var slotNumber: Int? = 0,
    @SerializedName("trayId") var trayId: Int? = null,
    @SerializedName("availableQuantity") var availableQuantity: Int? = null,
    @SerializedName("maxQuantity") var maxQuantity: Int? = null,
    @SerializedName("productSku") var productSku: String? = null,
    @SerializedName("productId") var productId: String? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("productDescription") var productDescription: String? = null,
    @SerializedName("productCostPrice") var productCostPrice: String? = null,
    @SerializedName("productSellingPrice") var productSellingPrice: String? = null,
    @SerializedName("productImage") var productImage: String? = null,
    @SerializedName("productQuantity") var productQuantity: String? = null,
    @SerializedName("productCategoryName") var productCategoryName: String? = null,
    @SerializedName("productCategoryId") var productCategoryId: Int? = null,
    @SerializedName("productTypeId") var productTypeId: Int? = null,
    @SerializedName("productURL") var productURL: String? = null,
)
