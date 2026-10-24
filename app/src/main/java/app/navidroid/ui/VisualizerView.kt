package app.navidroid.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import app.navidroid.R
import kotlin.math.sin

/**
 * Lightweight animated bars for the home media widget (v1).
 *
 * This is motion, not measurement: the bars dance while music plays and
 * settle to a flat baseline when paused. No audio session, no extra
 * permission, works with every player. A real FFT-driven visualizer
 * can replace the height function later without touching the widget.
 */
class VisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barCount = 28
    private val paints: List<Paint>
    private var phase = 0.0
    private var playing = false

    private val ticker = object : Runnable {
        override fun run() {
            if (!playing) return
            phase += 0.16
            invalidate()
            postDelayed(this, 50)
        }
    }

    init {
        val colors = listOf(
            R.color.neonPink,
            R.color.neonGreen,
            R.color.neonCyan
        ).map { ContextCompat.getColor(context, it) }
        paints = colors.map { color ->
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                style = Paint.Style.FILL
            }
        }
    }

    fun setPlaying(isPlaying: Boolean) {
        if (isPlaying == playing) return
        playing = isPlaying
        removeCallbacks(ticker)
        if (playing) post(ticker) else invalidate()
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(ticker)
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val gap = 3f * resources.displayMetrics.density
        val barWidth = (width - gap * (barCount - 1)) / barCount
        if (barWidth <= 0) return
        val maxH = height.toFloat()
        val minH = 2f * resources.displayMetrics.density
        val baseline = height - minH
        for (i in 0 until barCount) {
            val h = if (playing) {
                val wave = (0.5 + 0.5 * sin(phase * 1.3 + i * 0.6)) *
                    (0.5 + 0.5 * sin(phase * 0.7 + i * 0.31))
                (minH + (maxH - minH) * (0.15 + 0.85 * wave)).toFloat()
            } else {
                minH
            }
            val left = i * (barWidth + gap)
            canvas.drawRoundRect(
                left, baseline - h + minH, left + barWidth, baseline + minH,
                barWidth / 2, barWidth / 2, paints[i % paints.size]
            )
        }
    }
}
