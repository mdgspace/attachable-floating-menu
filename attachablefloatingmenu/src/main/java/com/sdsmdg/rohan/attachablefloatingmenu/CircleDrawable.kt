package com.sdsmdg.rohan.attachablefloatingmenu

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.annotation.ColorInt
import android.util.Log

internal class CircleDrawable(
        val circleDiameter: Float,
        @ColorInt circleColor: Int,
        val blurRadius: Float = 0f,
        xShadowOffset: Float = 0f,
        yShadowOffset: Float = 0f,
        shadowColor: Int = Color.BLACK
) : ShapeDrawable(OvalShape()) {

    companion object {
        const val LOG_TAG = "CircleDrawable"
        const val INVALID_CIRCLE_RADIUS_ERR = "Circle radius must be valid"
    }
    
    val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if (circleDiameter <= 0f) throw IllegalArgumentException(INVALID_CIRCLE_RADIUS_ERR)
        val mBound = Rect()
        when (blurRadius) {
            0f -> {
                mBound.left = 0
                mBound.right = circleDiameter.floor()
                mBound.top = 0
                mBound.bottom = circleDiameter.floor()
            }
            else -> {
                mBound.left = 0
                mBound.right = circleDiameter.floor() + 20
                mBound.top = 0
                mBound.bottom = circleDiameter.floor() + 20
            }
        }
        bounds = mBound
        mPaint.color = circleColor
        mPaint.style = Paint.Style.FILL
        intrinsicWidth = mBound.width()
        intrinsicHeight = mBound.height()

        paint.set(mPaint)

        if (blurRadius != 0f) {
            mPaint.setShadowLayer(blurRadius, xShadowOffset, yShadowOffset, shadowColor)
        }
    }

    override fun draw(canvas: Canvas) {
        when (blurRadius) {
            0f -> super.draw(canvas)
            else -> {
                Log.d("${LOG_TAG}/hw acc", canvas.isHardwareAccelerated.toString())
                canvas.drawCircle(canvas.width / 2f, canvas.height / 2f,
                        circleDiameter / 2f, mPaint)
            }
        }
    }
}
