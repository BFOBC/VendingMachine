package com.example.vendingmachineinventorymanagement.utils

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun View.singleClickListener(klik: (View) -> Unit) {
    this.setOnClickListener(object : SingleClickListener() {
        override fun onSingleClick(v: View?) {
            v?.also { klik(it) }
        }
    })
}


internal abstract class SingleClickListener : View.OnClickListener {
    private val MIN_CLICK_INTERVAL: Long = 1000
    var mLastClickTime: Long = 0

    abstract fun onSingleClick(v: View?)

    override fun onClick(v: View?) {
        if (mLastClickTime <= 0) {
            mLastClickTime = SystemClock.uptimeMillis()
            onSingleClick(v)
            return
        }

        if (SystemClock.uptimeMillis() - mLastClickTime <= MIN_CLICK_INTERVAL) {
            return
        }

        mLastClickTime = SystemClock.uptimeMillis()

        onSingleClick(v)
    }
    // Extension function for Context
    fun Context.showConfirmationDialog(message: String, positiveCallback: () -> Unit, negativeCallback: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> positiveCallback.invoke() }
            .setNegativeButton("No") { _, _ -> negativeCallback.invoke() }
            .show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun Context.getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()

        // Format date
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = currentDateTime.format(dateFormatter)

        // Get day name
        val dayName = currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

        // Format time in 24-hour format
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val formattedTime = currentDateTime.format(timeFormatter)

        return "Date: $formattedDate, Day: $dayName, Time: $formattedTime"
    }
}