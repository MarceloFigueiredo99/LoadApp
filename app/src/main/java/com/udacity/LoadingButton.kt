package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val TAG = "#LAPP LoadingButton"

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> loadingAnimation()
            ButtonState.Completed -> completedAnimation()
        }
    }

    private var buttonColor = 0
    private var textColor = 0
    private var circleColor = 0

    private var loadingWidth = 0f
    private var circleAngle = 0f

    private var loadingAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    private var buttonText = "Download"

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_ButtonColor, 0)
            textColor = getColor(R.styleable.LoadingButton_TextColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_CircleColor, 0)
        }

        loadingWidth = 0f
        circleAngle = 0f
    }

    private val animationDuration = 2500L
    private fun loadingAnimation() {
        // Show circle
        paintCircle.color = circleColor

        // Change text
        buttonText = "We are loading"

        // Animate Loading Bar
        loadingAnimator.setFloatValues(0f, measuredWidth.toFloat())
        loadingAnimator.duration = animationDuration
        loadingAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                isEnabled = true
                buttonState = ButtonState.Completed
            }
        })
        loadingAnimator.addUpdateListener { updatedAnimation ->
            loadingWidth = updatedAnimation.animatedValue as Float
            invalidate()
        }
        loadingAnimator.start()

        // Animate Circle
        circleAnimator.setFloatValues(0f, 360f)
        circleAnimator.duration = animationDuration
        circleAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                buttonState = ButtonState.Completed
            }
        })
        circleAnimator.addUpdateListener { updatedAnimation ->
            circleAngle = updatedAnimation.animatedValue as Float
            invalidate()
        }
        circleAnimator.start()
    }

    private fun completedAnimation() {
        // Hide circle
        paintCircle.color = Color.TRANSPARENT

        // Reset text
        buttonText = "Download"

        // Reset loading and circle
        loadingWidth = 0f
        circleAngle = 0f
    }

    private val paintRect = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40f
        color = buttonColor
    }

    private var paintCircle = Paint().apply {
        style = Paint.Style.FILL
        color = Color.TRANSPARENT
    }

    private val paintText = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 70f
        color = textColor
    }

    private val paintLoading = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 70f
        color = R.color.colorPrimaryDark
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Button background
        val rectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRect(rectangle, paintRect)

        // Button text
        canvas.drawText(buttonText, (width / 2).toFloat(), (height / 2).toFloat(), paintText)

        // Button loading circle
        canvas.drawArc(
            measuredWidth - 250f,
            measuredHeight / 2 - 50f,
            measuredWidth - 150f,
            measuredHeight / 2 + 50f,
            0f,
            circleAngle,
            true,
            paintCircle
        )

        // Loading bar
        canvas.drawRect(0f, 0f, loadingWidth, measuredHeight.toFloat(), paintLoading)

        Log.i(TAG, "Width is $width, height is $height")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

        Log.i(TAG, "onMeasure Width is $widthSize, height is $heightSize")
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Loading
        invalidate()
        return true
    }
}