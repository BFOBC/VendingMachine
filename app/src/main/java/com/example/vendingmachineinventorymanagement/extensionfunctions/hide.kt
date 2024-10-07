package com.example.vendingmachineinventorymanagement.extensionfunctions

import android.view.View

fun View.hide() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}
fun View.inVisible() {
    this.visibility = View.INVISIBLE
}