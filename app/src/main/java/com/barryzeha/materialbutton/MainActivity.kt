package com.barryzeha.materialbutton

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.barryzeha.materialbuttonloading.components.ButtonLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val contentMain = findViewById<LinearLayout>(R.id.main)

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val btn = ButtonLoading(this)
        btn.setLoading(true)
        btn.setLoadingColor(Color.parseColor("#d23232"))
        btn.setProgressType(ButtonLoading.ProgressType.DOTS)
        btn.setButtonStyle(ButtonLoading.StyleButton.OUTLINE_STYLE)
        btn.setAllCaps(false)
        btn.setText("Iniciar sesión")
        btn.layoutParams=layoutParams

        contentMain.addView(btn)

         val button = findViewById<ButtonLoading>(R.id.btnLoading)
          button.setLoading(false)
          button.setOnClickListener {
              CoroutineScope(Dispatchers.IO).launch {
                  launch(Dispatchers.Main){
                      button.setLoading(true)
                  }
                  delay(3000)
                  launch(Dispatchers.Main) {
                      button.setLoading(false)
                  }
              }
              //Toast.makeText(this, "Hola mundo", Toast.LENGTH_SHORT).show()
          }

    }
}