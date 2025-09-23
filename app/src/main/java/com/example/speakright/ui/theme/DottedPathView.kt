package com.example.speakright.ui.theme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class DottedPathView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.parseColor("#6C63FF")
        style = Paint.Style.STROKE
        strokeWidth = 6f
        pathEffect = DashPathEffect(floatArrayOf(20f, 15f), 0f)
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()
        val widthMid = width / 2f
        val heightTotal = height.toFloat()

        // Define amplitude for horizontal swing (half width)
        val amplitude = width / 3f

        // Each segment height (total view height divided by number of curves)
        val numCurves = 1  // Each DottedPathView represents a single curve between two buttons
        val segmentHeight = heightTotal / numCurves

        // Start from top center
        path.moveTo(widthMid, 0f)

        // Alternate curve direction
        val endX = if ((parent as? View)?.let { it.id % 2 == 0 } == true) widthMid - amplitude else widthMid + amplitude
        val controlX = widthMid
        val controlY = segmentHeight / 2

        // Draw smooth quadratic curve
        path.quadTo(controlX, controlY, endX, segmentHeight)

        canvas.drawPath(path, paint)
    }
}
