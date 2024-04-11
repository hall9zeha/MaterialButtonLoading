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
import androidx.core.graphics.ColorUtils
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.barryzeha.materialbuttonloading.R
import com.barryzeha.materialbuttonloading.common.adjustAlpha
import com.barryzeha.materialbuttonloading.common.alpha
import com.barryzeha.materialbuttonloading.common.appliedDimension
import com.barryzeha.materialbuttonloading.common.convertColorReferenceToHex
import com.barryzeha.materialbuttonloading.common.mColorList
import kotlin.math.min


/**
 * Project MaterialButtonLoading
 * Created by Barry Zea H. on 30/3/24.
 * Copyright (c)  All rights reserved.
 **/

const val TEXT_COLOR_PRIMARY_INVERSE = 0
const val COLOR_PRIMARY=1
const val MATERIAL_COLOR_ON_PRIMARY=2
const val MATERIAL_COLOR_SURFACE=3
const val COLOR_TRANSPARENT=4

class ButtonLoading @JvmOverloads constructor(
    context:Context,
    attrs: AttributeSet? = null,
    defStyleAttr:Int=0,
    ):RelativeLayout(context,attrs,defStyleAttr), View.OnTouchListener {

    enum class StyleButton(val value:Int){
        NORMAL_STYLE(0), OUTLINE_STYLE(1),TEXT_BUTTON_STYLE(2)
    }
    private var styleButton:Int?=null
    private val padding=8
    private var cornerRadius:Float? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()

    // Attributes
    private var attrLoading:Boolean?=null
    private var attrEnabled:Boolean?=null
    private var attrStrokeWidth:Float?=null
    private var attrTextColor:String?=null
    private var attrTextSize:Int?=null
    private var attrAllCaps:Boolean?=null
    private var attrColorStroke:String?=null
    private var attrColorBackground:String?=null
    private var attrColorRipple:String?=null
    private var attrProgressColor:String?=null
    // Attributes
    private var defaultTextColor:Int?=null
    private var defaultButtonColor:Int?=null
    private var textColor:Int?=null
    private var colorStroke:Int? = null
    private var backgroundColor:Int?=null
    private var progressColor:Int?=null
    private var buttonText:String?=null
    private var textView: TextView
    private var imageView:ImageView
    private var circularProgressDrawable:CircularProgressDrawable

    private var rippleX: Float? = null
    private var rippleY: Float? = null
    private var rippleRadius: Float? = null
    private var strokeWidth:Float?=null
    var maxRippleRadius: Float = 200f
    var rippleColor: Int? = 0x88888888.toInt()

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

        val nightModeFlags = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        defaultTextColor = try {
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                mColorList(context).getColor(MATERIAL_COLOR_ON_PRIMARY, TEXT_COLOR_PRIMARY_INVERSE)
            } else {
                mColorList(context).getColor(TEXT_COLOR_PRIMARY_INVERSE, TEXT_COLOR_PRIMARY_INVERSE)
            }
        }catch(e:Exception){
            mColorList(context).getColor(COLOR_PRIMARY, TEXT_COLOR_PRIMARY_INVERSE)
        }

        defaultButtonColor = mColorList(context).getColor(COLOR_PRIMARY, TEXT_COLOR_PRIMARY_INVERSE)
        styleButton = arr.getInt(R.styleable.loadingButtonStyleable_styleButton,0)
        buttonText = arr.getString(R.styleable.loadingButtonStyleable_text)
        cornerRadius = arr.getDimension(R.styleable.loadingButtonStyleable_cornerRadius, 50F)
        attrLoading = arr.getBoolean(R.styleable.loadingButtonStyleable_loading, false)
        attrEnabled = arr.getBoolean(R.styleable.loadingButtonStyleable_enabled, true)
        attrStrokeWidth = arr.getDimension(R.styleable.loadingButtonStyleable_strokeWidth,3f)
        attrTextColor = arr.getString(R.styleable.loadingButtonStyleable_textColor)
        attrTextSize = arr.getDimensionPixelSize(R.styleable.loadingButtonStyleable_textSize,0)
        attrAllCaps = arr.getBoolean(R.styleable.loadingButtonStyleable_allCaps,false)
        attrColorStroke = arr.getString(R.styleable.loadingButtonStyleable_colorStroke)
        attrColorBackground = arr.getString(R.styleable.loadingButtonStyleable_colorBackground)
        attrColorRipple = arr.getString(R.styleable.loadingButtonStyleable_colorRipple)
        attrProgressColor = arr.getString(R.styleable.loadingButtonStyleable_progressColor)

        textColor = if(!attrTextColor.isNullOrEmpty()) Color.parseColor(attrTextColor) else defaultTextColor!!
        colorStroke = if(!attrColorStroke.isNullOrEmpty()) Color.parseColor(attrColorStroke) else defaultButtonColor
        backgroundColor = if(!attrColorBackground.isNullOrEmpty()) Color.parseColor(attrColorBackground) else defaultButtonColor
        rippleColor = if(!attrColorRipple.isNullOrEmpty()) Color.parseColor(attrColorRipple) else rippleColor
        progressColor = if(!attrProgressColor.isNullOrEmpty()) Color.parseColor(attrProgressColor) else convertColorReferenceToHex(defaultTextColor)

        init()
        arr.recycle()


    }
    private fun init(){
        strokeWidth=attrStrokeWidth
        isEnabled = attrEnabled!!
        setText(buttonText)
        setLoading(attrLoading!!)
        setLoadingColor(defaultTextColor!!)
        setTextColor(textColor!!)
        setTextSize(attrTextSize!!)
        setAllCaps(attrAllCaps!!)
        setButtonStyle(styleButton!!)
    }
    // Public functions
    /**
     * Sets the loading state of the button.
     *
     * @param loading A boolean value indicating whether the button is in a loading state.
     *                If true, the button is set to the loading state, hiding the text and showing a loading indicator.
     *                If false, the button returns to its normal state, displaying the text and hiding the loading indicator.
     */
    fun setLoading(loading: Boolean){
        isEnabled=!loading
        isClickable = !loading
        if(loading){
            textView.visibility = View.INVISIBLE
            imageView.visibility = View.VISIBLE

        } else {
           textView.visibility = View.VISIBLE
            imageView.visibility = View.INVISIBLE
        }
    }
    /**
     * Sets the color of text button.
     *
     * @param color The color to be set for the text button. This can be a color value (e.g., Color.RED) or
     *              a color resource ID (e.g., R.color.my_color).
     */
    private fun setTextColor(color:Int?){
        textView.setTextColor(color?:defaultTextColor!!)
    }
    /**
     * Sets the color of the loading indicator.
     *
     * @param color The color to be set for the loading indicator. This can be a color value (e.g., Color.RED) or
     *              a color resource ID (e.g., R.color.my_color).
     */
    fun setLoadingColor(color:Int?){
        circularProgressDrawable.setColorSchemeColors(color!!)
    }
    /**
     * Sets the background color of the button.
     *
     * @param color The color value or color resource ID to be set as the background color of the button.
     *              It can be a color value (e.g., Color.RED) or a color resource (e.g., R.color.my_color).
     */
    fun setColorBackground(color:Int){
        paint.color=color
    }
    /**
     * Sets the text of the button.
     *
     * @param text The text to be displayed on the button. If null or empty, a default text "Button" will be set.
     *             This can also be a color resource ID (e.g., R.string.my_text_button) to set the text color.
     */
    fun setText(text : String?) {
       textView.text=if(text.isNullOrEmpty())"Button" else text
    }
    /**
     * Sets the text size of the button.
     *
     * @param size The size of the text. It can be specified in pixels as a float value,
     *             or it can be a dimension resource ID (e.g., R.dimen.text_size).
     */
    fun setTextSize(size:Int){
        if(size<=0){
            textView.setTextSize(15f)
        }else{ textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size.toFloat())}
    }
    /**
     * Sets the stroke width of the button's outline.
     *
     * @param size The width of the stroke. It can be specified in pixels as a float value,
     *             or it can be a dimension resource ID (e.g., R.dimen.stroke_width).
     */
    fun setStrokeWidth(size:Float){
        strokeWidth = convertDpToPixels(size,context).toFloat()
    }
    /**
     * Sets whether the text of the button should be displayed in all capital letters.
     *
     * @param isAllCaps A boolean value indicating whether the text should be displayed in all capital letters.
     *                  If true, the text will be displayed in all capital letters.
     *                  If false, the text will be displayed as-is without capitalization.
     */
    fun setAllCaps(isAllCaps:Boolean){
        if(isAllCaps)textView.isAllCaps=true
        else textView.isAllCaps=false
    }
    /**
     * Establishment the button style.
     * @param style The button style can be any of the following:
     *
     *             - StyleButton.NORMAL_STYLE: Style normal button.
     *             - StyleButton.OUTLINE_STYLE: Style without stroke and background button.
     *             - StyleButton.TEXT_BUTTON_STYLE: Style only text.
     */
    fun setButtonStyle(style:StyleButton){
        when(style){
            StyleButton.NORMAL_STYLE->setButtonNormalStyle()
            StyleButton.OUTLINE_STYLE->{setOutlineStyle()}
            StyleButton.TEXT_BUTTON_STYLE->setTextButtonStyle()
            else->setButtonNormalStyle()
        }
    }
    private fun setButtonStyle(style: Int){
        when(style){
            StyleButton.NORMAL_STYLE.value->setButtonNormalStyle()
            StyleButton.OUTLINE_STYLE.value->{setOutlineStyle()}
            StyleButton.TEXT_BUTTON_STYLE.value->setTextButtonStyle()
            else->setButtonNormalStyle()
        }
    }
    // Public functions

    private var ripplePaint = Paint().apply {
        color = backgroundColor!!.adjustAlpha(0.2f)
    }

    @SuppressLint("ResourceType")
    private fun setOutlineStyle(){
        backgroundColor = if(backgroundColor==defaultButtonColor) null else backgroundColor
        textColor=if(textColor==defaultTextColor)null else textColor
        progressColor=if(progressColor==defaultTextColor) null else progressColor
        rippleColor = rippleColor?:mColorList(context).getColor(COLOR_PRIMARY, TEXT_COLOR_PRIMARY_INVERSE)
        backgroundColor=backgroundColor?:mColorList(context).getColor(MATERIAL_COLOR_SURFACE, COLOR_PRIMARY)
        setTextColor(textColor?:mColorList(context).getColor(COLOR_PRIMARY, COLOR_PRIMARY))
        strokeWidth=strokeWidth?:appliedDimension(1,this)
        progressColor = progressColor?:mColorList(context).getColor(COLOR_PRIMARY, COLOR_PRIMARY)

    }
    private fun setTextButtonStyle(){

        strokeWidth = if(strokeWidth == 3f) null else strokeWidth
        backgroundColor = if(backgroundColor == defaultButtonColor) null else backgroundColor
        textColor=if(textColor==defaultTextColor)null else textColor
        progressColor=if(progressColor==defaultTextColor) null else progressColor
        colorStroke=if(colorStroke==defaultButtonColor)null else colorStroke
        colorStroke=colorStroke?:mColorList(context).getColor(MATERIAL_COLOR_SURFACE, COLOR_PRIMARY)
        backgroundColor = backgroundColor?:mColorList(context).getColor(MATERIAL_COLOR_SURFACE, COLOR_PRIMARY)
        setTextColor(textColor?:mColorList(context).getColor(COLOR_PRIMARY, COLOR_PRIMARY))
        progressColor= progressColor?:mColorList(context).getColor(COLOR_PRIMARY, COLOR_PRIMARY)
        rippleColor=rippleColor?:mColorList(context).getColor(MATERIAL_COLOR_ON_PRIMARY, COLOR_PRIMARY)

        ripplePaint=Paint().apply {
            color = mColorList(context).getColor(COLOR_TRANSPARENT, COLOR_PRIMARY)
        }

    }
    private fun setButtonNormalStyle(){
        setColorBackground(backgroundColor?:mColorList(context).getColor(COLOR_PRIMARY, COLOR_PRIMARY))
        setTextColor(textColor?:mColorList(context).getColor(TEXT_COLOR_PRIMARY_INVERSE, COLOR_PRIMARY))
        setLoadingColor(progressColor?:mColorList(context).getColor(MATERIAL_COLOR_ON_PRIMARY, TEXT_COLOR_PRIMARY_INVERSE))
        rippleColor=rippleColor?:0x88888888.toInt()
    }
    // Button styles


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
                    ripplePaint.color = color.adjustAlpha(0.5f)
                    invalidate()
                    postDelayed(this, 15L)
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
        ripplePaint.color = rippleColor!!.adjustAlpha(0.4f)
        animationExpand.run()
    }

    fun stopRipple() {
        if (rippleRadius != null) {
            animationFade.run()
            ripplePaint.color = rippleColor!!.adjustAlpha(0f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        // para que stroke width crezca hacia el centro y no afuera de los márgenes
        val strokeWidth = appliedDimension(strokeWidth?.toInt() ?: 2, this)
        val halfStrokeWidth = strokeWidth / 2f // Calcula la mitad del ancho del borde

        val rectLeft = 6f + halfStrokeWidth // Ajusta el borde hacia adentro sumando la mitad del ancho del borde
        val rectTop = padding.toFloat() + halfStrokeWidth // Ajusta el borde hacia adentro sumando la mitad del ancho del borde
        val rectRight = width - 4f - halfStrokeWidth // Ajusta el borde hacia adentro restando la mitad del ancho del borde
        val rectBottom = height - padding.toFloat() - halfStrokeWidth // Ajusta el borde hacia adentro restando la mitad del ancho del borde

        //**************************************

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

        paint.strokeWidth = strokeWidth
        // Primero agregamos el efecto ripple
        canvas.drawRoundRect(rect, corners, corners, ripplePaint)
        // Luego nuestro diseño principal
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
        if (widthMode == MeasureSpec.EXACTLY) {
            newWidth = width
            if (heightMode == MeasureSpec.EXACTLY) {
                newHeight = height
            } else {
                newHeight = textView.measuredHeight
            }
        } else {
            if (width < textViewWidth) {
                newWidth = textView.measuredWidth + 48
            } else {
                newWidth = if ((textViewWidth + 48) > width) width + 22
                else width - 56
            }

            if (heightMode == MeasureSpec.EXACTLY) {
                newHeight = height
            } else {
                newHeight = textView.measuredHeight
            }

        }

        textView.measure(newWidth, newHeight)

        // Si no colocamos  measureChild el texto dentro de textview no se centra
        measureChild(imageView,width,height)
        measureChild(textView,newWidth,newHeight)
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


