package com.example.vendingmachineinventorymanagement.extensionfunctions

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import com.example.vendingmachineinventorymanagement.R

fun Context.getProgressDialog(cancelable: Boolean): Dialog {
    val progressDialog = Dialog(this)
    progressDialog.setContentView(R.layout.progress_dialog)
    progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    progressDialog.setCancelable(cancelable)
    progressDialog.window?.setGravity(Gravity.CENTER)
    return progressDialog
}