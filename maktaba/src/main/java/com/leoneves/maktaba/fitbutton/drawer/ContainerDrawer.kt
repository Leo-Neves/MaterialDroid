package com.leoneves.maktaba.fitbutton.drawer

import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import com.leoneves.maktaba.fitbutton.FitButton
import com.leoneves.maktaba.fitbutton.model.FButton
import com.leoneves.maktaba.fitbutton.model.IconPosition
import com.leoneves.maktaba.fitbutton.model.Shape
import com.leoneves.maktaba.fitbutton.util.RippleEffect
import com.leoneves.maktaba.fitbutton.util.Util.pxToDp

/**
 * Drawing the FitButton view container
 * @author Ivan V on 27.03.2019.
 * @version 1.0
 */
internal class ContainerDrawer(val view: FitButton, val button: FButton)
    : Drawer<FitButton, FButton>(view, button) {

    private lateinit var container: GradientDrawable

    override fun draw() {
        initContainer()
        setOrientation()
        drawShape()
    }

    override fun isReady(): Boolean {
        return view.visibility != View.GONE
    }

    private fun initContainer() {
        container = GradientDrawable()
        container.cornerRadius = pxToDp(button.cornerRadius)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = button.elevation
        }
        setButtonColor()
        addRipple()
    }

    private fun setButtonColor() {
        if (!button.enable) {
            if (button.disableColor != 0) {
                container.setColor(button.disableColor)
                container.setStroke(button.borderWidth.toInt(), button.disableColor)
            } else {
                container.setColor(button.btnColor)
                container.setStroke(button.borderWidth.toInt(), button.borderColor)
                container.alpha = getAlpha().toInt()
            }
        } else {
            container.setColor(button.btnColor)
            container.setStroke(button.borderWidth.toInt(), button.borderColor)
        }
    }

    // GradientDrawable get the alpha value range 0 - 255, so the method should be override
    override fun getAlpha(): Float = MAX_ALPHA * (1 - (ALPHA_PERCENTS.toFloat() / 100))

    // Add a ripple effect for the button if it was enabled
    private fun addRipple() {
        view.isEnabled = button.enable
        view.isClickable = button.enable
        view.isFocusable = button.enable
        RippleEffect.createRipple(view,
                button.enableRipple && button.enable,
                button.btnColor,
                button.rippleColor,
                button.cornerRadius,
                button.btnShape,
                container)
    }

    // Set the layout orientation dependent on icon position
    private fun setOrientation() {
        view.orientation = when (button.iconPosition) {
            IconPosition.LEFT, IconPosition.RIGHT -> LinearLayout.HORIZONTAL
            else -> LinearLayout.VERTICAL
        }
    }

    // Draw button shape
    private fun drawShape() {
        container.shape = when (button.btnShape) {
            Shape.RECTANGLE -> GradientDrawable.RECTANGLE
            Shape.OVAL -> GradientDrawable.OVAL
            Shape.SQUARE -> alignSides(GradientDrawable.RECTANGLE)
            Shape.CIRCLE -> alignSides(GradientDrawable.OVAL)
        }
    }

    // Align shape sides
    private fun alignSides(shape: Int) : Int {
        val dimension = if (view.layoutParams != null) {
            defineFitSide(view.layoutParams.width, view.layoutParams.height)
        } else {
            defineFitSide(view.measuredWidth, view.measuredHeight)
        }
        if (view.layoutParams != null) {
            view.layoutParams.width = dimension
            view.layoutParams.height = dimension
        }
        return shape
    }

    // Get a min side or a max side if anyone side equal zero or less
    private fun defineFitSide(w: Int, h: Int) : Int {
        return if (w <= 0 || h <= 0) {
            Math.max(w, h)
        } else {
            Math.min(w, h)
        }
    }

    override fun updateLayout() {
        draw()
    }

}