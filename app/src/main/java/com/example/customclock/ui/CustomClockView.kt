package com.example.customclock.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.format.DateUtils.SECOND_IN_MILLIS
import android.util.AttributeSet
import android.view.View
import com.example.customclock.R
import java.util.*
import kotlin.math.max
import kotlin.math.min

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bDial: Drawable
    private val bHour: Drawable
    private val bMinute: Drawable
    private val bSecond: Drawable

    private var calendar : Calendar = Calendar.getInstance()

    private val clockTick = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()

            calendar.timeInMillis = now
            invalidate()

            val delay = SECOND_IN_MILLIS - now % SECOND_IN_MILLIS
            postDelayed(this, delay)
        }
    }

    init {
        bDial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(R.drawable.dial)!!
        } else {
            context.resources.getDrawable(R.drawable.dial)!!
        }

        bHour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(R.drawable.hour)!!
        } else {
            context.resources.getDrawable(R.drawable.hour)!!
        }

        bMinute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(R.drawable.minute)!!
        } else {
            context.resources.getDrawable(R.drawable.minute)!!
        }

        bSecond = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(R.drawable.second)!!
        } else {
            context.resources.getDrawable(R.drawable.second)!!
        }

        initDrawable(bDial)
        initDrawable(bHour)
        initDrawable(bMinute)
        initDrawable(bSecond)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = max(bDial.intrinsicWidth, suggestedMinimumWidth)
        val minHeight = max(bDial.intrinsicHeight, suggestedMinimumHeight)

        setMeasuredDimension(
            resolveSize(minWidth, widthMeasureSpec),
            resolveSize(minHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val w = width
        val h = height

        val saveCount = canvas!!.save()
        canvas.translate((w / 2).toFloat(), (h / 2).toFloat())
        val scale: Float = min(
            w.toFloat() / bDial.intrinsicWidth,
            h.toFloat() / bDial.intrinsicHeight
        )
        if (scale < 1f) {
            canvas.scale(scale, scale, 0f, 0f)
        }

        bDial.draw(canvas)

        val alphaHour = calendar[Calendar.HOUR] * 30f
        canvas.rotate(alphaHour, 0f, 0f)
        bHour.draw(canvas)

        val alphaMinute = calendar[Calendar.MINUTE] * 6f
        canvas.rotate(alphaMinute - alphaHour,0f, 0f)
        bMinute.draw(canvas)

        val alphaSecond = calendar[Calendar.SECOND] * 6f
        canvas.rotate(alphaSecond - alphaMinute, 0f, 0f)
        bSecond.draw(canvas)

        canvas.restoreToCount(saveCount)
    }

    private fun initDrawable(drawable: Drawable) {
        val midX : Int = drawable.intrinsicWidth / 2
        val midY : Int = drawable.intrinsicHeight / 2

        drawable.setBounds(-midX, -midY, midX, midY)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        clockTick.run()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(clockTick)
    }
}