package com.navinfo.volvo.ui

import android.graphics.Color
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.markRequiredInRed() {
    hint = buildSpannedString {
        append(hint)// Mind the space prefix.
        color(Color.RED) { append(" *") }
    }
}