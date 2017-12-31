package com.sdsmdg.rohan.attachablefloatingmenu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.support.animation.FloatPropertyCompat
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageButton


class FloatingActionButton @JvmOverloads constructor(
        context: Context,
        attrSet: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ImageButton(context, attrSet, defStyleAttr, defStyleRes) {

    enum class FabSize { SIZE_NORMAL, SIZE_MINI }

    private companion object {
        const val LOG_TAG = "FloatingActionButton"
        const val SIZE_NORMAL = 50f
        const val SIZE_MINI = 30f
        @ColorInt const val DEFAULT_COLOR_NORMAL = Color.WHITE
        @ColorInt const val DEFAULT_COLOR_SELECTED: Int = 0xFFB71C1C.toInt() // Bug in compiler
    }

    // XML attributes
    var fabSize: FabSize = FabSize.SIZE_NORMAL
        set(value) {
            field = value
            setDefBackground()
        }
    var fabState = 0
        set(value) {
            if (field == value) return
            when (value) {
                1 -> fabColor = fabColorSelected
                0 -> fabColor = fabColorNormal
                else -> throw IllegalArgumentException("fabState can be 0/1")
            }
            setDefBackground()
            field = value
            Log.d("$LOG_TAG/state", "Changed! new val = $value")
        }
    private val fabColorNormal: Int
    private val fabColorSelected: Int
    private var fabColor: Int = DEFAULT_COLOR_NORMAL
    private val fabPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val desiredFabDiameter
        get() = when (fabSize) {
            FabSize.SIZE_NORMAL -> SIZE_NORMAL.toPixel()
            FabSize.SIZE_MINI -> SIZE_MINI.toPixel()
        }
    private inline val fabDiameter get() = width
    private val desiredIconSize get() = 24f.toPixel()
    private val iconSize get() = desiredIconSize + (fabDiameter - desiredFabDiameter) / 2f
    private val boundsRectF = RectF()
    private val viewRectF = RectF()
    private lateinit var circleDrawable: CircleDrawable
    val arr = IntArray(2)
    val pCenterX by lazy { left + width / 2f }
    val pCenterY by lazy { top + height / 2f }
    val scale = object : FloatPropertyCompat<FloatingActionButton>("scale") {
        override fun getValue(`object`: FloatingActionButton?) = scaleX

        override fun setValue(`object`: FloatingActionButton?, value: Float) {
            scaleX = value
            scaleY = value
        }
    }
    val mAnim = FabScaleAnim(this)

    // Init XML Attrs.
    init {
        val a = context.theme.obtainStyledAttributes(attrSet,
                R.styleable.CustomFab, defStyleAttr, defStyleRes)
        try {
            fabSize = when (a.getInteger(R.styleable.CustomFab_size, 0)) {
                0 -> FabSize.SIZE_NORMAL
                else -> FabSize.SIZE_MINI
            }
            fabColorNormal = a.getColor(R.styleable.CustomFab_color_normal, DEFAULT_COLOR_NORMAL)
            fabColorSelected = a.getColor(
                    R.styleable.CustomFab_color_selected, DEFAULT_COLOR_SELECTED)
        } finally {
            a.recycle()
        }
    }

    // initializer code
    init {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.abc_ic_star_black_48dp))
        fabPaint.style = Paint.Style.FILL
        fabPaint.color = fabColorNormal
        fabColor = fabColorNormal
        setDefBackground()
        // setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 6f.toPixel()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = maxOf(desiredFabDiameter, suggestedMinimumWidth.toFloat())
        val minHeight = maxOf(desiredFabDiameter, suggestedMinimumHeight.toFloat())

        var measuredWidth = resolveSize(minWidth.floor(), widthMeasureSpec)
        var measuredHeight = resolveSize(minHeight.floor(), heightMeasureSpec)

        when {
            measuredWidth < measuredHeight -> measuredHeight = measuredWidth
            measuredWidth > measuredHeight -> measuredWidth = measuredHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val halfIconSize = iconSize / 2f
        boundsRectF.left = w / 2f - halfIconSize
        boundsRectF.right = w / 2f + halfIconSize
        boundsRectF.top = w / 2f - halfIconSize
        boundsRectF.bottom = w / 2f + halfIconSize
        viewRectF.left = 0f; viewRectF.top = 0f
        viewRectF.right = w.toFloat(); viewRectF.bottom = h.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        if (viewRectF.contains(boundsRectF)) {
            drawable?.setBounds(boundsRectF)
            drawable?.draw(canvas)
        }
    }

    private var c = 0
    private var prev = c
    fun contains(x: Float, y: Float): Boolean {
        getLocationInWindow(arr)
        //arr[1] -= 24f.toPixel().toInt()
        //val arr = floatArrayOf(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        //matrix.mapPoints(arr)
        if (c - prev > 40) {
            //Log.d("$LOG_TAG/(x, y)", "(${x}, ${y})")
            //Log.d("$LOG_TAG/prop", "(${arr[0]}, ${arr[1]})")
            //Log.d("$LOG_TAG/adj(x, y)", "(${x - arr[0]}, ${y - arr[1]})")
            //Log.d("$LOG_TAG/(w, h)", "(${width * scaleX}, ${height * scaleY})")
            prev = c
        }
        c++
        return (x - arr[0]) in 0f..(1.37f * width * scaleX) &&
                (y - arr[1]) in 0f..(1.37f * height * scaleY)
    }

    private fun setDefBackground() {
        circleDrawable = CircleDrawable(desiredFabDiameter, fabColor,
                0f, 0f, 0f)
        background = circleDrawable
    }

}
