package com.example.vendingmachineinventorymanagement.extensionfunctions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Extension function to get current date
fun Calendar.getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(this.time)
}

// Extension function to get current time
fun Calendar.getCurrentTime(): String {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return timeFormat.format(this.time)
}

// Extension function to get current date
fun Date.getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(this)
}

// Extension function to get current time
fun Date.getCurrentTime(): String {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(this)
}
// Extension function to get the name of the current day of the week
fun Calendar.getCurrentDayOfWeek(): String {
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    return dayFormat.format(this.time)
}
