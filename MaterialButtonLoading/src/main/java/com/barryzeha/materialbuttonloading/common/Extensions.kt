package com.barryzeha.materialbuttonloading.common

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