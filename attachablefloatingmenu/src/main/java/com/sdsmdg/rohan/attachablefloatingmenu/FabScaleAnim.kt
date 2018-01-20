package com.sdsmdg.rohan.attachablefloatingmenu

import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.view.View.*

class FabScaleAnim(fab: Fab) {

    private val anim = SpringAnimation(fab, fab.scale)
    private val springForce = SpringForce(1f)

    private val MAX_START_VELOCITY = 200f.toPixel()

    companion object {
        const val MAX_SCALE = 1.27f
        const val MIN_SCALE = 1f
    }

    init {
        springForce.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
        springForce.stiffness = SpringForce.STIFFNESS_LOW
        anim.minimumVisibleChange = DynamicAnimation.MIN_VISIBLE_CHANGE_SCALE
        anim.setMinValue(MIN_SCALE).setMaxValue(MAX_SCALE).setSpring(springForce)
        anim.addUpdateListener { _, _, _ ->
            fab.setLayerType(LAYER_TYPE_HARDWARE, null)
        }
        anim.addEndListener { _, _, _, _ ->
            fab.setLayerType(if (fab.shadowRadius == 0f) LAYER_TYPE_NONE else LAYER_TYPE_SOFTWARE, null)
        }
    }

    fun animateTo(scale: Float) {
        anim.animateToFinalPosition(scale)
    }

}
