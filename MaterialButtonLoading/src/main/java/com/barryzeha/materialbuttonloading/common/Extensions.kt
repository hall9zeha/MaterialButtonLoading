package com.barryzeha.materialbuttonloading.common

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import com.barryzeha.materialbuttonloading.components.COLOR_PRIMARY
import com.barryzeha.materialbuttonloading.components.MATERIAL_COLOR_ON_PRIMARY
import com.barryzeha.materialbuttonloading.components.TEXT_COLOR_PRIMARY_INVERSE
import kotlin.math.roundToInt


/**
 * Project MaterialButtonLoading
 * Created by Barry Zea H. on 5/4/24.
 * Copyright (c)  All rights reserved.
 **/

fun Int.adjustAlpha(factor: Float): Int =
    (this.ushr(24) * factor).roundToInt() shl 24 or (0x00FFFFFF and this)

inline val Int.alpha: Int
    get() = (this shr 24) and 0xFF

fun convertColorReferenceToHex(colorRef:Int?):Int{
    // Receive at reference color and extract a Hexadecimal format for convert to Color
    colorRef?.let {color->
        val colorHex = String.format("#%06X", 0xFFFFFF and color)
        return Color.parseColor(colorHex)
    }
    return 0
}

fun appliedDimension(size:Int, view: View)= TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX,
    size.toFloat(),
    view.resources.displayMetrics
)
fun mColorList(context:Context)=
    context.obtainStyledAttributes(intArrayOf(
        android.R.attr.textColorPrimaryInverseNoDisable,
        android.R.attr.colorPrimary,
        com.google.android.material.R.attr.colorOnPrimary,
        com.google.android.material.R.attr.colorSurface,
        android.R.color.transparent
        ))
fun checkIfNightMode(context: Context):Boolean{
    val nightModeFlags = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}