package com.example.vendingmachineinventorymanagement.extensionfunctions

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import com.example.vendingmachineinventorymanagement.databinding.CustomErrorDialogBinding
import com.example.vendingmachineinventorymanagement.utils.singleClickListener

private var customErrorDialog: Dialog? = null

fun Context.showCustomErrorDialog(
    title: String,
    description: String,
    buttonText: String,
    @DrawableRes imageResId: Int? = null,
    onButtonClick: () -> Unit,
) {
    if (customErrorDialog != null && customErrorDialog?.isShowing == true) {
        return // Dialog is already showing, do not create another one
    }

    val binding = CustomErrorDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = Dialog(this)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    // Set content view using View Binding
    dialog.setContentView(binding.root)

    // Set data to views
    binding.textHeading.text = title
    binding.textDescription.text = description
    binding.btnRefresh.text = buttonText
    // Set image if resource ID is provided, otherwise set default image
    if (imageResId != null) {
        binding.appCompatImageView.setImageResource(imageResId)
    }

    // Set button click listener
    binding.btnRefresh.singleClickListener {
        onButtonClick.invoke()
        dialog.dismiss()
        customErrorDialog = null // Reset dialog reference after dismissal
    }
    binding.imgClose.singleClickListener {
        dialog.dismiss()
        customErrorDialog = null // Reset dialog reference after dismissal
    }
    // Adjust dialog size
    val displayMetrics = this.resources.displayMetrics
    val width = displayMetrics.widthPixels * 0.85
    dialog.window?.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

    // Show the dialog
    dialog.show()

    customErrorDialog = dialog // Assign dialog reference
}

