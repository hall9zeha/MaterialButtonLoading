package com.barryzeha.materialbuttonloading.components


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
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
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.barryzeha.materialbuttonloading.R
import com.barryzeha.materialbuttonloading.common.adjustAlpha
import com.barryzeha.materialbuttonloading.common.alpha
import com.barryzeha.materialbuttonloading.common.convertColorReferenceToHex
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
    ):RelativeLayout(context,attrs,defStyleAttr), View.OnTouchListener {

    private val padding=8
    private var cornerRadius:Float? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()
    private var defaultTextColor:Int?=null
    private var colorStroke:Int? = null
    private var backgroundColor:Int?=null
    private var progressColor:Int?=null

    private var textView: TextView
    private var imageView:ImageView
    private var circularProgressDrawable:CircularProgressDrawable

    private var rippleX: Float? = null
    private var rippleY: Float? = null
    private var rippleRadius: Float? = null
    private var strokeWidth:Float?=null
    var maxRippleRadius: Float = 200f
    var rippleColor: Int = 0x88888888.toInt()

    init {
        setBackgroundColor(Color.TRANSPARENT)
        textView = TextView(context)
        imageView = ImageView(context)
        circularProgressDrawable = CircularProgressDrawable(context)

        loadAttr(attrs, defStyleAttr)
        setUpChildViews()
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

        // Setup textView
        textView.setPadding(22,32,22,36)
        // Configurar el texto y otros atributos del TextView según sea necesario
        textView.typeface =  Typeface.create("sans-serif-medium", Typeface.NORMAL)
        textView.gravity= Gravity.CENTER
        params.addRule(CENTER_IN_PARENT)
        textView.layoutParams=params

        // Setup progress drawable
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(progressColor!!)
        circularProgressDrawable.start()

        // Setup imageView
        imageView.setImageDrawable(circularProgressDrawable)
        imageView.layoutParams=params

        addView(textView)
        addView(imageView)

    }
    @SuppressLint("CustomViewStyleable", "ResourceType")
    private fun loadAttr(attrs: AttributeSet?, defStyleAttr: Int) {
        val arr = context.obtainStyledAttributes(
            attrs,
            R.styleable.loadingButtonStyleable,
            defStyleAttr,
            0
        )

        val arrayColors: TypedArray = context.obtainStyledAttributes(intArrayOf(
            android.R.attr.textColorPrimaryInverseNoDisable,
            android.R.attr.colorPrimary,
            com.google.android.material.R.attr.colorOnPrimary
            ))

        val nightModeFlags = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        defaultTextColor = try {
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                arrayColors.getColor(2,0)
            } else {
                arrayColors.getColor(0, 0)
            }
        }catch(e:Exception){
            arrayColors.getColor(1,0)
        }

        val defaultButtonColor = arrayColors.getColor(1, 0)

        val buttonText = arr.getString(R.styleable.loadingButtonStyleable_text)
        cornerRadius = arr.getDimension(R.styleable.loadingButtonStyleable_cornerRadius, 50F)
        val attrLoading = arr.getBoolean(R.styleable.loadingButtonStyleable_loading, false)
        val attrEnabled = arr.getBoolean(R.styleable.loadingButtonStyleable_enabled, true)
        val attrStrokeWidth = arr.getDimension(R.styleable.loadingButtonStyleable_strokeWidth,1f)
        val attrTextColor = arr.getString(R.styleable.loadingButtonStyleable_textColor)
        val attrTextSize = arr.getDimensionPixelSize(R.styleable.loadingButtonStyleable_textSize,0)
        val attrAllCaps = arr.getBoolean(R.styleable.loadingButtonStyleable_allCaps,false)
        val attrColorStroke = arr.getString(R.styleable.loadingButtonStyleable_colorStroke)
        val attrColorBackground = arr.getString(R.styleable.loadingButtonStyleable_colorBackground)
        val attrColorRipple = arr.getString(R.styleable.loadingButtonStyleable_colorRipple)
        val attrProgressColor = arr.getString(R.styleable.loadingButtonStyleable_progressColor)

        val textColor = if(!attrTextColor.isNullOrEmpty()) Color.parseColor(attrTextColor) else defaultTextColor!!
        colorStroke = if(!attrColorStroke.isNullOrEmpty()) Color.parseColor(attrColorStroke) else defaultButtonColor
        backgroundColor = if(!attrColorBackground.isNullOrEmpty()) Color.parseColor(attrColorBackground) else defaultButtonColor
        rippleColor = if(!attrColorRipple.isNullOrEmpty()) Color.parseColor(attrColorRipple) else rippleColor
        progressColor = if(!attrProgressColor.isNullOrEmpty()) Color.parseColor(attrProgressColor) else convertColorReferenceToHex(defaultTextColor)
        strokeWidth = attrStrokeWidth

        arr.recycle()
        isEnabled = attrEnabled
        setText(buttonText)

        setLoading(attrLoading)
        setLoadingColor(defaultTextColor!!)
        setTextColor(textColor)
        setTextSize(attrTextSize)
        setAllCaps(attrAllCaps)
    }
    // Public functions
    fun setLoading(loading: Boolean){
        isEnabled=!loading
        isClickable = !loading
        if(loading){
            textView.visibility = View.INVISIBLE
            imageView.visibility = View.VISIBLE

        } else {
           textView.visibility = View.VISIBLE
            imageView.visibility = View.GONE
        }
    }
    fun setTextColor(color:Int){
        textView.setTextColor(color)
    }

    fun setLoadingColor(color:Int){
        progressColor= convertColorReferenceToHex(color)
    }
    fun setText(text : String?) {
       textView.text=if(text.isNullOrEmpty())"Button" else text

    }
    fun setTextSize(size:Int){
        if(size<=0){
            textView.setTextSize(15f)
        }else{ textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size.toFloat())}
    }
    fun setAllCaps(isAllCaps:Boolean){
        if(isAllCaps)textView.isAllCaps=true
        else textView.isAllCaps=false
    }
    // Public functions

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

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

        val rectLeft = padding.toFloat()
        val rectTop = padding.toFloat()
        val rectRight = width - 4f
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
            MeasureSpec.UNSPECIFIED->ViewGroup.LayoutParams.WRAP_CONTENT
            else->ViewGroup.LayoutParams.WRAP_CONTENT
        }
        val height= when(heightMode){
            MeasureSpec.EXACTLY->heightSize
            MeasureSpec.AT_MOST->min(defaultButtonHeight, heightSize)
            MeasureSpec.UNSPECIFIED->ViewGroup.LayoutParams.WRAP_CONTENT
            else->ViewGroup.LayoutParams.WRAP_CONTENT
        }

        // Para redimencionar el textview cuando cambie el ancho del contenMain que es el relative layout
        val textViewHeight = textView.measuredHeight
        val textViewWidth = textView.measuredWidth
        val newWidth:Int
        var newHeight:Int
        if(widthMode == MeasureSpec.EXACTLY) {
            newWidth = width + 22
            if(heightMode == MeasureSpec.EXACTLY){
                newHeight = height
            }else{
                newHeight = textView.measuredHeight
            }
        }else{
            newWidth = if (width < textViewWidth) textView.measuredWidth + 48 else width - 56

            if(heightMode == MeasureSpec.EXACTLY){
                newHeight = height
            }else{
                newHeight = textView.measuredHeight
            }

        }

        textView.measure(newWidth, newHeight)

        // Si no colocamos  measureChild el texto dentro de textview no se centra
        measureChild(imageView,width,height)
        measureChild(textView,height,width)
        setMeasuredDimension(newWidth, newHeight)

        // Ajustar el ancho del TextView al nuevo ancho del contenedor
        val layoutParams = textView.layoutParams
        layoutParams.width = newWidth - paddingLeft - paddingRight
        layoutParams.height = newHeight + paddingTop + paddingBottom
        textView.layoutParams = layoutParams

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
        )
    }
    companion object {
        fun convertDpToPixels(dp: Float, context: Context): Int {
            val scale = context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }

    }
}

