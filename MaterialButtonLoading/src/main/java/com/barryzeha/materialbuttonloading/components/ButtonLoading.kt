package com.barryzeha.materialbuttonloading.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.barryzeha.materialbuttonloading.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlin.math.min


/**
 * Project MaterialButtonLoading
 * Created by Barry Zea H. on 30/3/24.
 * Copyright (c)  All rights reserved.
 **/

class ButtonLoading @JvmOverloads constructor(
    context:Context,
    attrs: AttributeSet? = null,
    defStyleAttr:Int=0,
    ):RelativeLayout(context,attrs,defStyleAttr) {

    private val cornerRadius = 20f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()
    private val contentMain:RelativeLayout
    private var progressBar: CircularProgressIndicator
    private val buttonTextView: TextView
    private var textColor = androidx.appcompat.R.attr.titleTextColor


    init {
        paint.style = Paint.Style.FILL
        val array: TypedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorPrimary))
        val defaultTextColor = array.getColor(0, 0)
        paint.color = resources.getColor(android.R.color.holo_blue_light)

        val root = LayoutInflater.from(context).inflate(R.layout.button_loading_layout, this, true)
        contentMain = root.findViewById(R.id.contentMain)
        buttonTextView = root.findViewById(R.id.tvButton)
        progressBar = root.findViewById(R.id.pbLoading)
        loadAttr(attrs, defStyleAttr)
    }

    @SuppressLint("CustomViewStyleable")
    private fun loadAttr(attrs: AttributeSet?, defStyleAttr: Int) {
        val arr = context.obtainStyledAttributes(
            attrs,
            R.styleable.loadingButtonStyleable,
            defStyleAttr,
            0
        )

        val buttonText = arr.getString(R.styleable.loadingButtonStyleable_text)
        val loading = arr.getBoolean(R.styleable.loadingButtonStyleable_loading, false)
        val enabled = arr.getBoolean(R.styleable.loadingButtonStyleable_enabled, true)
        val colorText = arr.getString(R.styleable.loadingButtonStyleable_textColor)
        val array: TypedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimaryInverse))
        val defaultTextColor = array.getColor(0, 0)
        textColor = if(!colorText.isNullOrEmpty()) Color.parseColor(colorText) else defaultTextColor

        arr.recycle()
        isEnabled = enabled
        buttonTextView.isEnabled = enabled
        setText(buttonText)
        //progressBar.setAnimation(lottieResId)
        setLoading(loading)
        setLoadingColor(defaultTextColor)
        setTextColor(textColor)
    }

    fun setLoading(loading: Boolean){
        isClickable = !loading //Disable clickable when loading
        if(loading){
            buttonTextView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

        } else {
            buttonTextView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
    fun setTextColor(color:Int){
        buttonTextView.setTextColor(color)
    }

    fun setLoadingColor(color:Int){
        progressBar.trackColor = color

    }
    fun setText(text : String?) {
        buttonTextView.text = text
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        buttonTextView.isEnabled = enabled
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

        // Dibuja el fondo redondeado
        rect.set(0f, 0f, width, height)
        path.reset()
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.drawPath(path, paint)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("onMeasure: ",widthMeasureSpec.toString())
        Log.d("onMeasure: ",heightMeasureSpec.toString())
        val defaultButtonWidth = resources.getDimensionPixelSize(R.dimen.button_width_default)
        val defaultButtonHeight = resources.getDimensionPixelSize(R.dimen.button_height_default)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width= when(widthMode){
            MeasureSpec.EXACTLY->widthSize
            MeasureSpec.AT_MOST->min(defaultButtonWidth, widthSize)
            MeasureSpec.UNSPECIFIED->defaultButtonWidth
            else->defaultButtonWidth
        }
        val height= when(heightMode){
            MeasureSpec.EXACTLY->heightSize
            MeasureSpec.AT_MOST->min(defaultButtonHeight, heightSize)
            MeasureSpec.UNSPECIFIED->defaultButtonHeight
            else->defaultButtonHeight
        }
        measureChild(contentMain,width, height)
        setMeasuredDimension(width, height)
    }
}