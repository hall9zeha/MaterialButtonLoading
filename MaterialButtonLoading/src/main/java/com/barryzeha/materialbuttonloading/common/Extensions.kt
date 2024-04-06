package com.barryzeha.materialbuttonloading.common

import android.graphics.Color
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