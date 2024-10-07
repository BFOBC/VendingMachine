package com.example.vendingmachineinventorymanagement.extensionfunctions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocus = activity.currentFocus
    if (currentFocus != null) {
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}
