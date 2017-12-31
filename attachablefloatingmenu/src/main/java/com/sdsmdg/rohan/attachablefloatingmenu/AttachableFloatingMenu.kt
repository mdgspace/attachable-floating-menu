package com.sdsmdg.rohan.attachablefloatingmenu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class AttachableFloatingMenu @JvmOverloads constructor(
        context: Context,
        val startX: Float = -1f,
        val startY: Float = -1f,
        attrSet: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ViewGroup(context, attrSet, defStyleAttr, defStyleRes) {

    var isAnimating = false
    var motionX: Float = 0.0f
        set(value) {
            if (isAnimating) return
            field = value
            animateMe()
        }
    var motionY: Float = 0.0f
        set(value) {
            if (isAnimating) return
            field = value
            animateMe()
        }
    var isDrawn: Boolean = false
    val dTheta get() = -interpolate(startX)
    var totalAngle = 0.0
    val _r = 95f.toPixel()
    val minR = 75f.toPixel()
    val angle = 42.0
    val pivotR = 40f.toPixel()
    var coordinate = mutableListOf<Point<Double>>()
    var theta = mutableListOf<Double>()
    val centerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val interpolator = DecelerateInterpolator(0.8f)

    companion object {
        const val LOG_TAG = "AttachableFloatingMenu"
    }

    init {
        setWillNotDraw(false)
        centerCirclePaint.style = Paint.Style.STROKE
        centerCirclePaint.strokeWidth = 3f.toPixel()
        centerCirclePaint.color = Color.WHITE
        centerCirclePaint.alpha = 100
        addView(FloatingActionButton(context), 0)
        addView(FloatingActionButton(context), 1)
        val smallView = FloatingActionButton(context)
        smallView.fabSize = FloatingActionButton.FabSize.SIZE_MINI
        addView(smallView, 2)
    }

    override fun shouldDelayChildPressedState() = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (index in 0 until childCount) {
            measureChild(getChildAt(index), widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        totalAngle = ((childCount - 1) * angle).toRadians()
        for (index in 0 until childCount) {
            theta.add(index, (index * angle).toRadians() + dTheta)
            if (theta[index] < -PI) theta[index] += 2 * PI
            if (theta[index] > PI) theta[index] -= 2 * PI
            val childCenterX = startX + _r * sin(theta[index])
            val childCenterY = startY - _r * cos(theta[index])
            coordinate.add(index, Point(childCenterX, childCenterY))
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount) {
            val child = getChildAt(index) as FloatingActionButton
            val left = (coordinate[index].x - child.measuredWidth / 2f).floor()
            val top = (coordinate[index].y - child.measuredHeight / 2f).floor()
            child.layout(
                    left, top,
                    (coordinate[index].x + child.measuredWidth / 2f).floor(),
                    (coordinate[index].y + child.measuredHeight / 2f).floor()
            )
            child.pivotX = startX - left + pivotR * sin(theta[index]).toFloat()
            child.pivotY = startY - top - pivotR * cos(theta[index]).toFloat()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(startX, startY, 23.5f.toPixel(), centerCirclePaint)
        isDrawn = true
    }

    /**
     * @return the interpolated angle
     */
    private fun interpolate(x: Float) = x / width * totalAngle

    private fun animateMe() {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as FloatingActionButton
            if (checkConditions(i)) {
                val dist = getDistance(child.pCenterX, child.pCenterY, motionX, motionY)
                if (dist > minR) break
                //child.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                val dScale = interpolator.getInterpolation((1 - dist / minR).toFloat())
                val newScale = dScale * 0.27f + 1
                child.mAnim.animateTo(newScale)
                when {
                    child.contains(motionX, motionY) -> child.fabState = 1
                    else -> child.fabState = 0
                }
            } else if (child.scaleX != 1f) {
                child.fabState = 0
                child.mAnim.animateTo(1f)
            }
        }
    }

    private var c = 0
    private var prev = c
    private fun checkConditions(i: Int): Boolean {
        val slopeAngle = -getTheta(startX, startY, motionX, motionY)
        var midMax = theta[i] + angle.toRadians() / 2
        var max = midMax
        if (max > PI / 2) {
            midMax = PI / 2
            max -= PI
        }
        var midMin = theta[i] - angle.toRadians() / 2
        var min = midMin
        if (min < -PI / 2) {
            midMin = -PI / 2
            min += PI
        }
        if (max < -PI / 2 || min > PI / 2) {
            if (max < -PI / 2) max += PI
            if (min > PI / 2) min -= PI
            midMin = min
            midMax = max
        }
        if (c - prev > 100 && i == 0) {
            /*Log.d("$LOG_TAG/vals", "(${slopeAngle.toDeg()}, ${min.toDeg()}, ${midMin.toDeg()}," +
                    " ${midMax.toDeg()}, ${max.toDeg()})")*/
            prev = c
        }
        c++
        return when {
            min == midMin && slopeAngle in min..midMax -> true
            max != midMax && slopeAngle in -PI / 2..max -> true
            max == midMax && slopeAngle in midMin..max -> true
            min != midMin && slopeAngle in min..PI / 2 -> true
            else -> false
        }
    }

    //private operator fun Float.rangeTo(that: Float) = AbsoluteRange(this, that)


}
