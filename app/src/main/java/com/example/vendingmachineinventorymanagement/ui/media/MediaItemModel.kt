package com.example.vendingmachineinventorymanagement.ui.media

import com.example.vendingmachineinventorymanagement.utils.enums.MediaType


data class MediaItemModel(
    val type: MediaType, // Enum to distinguish between IMAGE and VIDEO
    val content: String // URL or resource ID depending on the type
)