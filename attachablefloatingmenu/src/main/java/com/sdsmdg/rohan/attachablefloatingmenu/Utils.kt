package com.sdsmdg.rohan.attachablefloatingmenu

import android.content.res.Resources
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import kotlin.math.*

internal data class Point<T>(val x: T, val y: T)

internal fun Double.toRadians() = PI * this / 180.0

internal fun Double.toDeg() = 180 * this / PI

internal fun Float.toPixel(): Float {
    val metrics = Resources.getSystem().getDisplayMetrics()
    return this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

internal fun Float.toDp(): Float {
    val metrics = Resources.getSystem().getDisplayMetrics()
    return this / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

internal fun Drawable.setBounds(rectF: RectF) {
    setBounds(
            rectF.left.floor(),
            rectF.top.floor(),
            rectF.right.floor(),
            rectF.bottom.floor()
    )
}

internal fun Float.floor() = Math.floor(this.toDouble()).toInt()

internal fun Double.floor() = floor(this).toInt()

internal fun getTheta(x1: Float, y1: Float, x2: Float, y2: Float): Double {
    return atan((x2 - x1) / (y2 - y1).toDouble())
}

internal fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Double {
    return sqrt((x2 - x1).pow(2f).toDouble() + (y2 - y1).pow(2f))
}

/*class AbsoluteRange<T : Comparable<T>>(
        override val endInclusive: T,
        override val start: T
) : ClosedRange<T> {

    override fun isEmpty() = false

    override fun contains(value: T): Boolean {
        return (value >= start && value <= endInclusive) ||
                (value >= endInclusive && value <= start)
    }
}*/

enum class Size(val value: Float) {
    NORMAL(50f), MINI(30f)
}
