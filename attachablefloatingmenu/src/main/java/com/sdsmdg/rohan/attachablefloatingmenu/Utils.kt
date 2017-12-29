package com.sdsmdg.rohan.attachablefloatingmenu

import android.content.res.Resources
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import kotlin.math.*

data class Point<T>(val x: T, val y: T)

fun Double.toRadians() = PI * this / 180.0

fun Double.toDeg() = 180 * this / PI

fun Float.toPixel(): Float {
    val metrics = Resources.getSystem().getDisplayMetrics()
    return this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Float.toDp(): Float {
    val metrics = Resources.getSystem().getDisplayMetrics()
    return this / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Drawable.setBounds(rectF: RectF) {
    setBounds(
            rectF.left.floor(),
            rectF.top.floor(),
            rectF.right.floor(),
            rectF.bottom.floor()
    )
}

fun Float.floor() = Math.floor(this.toDouble()).toInt()

fun Double.floor() = floor(this).toInt()

fun getTheta(x1: Float, y1: Float, x2: Float, y2: Float): Double {
    return atan((x2 - x1) / (y2 - y1).toDouble())
}

fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Double {
    return sqrt((x2 - x1).pow(2f).toDouble() + (y2 - y1).pow(2f))
}

class AbsoluteRange<T : Comparable<T>>(
        override val endInclusive: T,
        override val start: T
) : ClosedRange<T> {

    override fun isEmpty() = false

    override fun contains(value: T): Boolean {
        return (value >= start && value <= endInclusive) ||
                (value >= endInclusive && value <= start)
    }
}

