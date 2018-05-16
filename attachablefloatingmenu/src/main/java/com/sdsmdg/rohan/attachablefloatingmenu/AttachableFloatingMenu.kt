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
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class AttachableFloatingMenu @JvmOverloads constructor(
        context: Context,
        private val startX: Float = -1f,
        private val startY: Float = -1f,
        attrSet: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ViewGroup(context, attrSet, defStyleAttr) {

    internal var isEntering = false
    internal var motionX by childAnimateable(0.0f)
    internal var motionY by childAnimateable(0.0f)
    internal var isDrawn: Boolean = false

    private val initialDrawingAngle get() = -(startX / width * totalAngle)
    private var totalAngle = 0.0

    // XML attributes
    /**
     * total dist from center to child(in pixels)
     */
    var r by reqLayoutDelegate(95f.toPixel())
    /**
     * least dist from child for starting animation(in pixels)
     */
    var minR by reqLayoutDelegate(75f.toPixel())
    /**
     * separation between children(in degrees)
     */
    var angularSeparation by reqLayoutDelegate(42.0)
    /**
     * radial distance from initial touch pos to the pivot point for each child(in pixels)
     */
    var pivotR by reqLayoutDelegate(40f.toPixel())

    private var coordinate = mutableListOf<Point<Double>>()
    private var theta = mutableListOf<Double>()
    val centerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val interpolator = DecelerateInterpolator(0.8f)
    private val mAngle get() = angularSeparation.toRadians()
    private var isLayoutDone = false

    companion object {
        @Suppress("unused")
        const val LOG_TAG = "AttachableFloatingMenu"
    }

    init {
        val a = context.theme.obtainStyledAttributes(attrSet, R.styleable.Fab, defStyleAttr, 0)
        try {
            r = a.getDimension(R.styleable.AttachableFloatingMenu_r, r)
            minR = a.getDimension(R.styleable.AttachableFloatingMenu_minR, minR)
            pivotR = a.getDimension(R.styleable.AttachableFloatingMenu_pivotR, pivotR)
            angularSeparation = a.getFloat(R.styleable.AttachableFloatingMenu_angularSeparation,
                    angularSeparation.toFloat()).toDouble()
        } finally {
            a.recycle()
        }
    }

    init {
        setWillNotDraw(false)
        centerCirclePaint.style = Paint.Style.STROKE
        centerCirclePaint.strokeWidth = 3f.toPixel()
        centerCirclePaint.color = Color.WHITE
        centerCirclePaint.alpha = 100
        addView(Fab(context), 0)
        addView(Fab(context), 1)
        val smallView = Fab(context)
        smallView.fabSize = Fab.Size.MINI
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
        totalAngle = (childCount - 1) * mAngle
        for (index in 0 until childCount) {
            theta.add(index, index * mAngle + initialDrawingAngle)
            if (theta[index] < -PI) theta[index] += 2 * PI
            if (theta[index] > PI) theta[index] -= 2 * PI
            val childCenterX = startX + r * sin(theta[index])
            val childCenterY = startY - r * cos(theta[index])
            coordinate.add(index, Point(childCenterX, childCenterY))
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount) {
            val child = getChildAt(index) as Fab
            val childLeft = (coordinate[index].x - child.measuredWidth / 2f).floor()
            val childTop = (coordinate[index].y - child.measuredHeight / 2f).floor()
            child.layout(
                    childLeft, childTop,
                    (coordinate[index].x + child.measuredWidth / 2f).floor(),
                    (coordinate[index].y + child.measuredHeight / 2f).floor()
            )
            child.pivotX = (startX - childLeft) + pivotR * sin(theta[index]).toFloat()
            child.pivotY = (startY - childTop) - pivotR * cos(theta[index]).toFloat()
        }
        isLayoutDone = true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(startX, startY, 23.5f.toPixel(), centerCirclePaint)
        isDrawn = true
    }

    private fun animateMe() {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as Fab
            if (checkConditions(i)) {
                val dist = getDistance(child.pCenterX, child.pCenterY, motionX, motionY)
                if (dist > minR) break
                val dScale = interpolator.getInterpolation((1 - dist / minR).toFloat())
                val newScale = dScale * 0.27f + 1
                child.mAnim.animateTo(newScale)
                when {
                    child.contains(motionX, motionY) -> child.fabState = Fab.State.SELECTED
                    else -> child.fabState = Fab.State.NORMAL
                }
            } else if (child.scaleX != 1f) {
                child.fabState = Fab.State.NORMAL
                child.mAnim.animateTo(1f)
            }
        }
    }

    private fun checkConditions(i: Int): Boolean {
        val slopeAngle = -getTheta(startX, startY, motionX, motionY)
        var midMax = theta[i] + mAngle / 2
        var max = midMax
        if (max > PI / 2) {
            midMax = PI / 2
            max -= PI
        }
        var midMin = theta[i] - mAngle / 2
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
        return when {
            min == midMin && slopeAngle in min..midMax -> true
            max != midMax && slopeAngle in -PI / 2..max -> true
            max == midMax && slopeAngle in midMin..max -> true
            min != midMin && slopeAngle in min..PI / 2 -> true
            else -> false
        }
    }

    //private operator fun Float.rangeTo(that: Float) = AbsoluteRange(this, that)

    private fun <T> childAnimateable(initialValue: T) = object : ObservableProperty<T>(initialValue) {
        override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T) = !isEntering
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) = animateMe()
    }

    private fun <T> reqLayoutDelegate(initialValue: T) = object : ObservableProperty<T>(initialValue) {
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            if (isLayoutDone) this@AttachableFloatingMenu.requestLayout()
        }
    }

}
