package com.simple.domain.support

import java.text.DecimalFormat

fun Long.toFormattedPrice(): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(this)
}
