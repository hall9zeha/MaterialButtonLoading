package com.barryzeha.materialbuttonloading.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.barryzeha.materialbuttonloading.R
import com.google.android.material.progressindicator.CircularProgressIndicator


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

    private val progressBar: CircularProgressIndicator
    private val buttonTextView: TextView
    private var textColor = androidx.appcompat.R.attr.titleTextColor

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.button_loading_layout, this, true)
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
        //val lottieResId = arr.getResourceId(R.styleable.loadingButtonStyleable_progressIndicator,R.)

        arr.recycle()
        isEnabled = enabled
        buttonTextView.isEnabled = enabled
        setText(buttonText)
        //progressBar.setAnimation(lottieResId)
        setLoading(loading)
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

    fun setText(text : String?) {
        buttonTextView.text = text
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        buttonTextView.isEnabled = enabled
    }

}