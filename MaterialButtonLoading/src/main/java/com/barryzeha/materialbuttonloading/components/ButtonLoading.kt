package com.barryzeha.materialbuttonloading.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
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

    private val padding=8
    private var cornerRadius:Float? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()
    private var colorStroke:Int? = null
    private var backgroundColor:Int?=null

    private var textView: TextView

    init {
        setBackgroundColor(Color.TRANSPARENT)
        //setRippleEffect()
        textView = TextView(context)
        setUpChildViews()
        loadAttr(attrs, defStyleAttr)

    }
    private fun setRippleEffect() {
        // Define el color del efecto Ripple
        val rippleColor = Color.parseColor("#80FFFFFF") // Por ejemplo, un color semi-transparente blanco

        // Crea el fondo del botón con efecto Ripple
        val rippleDrawable = RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            background,
            null
        )

        // Aplica el fondo con efecto Ripple al botón
        background = rippleDrawable
    }
    private fun setUpChildViews(){
        val params = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        textView.setPadding(22,32,22,22)
        // Configurar el texto y otros atributos del TextView según sea necesario
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        textView.gravity= Gravity.CENTER
        params.addRule(CENTER_IN_PARENT)
        textView.layoutParams=params

        addView(textView)
    }
    @SuppressLint("CustomViewStyleable")
    private fun loadAttr(attrs: AttributeSet?, defStyleAttr: Int) {
        val arr = context.obtainStyledAttributes(
            attrs,
            R.styleable.loadingButtonStyleable,
            defStyleAttr,
            0
        )

        val array: TypedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimaryInverseNoDisable,android.R.attr.colorPrimary))
        val defaultTextColor = array.getColor(0, 0)
        val defaultColor = array.getColor(1, 0)

        val buttonText = arr.getString(R.styleable.loadingButtonStyleable_text)
        cornerRadius = arr.getDimension(R.styleable.loadingButtonStyleable_cornerRadius, 50F)
        val loading = arr.getBoolean(R.styleable.loadingButtonStyleable_loading, false)
        val enabled = arr.getBoolean(R.styleable.loadingButtonStyleable_enabled, true)
        val colorText = arr.getString(R.styleable.loadingButtonStyleable_textColor)
        val textSize = arr.getDimensionPixelSize(R.styleable.loadingButtonStyleable_textSize,15)
        val allCaps = arr.getBoolean(R.styleable.loadingButtonStyleable_allCaps,false)
        val strokeColor = arr.getString(R.styleable.loadingButtonStyleable_colorStroke)
        val colorBackground = arr.getString(R.styleable.loadingButtonStyleable_colorBackground)

        val textColor = if(!colorText.isNullOrEmpty()) Color.parseColor(colorText) else defaultTextColor
        colorStroke = if(!strokeColor.isNullOrEmpty()) Color.parseColor(strokeColor) else defaultColor
        backgroundColor = if(!colorBackground.isNullOrEmpty()) Color.parseColor(colorBackground) else defaultColor

        arr.recycle()
        isEnabled = enabled
        setText(buttonText)

        setLoading(loading)
        setLoadingColor(defaultTextColor)
        setTextColor(textColor)
        setTextSize(textSize)
        setAllCaps(allCaps)
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
        textView.setTextColor(color)
    }

    fun setLoadingColor(color:Int){

    }
    fun setText(text : String?) {
       textView.text=if(text.isNullOrEmpty())"Button" else text

    }
    fun setTextSize(size:Int){
       textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size.toFloat())
    }
    fun setAllCaps(isAllCaps:Boolean){
        if(isAllCaps)textView.isAllCaps=true
        else textView.isAllCaps=false
    }
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

        val rectLeft = padding.toFloat()
        val rectTop = padding.toFloat()
        val rectRight = width - padding.toFloat()
        val rectBottom = height - padding.toFloat()

        val corners=
        cornerRadius?.let{
            24F
        }?.run{
            cornerRadius
        }

        val array: TypedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorPrimary))
        val defaultColor = array.getColor(0, 0)

        // Establecer los colores por defecto
        val cStroke=colorStroke?.let{defaultColor}?.run{colorStroke}
        val cBackground=backgroundColor?.let{defaultColor}?.run{backgroundColor}

        // Dibujar el fondo redondeado
        paint.style = Paint.Style.FILL
        paint.color = cBackground!!
        // Aplicamos paddin al objeto que se dibuja dentro de nuestro contentMain
        rect.set(rectLeft, rectTop, rectRight, rectBottom)

        path.reset()
        path.addRoundRect(rect, corners!!, corners, Path.Direction.CW)
        canvas.drawPath(path, paint)

        // Dibujar el borde redondeado
        paint.style = Paint.Style.STROKE

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

        // Para redimencionar el textview cuando cambie el ancho del contenMain que es el relative layout
        textView.measure(width, height)
        val minWidth = resources.getDimensionPixelSize(R.dimen.button_width_default) // Define esta dimensión en tus recursos
        textView.layoutParams.width =if (width < minWidth) minWidth else width
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)

        // Si no colocamos  measureChild el texto dentro de textview no se centra
        measureChild(textView,width,height)
        setMeasuredDimension(width, height)
    }
    companion object {
        fun convertDpToPixels(dp: Float, context: Context): Int {
            val scale = context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }

    }
}

