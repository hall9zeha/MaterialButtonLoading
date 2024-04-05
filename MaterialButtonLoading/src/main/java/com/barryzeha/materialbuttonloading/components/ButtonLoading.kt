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
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.barryzeha.materialbuttonloading.R
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * Project MaterialButtonLoading
 * Created by Barry Zea H. on 30/3/24.
 * Copyright (c)  All rights reserved.
 **/

class ButtonLoading @JvmOverloads constructor(
    context:Context,
    attrs: AttributeSet? = null,
    defStyleAttr:Int=0,
    ):RelativeLayout(context,attrs,defStyleAttr), View.OnTouchListener {

    private val padding=8
    private var cornerRadius:Float? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()
    private var colorStroke:Int? = null
    private var backgroundColor:Int?=null

    private var textView: TextView

    private var rippleX: Float? = null
    private var rippleY: Float? = null
    private var rippleRadius: Float? = null
    private var strokeWidth:Float?=null
    var maxRippleRadius: Float = 200f // Retrieve from resources
    var rippleColor: Int = 0x88888888.toInt()

    init {
        setBackgroundColor(Color.TRANSPARENT)
        textView = TextView(context)
        setUpChildViews()
        loadAttr(attrs, defStyleAttr)
        setOnTouchListener(this)

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                 startRipple(event.x, event.y)
            }

            MotionEvent.ACTION_UP -> {
                stopRipple()
            }
        }

        return false
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
        val widthStroke = arr.getDimension(R.styleable.loadingButtonStyleable_strokeWidth,1f)
        val colorText = arr.getString(R.styleable.loadingButtonStyleable_textColor)
        val textSize = arr.getDimensionPixelSize(R.styleable.loadingButtonStyleable_textSize,15)
        val allCaps = arr.getBoolean(R.styleable.loadingButtonStyleable_allCaps,false)
        val strokeColor = arr.getString(R.styleable.loadingButtonStyleable_colorStroke)
        val colorBackground = arr.getString(R.styleable.loadingButtonStyleable_colorBackground)
        val colorRipple = arr.getString(R.styleable.loadingButtonStyleable_colorRipple)

        val textColor = if(!colorText.isNullOrEmpty()) Color.parseColor(colorText) else defaultTextColor
        colorStroke = if(!strokeColor.isNullOrEmpty()) Color.parseColor(strokeColor) else defaultColor
        backgroundColor = if(!colorBackground.isNullOrEmpty()) Color.parseColor(colorBackground) else defaultColor
        rippleColor = if(!colorRipple.isNullOrEmpty()) Color.parseColor(colorRipple) else rippleColor
        strokeWidth = widthStroke

        arr.recycle()
        isEnabled = enabled
        setText(buttonText)

        setLoading(loading)
        setLoadingColor(defaultTextColor)
        setTextColor(textColor)
        setTextSize(textSize)
        setAllCaps(allCaps)
    }
    // # region functions public
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
    // # end region

    /*Ripple try*/

    private val ripplePaint = Paint().apply {
        color = backgroundColor!!

    }

    private val animationExpand = object : Runnable {
        override fun run() {
            rippleRadius?.let { radius ->
                if (radius < maxRippleRadius) {
                    rippleRadius = radius + maxRippleRadius * 0.1f
                    invalidate()
                    postDelayed(this, 20L)
                }
            }
        }
    }

    private val animationFade = object : Runnable {
        override fun run() {
            ripplePaint.color.let { color ->
                if (color.alpha > 10) {
                    ripplePaint.color = color.adjustAlpha(0.6f)
                    invalidate()
                    postDelayed(this, 20L)
                } else {
                    rippleX = null
                    rippleY = null
                    rippleRadius = null
                    invalidate()
                }
            }

        }
    }

    fun startRipple(x: Float, y: Float) {
        rippleX = x
        rippleY = y
        rippleRadius = maxRippleRadius * 0.15f
        ripplePaint.color = rippleColor
        animationExpand.run()
    }

    fun stopRipple() {
        if (rippleRadius != null) {
            animationFade.run()
        }
    }


    /*Ripple try*/
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


        // Dibujar el fondo redondeado
        paint.style = Paint.Style.FILL
        paint.color = backgroundColor!!
        // Aplicamos paddin al objeto que se dibuja dentro de nuestro contentMain
        rect.set(rectLeft, rectTop, rectRight, rectBottom)

        path.reset()
        path.addRoundRect(rect, corners!!, corners, Path.Direction.CW)
        canvas.drawPath(path, paint)

        // Dibujar el borde redondeado
        paint.style = Paint.Style.STROKE

        paint.color = colorStroke!!
        paint.strokeWidth = convertDpToPixels(strokeWidth?:1f,context).toFloat()
        canvas.drawRoundRect(rect, corners, corners, paint)
        canvas.drawRoundRect(rect, corners, corners, ripplePaint)


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
fun Int.adjustAlpha(factor: Float): Int =
    (this.ushr(24) * factor).roundToInt() shl 24 or (0x00FFFFFF and this)

inline val Int.alpha: Int
    get() = (this shr 24) and 0xFF
