package com.navinfo.vivo.ui

import android.graphics.Color
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.google.android.material.textfield.TextInputLayout

class ViewExtend {
    fun TextInputLayout.markRequiredInRed() {
        hint = buildSpannedString {
            append(hint)
            color(Color.RED) { append(" *") } // Mind the space prefix.
        }
    }
}