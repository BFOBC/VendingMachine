package com.example.vendingmachineinventorymanagement.ui.media

import android.content.Context
import android.widget.MediaController

class CustomMediaController(context: Context) : MediaController(context) {

    // Override all the methods to prevent user interactions
    override fun show() {}
    override fun show(timeout: Int) {}
    override fun hide() {}
    override fun isShowing(): Boolean {
        return false
    }
}
