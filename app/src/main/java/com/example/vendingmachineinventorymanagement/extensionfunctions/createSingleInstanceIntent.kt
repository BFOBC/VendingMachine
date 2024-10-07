package com.example.vendingmachineinventorymanagement.extensionfunctions

// Extension function to create an intent for an activity with single instance launch mode
import android.content.Context
import android.content.Intent

// Extension function to create an intent for an activity with single instance launch mode
inline fun <reified T : Any> Context.createSingleInstanceIntent(): Intent {
    return Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
}
