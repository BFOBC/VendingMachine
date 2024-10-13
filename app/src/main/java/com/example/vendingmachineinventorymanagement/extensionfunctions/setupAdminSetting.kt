package com.example.vendingmachineinventorymanagement.extensionfunctions

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.utils.singleClickListener

fun ImageView.setupAdminSetting(context: Context, totalAttempts: Int = 4, onAdminLogin: () -> Unit) {
    var count = 0
    this.singleClickListener {
        count++
        val remainingAttempts = totalAttempts - count
        if (remainingAttempts > 0) {
            context.showToast("You are $remainingAttempts away from the Admin Settings")
        }
        if (count == totalAttempts) {
            onAdminLogin.invoke()
            count = 0
        }
    }
}
fun Context.showToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
// Extension function to show a custom toast
fun Context.showCustomToast(message: String) {
    val inflater = LayoutInflater.from(this)
    val layout = inflater.inflate(R.layout.custom_error_toast, null)

    val text = layout.findViewById<TextView>(R.id.tvMsg)
    text.text = message

    val toast = Toast(this)
    toast.setGravity(Gravity.BOTTOM, 0, 20)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}
