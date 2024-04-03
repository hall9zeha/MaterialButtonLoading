package com.barryzeha.materialbuttonloading.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.widget.RelativeLayout
import android.widget.TextView
import com.barryzeha.materialbuttonloading.R
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

    private var cornerRadius:Float? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()
    private var colorStroke:Int? = null

    private val textView: TextView

    init {
        setBackgroundColor(Color.TRANSPARENT)
        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        textView = TextView(context)
        textView.setPadding(22,22,22,22)
        // Configurar el texto y otros atributos del TextView según sea necesario
        textView.text = "Texto de ejemplo"
        textView.setTextColor(Color.WHITE)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        textView.gravity= CENTER
        params.addRule(CENTER_IN_PARENT)
        textView.layoutParams=params

        // Añadir el TextView como vista hijo
        addView(textView)
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

        val array: TypedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimaryInverse,android.R.attr.colorPrimary))
        val defaultTextColor = array.getColor(0, 0)
        val defaultColor = array.getColor(1, 0)

        val buttonText = arr.getString(R.styleable.loadingButtonStyleable_text)
        cornerRadius = arr.getDimension(R.styleable.loadingButtonStyleable_cornerRadius, 50F)
        val loading = arr.getBoolean(R.styleable.loadingButtonStyleable_loading, false)
        val enabled = arr.getBoolean(R.styleable.loadingButtonStyleable_enabled, true)
        val colorText = arr.getString(R.styleable.loadingButtonStyleable_textColor)
        val strokeColor = arr.getString(R.styleable.loadingButtonStyleable_colorStroke)

        val textColor = if(!colorText.isNullOrEmpty()) Color.parseColor(colorText) else defaultTextColor
        colorStroke = if(!strokeColor.isNullOrEmpty()) Color.parseColor(strokeColor) else defaultColor

        arr.recycle()
        isEnabled = enabled
        setText(buttonText)
        //progressBar.setAnimation(lottieResId)
        setLoading(loading)
        setLoadingColor(defaultTextColor)
        setTextColor(textColor)
    }

    fun setLoading(loading: Boolean){
        isClickable = !loading //Disable clickable when loading
        if(loading){
            /*buttonTextView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE*/

        } else {
           /* buttonTextView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE*/
        }
    }
    fun setTextColor(color:Int){

    }

    fun setLoadingColor(color:Int){


    }
    fun setText(text : String?) {

    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val corners=
        cornerRadius?.let{
            24F
        }?.run{
            cornerRadius
        }

        val array: TypedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorPrimary))
        val defaultColor = array.getColor(0, 0)

        // Dibujar el fondo redondeado
        paint.style = Paint.Style.FILL
        paint.color = defaultColor
        rect.set(0f, 0f, width, height)
        path.reset()
        path.addRoundRect(rect, corners!!, corners, Path.Direction.CW)
        canvas.drawPath(path, paint)

        // Dibujar el borde redondeado
        paint.style = Paint.Style.STROKE
        val cStroke=colorStroke?.let{defaultColor}?.run{colorStroke}
        paint.color = cStroke!!
        paint.strokeWidth = 3f // Establecer el ancho del trazo si es necesario
        canvas.drawRoundRect(rect, corners, corners, paint)


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

        setMeasuredDimension(width, height)
    }

}

