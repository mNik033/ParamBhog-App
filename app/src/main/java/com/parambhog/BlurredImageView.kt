package com.parambhog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView

class BlurredImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {
    private val contentNode = RenderNode("image")
    private val blurNode = RenderNode("blur")
    private var blurHeight: Int = 0
    private var blurCornerRadius: Float = 0f
    private val path = Path()

    fun setBlurArea(height: Int, cornerRadius: Float) {
        blurHeight = height
        blurCornerRadius = cornerRadius
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        path.reset()
        path.addRoundRect(
            0f, 0f, width.toFloat(), height.toFloat(),
            blurCornerRadius, blurCornerRadius, Path.Direction.CW
        )
        canvas.clipPath(path)

        contentNode.setPosition(0, 0, width, height)
        val rnCanvas = contentNode.beginRecording()
        super.onDraw(rnCanvas)
        contentNode.endRecording()

        canvas.drawRenderNode(contentNode)

        blurNode.setRenderEffect(
            RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
        )
        blurNode.setPosition(0, 0, width, blurHeight)
        blurNode.translationY = (height - blurHeight).toFloat()

        val blurCanvas = blurNode.beginRecording()
        blurCanvas.translate(0f, -(height - blurHeight).toFloat())
        blurCanvas.drawRenderNode(contentNode)
        blurNode.endRecording()

        canvas.drawRenderNode(blurNode)
    }
}