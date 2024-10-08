package com.example.vendingmachineinventorymanagement.extensionfunctions

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vendingmachineinventorymanagement.R

fun loadImageIntoImageView(
    context: Context,
    imageUrl: String,
    imageView: ImageView,
    placeholderResId: Int,
    errorResId: Int
) {
    val placeholderImage = R.drawable.ic_no_product_found
    // Define RequestOptions for customization
    val requestOptions = RequestOptions()
        .placeholder(placeholderImage)
        .error(placeholderImage)

    // Load the image into the ImageView using Glide
    Glide.with(context)
        .load(imageUrl)
        .apply(requestOptions)
        .into(imageView)
}
