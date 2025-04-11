package com.pwrpower.apk.ui.others

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pwrpower.apk.R

class LanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_language)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val language1: ConstraintLayout = findViewById(R.id.languagePoland) // Poland
        val language2: ConstraintLayout = findViewById(R.id.languageEngland) // England
        val language3: ConstraintLayout = findViewById(R.id.languageGermany) // Germany

        language1.setOnClickListener {
            finish()
        }

        language2.setOnClickListener {
            finish()
        }

        language3.setOnClickListener {
            finish()
        }
    }
}