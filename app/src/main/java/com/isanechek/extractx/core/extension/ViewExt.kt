package com.isanechek.extractx.core.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


fun View.show() {
    if (this.visibility == View.INVISIBLE && this.visibility == View.GONE) this.visibility = View.VISIBLE
}

fun View.hide(gone: Boolean = false) {
    if (visibility == View.VISIBLE) visibility = if (gone) View.GONE else View.INVISIBLE
}

fun View.onClick(function: () -> Unit) {
    setOnClickListener {
        function()
    }
}

infix fun ViewGroup.inflate(layoutResId: Int): View =
    LayoutInflater.from(context).inflate(layoutResId, this, false)