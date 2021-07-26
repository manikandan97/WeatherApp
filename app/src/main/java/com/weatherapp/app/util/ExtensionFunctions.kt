package com.weatherapp.app.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast

object ExtensionFunctions {

    fun Activity.showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun Context?.showToast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { Toast.makeText(it, text, duration).show() }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.INVISIBLE
    }

}