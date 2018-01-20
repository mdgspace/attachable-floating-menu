package com.sdsmdg.rohan.attachablefloatingmenu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.os.Build
import android.support.animation.FloatPropertyCompat
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class Fab @JvmOverloads constructor(
        context: Context,
        attrSet: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ImageButton(context, attrSet, defStyleAttr) {

    companion object {
        @Suppress("unused")
        const val LOG_TAG = "Fab"
        @ColorInt
        const val DEFAULT_COLOR_NORMAL = Color.WHITE
        @ColorInt
        const val DEFAULT_COLOR_SELECTED: Int = 0xFFB71C1C.toInt() // Bug in compiler
    }

    enum class Size(val value: Float) {
        NORMAL(50f), MINI(30f)
    }

    // XML attributes
    var fabSize by bgInvalidateable(Size.NORMAL)
    var fabState = 0
        set(value) {
            currFabColor = when (value) {
                1 -> fabColorSelected
                0 -> fabColorNormal
                else -> throw IllegalArgumentException("fabState can be 0/1")
            }
        }
    var shadowRadius by bgInvalidateable(0f) { _, value ->
        if (value != 0f) setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        else setLayerType(View.LAYER_TYPE_NONE, null)
    }
    var xShadowOffset by bgInvalidateable(0f)
    var yShadowOffset by bgInvalidateable(0f)
    var shadowColor by bgInvalidateable(Color.BLACK)
    var fabColorNormal by bgInvalidateable(DEFAULT_COLOR_NORMAL)
    var fabColorSelected by bgInvalidateable(DEFAULT_COLOR_SELECTED)
    private var currFabColor by bgInvalidateable(DEFAULT_COLOR_NORMAL)


    private val desiredFabDiameter
        get() = fabSize.value.toPixel()
    private val desiredIconSize get() = 24f.toPixel()
    private val actualFabDiameter
        get() = if (desiredFabDiameter < width) desiredFabDiameter else width.toFloat()
    private val iconSize get() = desiredIconSize + (actualFabDiameter - desiredFabDiameter) / 2f
    private val iconBounds = RectF()
    private val mBounds = RectF()
    private val arr = IntArray(2)
    val pCenterX by lazy { left + width / 2f }
    val pCenterY by lazy { top + height / 2f }
    val scale = object : FloatPropertyCompat<Fab>("scale") {
        override fun getValue(`object`: Fab?) = scaleX

        override fun setValue(`object`: Fab?, value: Float) {
            scaleX = value
            scaleY = value
        }
    }
    val mAnim = FabScaleAnim(this)
    private var isBackgroundDrawn = false
    private var isViewMeasured = false

    // Init XML Attrs.
    init {
        val a = context.theme.obtainStyledAttributes(attrSet,
                R.styleable.Fab, defStyleAttr, defStyleRes)
        try {
            fabSize = when (a.getInteger(R.styleable.Fab_size, 0)) {
                0 -> Size.NORMAL
                else -> Size.MINI
            }
            fabColorNormal = a.getColor(
                    R.styleable.Fab_color_normal, DEFAULT_COLOR_NORMAL)
            fabColorSelected = a.getColor(
                    R.styleable.Fab_color_selected, DEFAULT_COLOR_SELECTED)
            shadowRadius = a.getDimension(R.styleable.Fab_shadow_radius, 0f)
            xShadowOffset = a.getDimension(R.styleable.Fab_x_shadow_offset, 0f)
            yShadowOffset = a.getDimension(R.styleable.Fab_y_shadow_offset, 0f)
            shadowColor = a.getColor(R.styleable.Fab_shadow_color, Color.BLACK)
        } finally {
            a.recycle()
        }
    }

    // initializer code
    init {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.abc_ic_star_black_48dp))
        currFabColor = fabColorNormal
        background = CircleDrawable(desiredFabDiameter, currFabColor,
                shadowRadius, xShadowOffset, yShadowOffset, shadowColor)
        isBackgroundDrawn = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && shadowRadius == 0f) {
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
        isViewMeasured = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val halfIconSize = iconSize / 2f
        iconBounds.left = w / 2f - halfIconSize
        iconBounds.right = w / 2f + halfIconSize
        iconBounds.top = w / 2f - halfIconSize
        iconBounds.bottom = w / 2f + halfIconSize
        mBounds.left = 0f; mBounds.top = 0f
        mBounds.right = w.toFloat(); mBounds.bottom = h.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        if (mBounds.contains(iconBounds)) {
            drawable?.setBounds(iconBounds)
            drawable?.draw(canvas)
        }
    }

    fun contains(x: Float, y: Float): Boolean {
        getLocationInWindow(arr)
        return (x - arr[0]) in 0f..(1.37f * width * scaleX) &&
                (y - arr[1]) in 0f..(1.37f * height * scaleY)
    }

    private inline fun <T> bgInvalidateable(
            initialValue: T,
            crossinline onChange: (oldValue: T, newValue: T) -> Unit = { _, _ -> }
    ): ReadWriteProperty<Any?, T> = object : ObservableProperty<T>(initialValue) {
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            if (isBackgroundDrawn) {
                background = if (isViewMeasured) {
                    CircleDrawable(actualFabDiameter, currFabColor,
                            shadowRadius, xShadowOffset, yShadowOffset, shadowColor)
                } else {
                    CircleDrawable(desiredFabDiameter, currFabColor,
                            shadowRadius, xShadowOffset, yShadowOffset, shadowColor)
                }
            }
            onChange(oldValue, newValue)
        }
    }

}
