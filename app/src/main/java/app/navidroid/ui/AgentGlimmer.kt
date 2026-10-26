package app.navidroid.ui

import android.animation.ValueAnimator
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.view.animation.LinearInterpolator
import android.widget.TextView

// The agent line's signature: a slow nightshadeNeon wave rolling across the
// words (pink -> green -> cyan -> pink), so it never reads as just another
// white home line. The gradient band starts and ends on pink and repeats,
// so shifting it by exactly one band loops seamlessly — no snap at the seam.
// The ✨ emoji keeps its own colours: colour-emoji glyphs ignore the shader,
// which is fine — the sparkle stays a sparkle while the words glimmer.
// One small TextView, one slow animator, stopped whenever the home screen
// isn't foregrounded, so it costs nothing in the background.
object AgentGlimmer {

    private const val PINK = 0xFFFF10F0.toInt()
    private const val GREEN = 0xFF39FF14.toInt()
    private const val CYAN = 0xFF00FFFF.toInt()
    private const val DURATION_MS = 3200L

    private var animator: ValueAnimator? = null

    fun start(view: TextView) {
        stop(view)
        val width = view.width
        if (width == 0) {
            // Not laid out yet (first populate before measure): retry once
            // the view lands. start() cancels any previous animator, so a
            // second call is safe; the attach check keeps a dead view from
            // picking up an animator after teardown.
            view.post { if (view.isAttachedToWindow) start(view) }
            return
        }
        val colors = intArrayOf(PINK, GREEN, CYAN, PINK)
        val positions = floatArrayOf(0f, 0.33f, 0.66f, 1f)
        val shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            colors, positions, Shader.TileMode.REPEAT
        )
        val matrix = Matrix()
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DURATION_MS
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                val fraction = it.animatedValue as Float
                matrix.setTranslate(fraction * width, 0f)
                shader.setLocalMatrix(matrix)
                view.paint.shader = shader
                view.invalidate()
            }
        }
        animator = anim
        anim.start()
    }

    fun stop(view: TextView) {
        animator?.cancel()
        animator = null
        if (view.paint.shader != null) {
            view.paint.shader = null
            view.invalidate()
        }
    }
}
