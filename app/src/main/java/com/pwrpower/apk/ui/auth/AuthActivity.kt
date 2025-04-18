package com.pwrpower.apk.ui.auth

import android.animation.*
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pwrpower.apk.R
import com.pwrpower.apk.ui.main.MainActivity
import com.pwrpower.apk.ui.others.SettingsFormActivity

class AuthActivity : AppCompatActivity() {

    private lateinit var imageCurrent: ImageView
    private lateinit var imageNext: ImageView
    private lateinit var textCurrent: TextView
    private lateinit var textNext: TextView


    private val images = listOf(
        R.drawable.background_des_1,
        R.drawable.background_des_2,
        R.drawable.background_des_3
    )

    private lateinit var descriptions: List<String>

    private var currentIndex = 0
    private val animDuration = 1000L
    private val interval = 6000L

    private val handler = Handler(Looper.getMainLooper())
    private val switchRunnable = object : Runnable {
        override fun run() {
            showNext()
            handler.postDelayed(this, interval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isLoggedIn: Boolean = preferences.getBoolean("isLoggedIn", false)

        if (!preferences.contains("DARK_MODE")) {
            preferences.edit { putBoolean("DARK_MODE", true) }
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            val isDarkMode = preferences.getBoolean("DARK_MODE", false)
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        if (!isLoggedIn) { // Check if user is logged in
            setContentView(R.layout.activity_auth)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        descriptions = listOf(
            getString(R.string.description_1),
            getString(R.string.description_2),
            getString(R.string.description_3)
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val registerButton: Button = findViewById(R.id.signUpButton)
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterEmailActivity::class.java))
        }

        val settingsButton: ImageView = findViewById(R.id.settingIcon)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsFormActivity::class.java))
        }

        imageCurrent = findViewById(R.id.backgroundImage)
        imageNext = findViewById(R.id.backgroundImageNext)
        textCurrent = findViewById(R.id.descriptionTextView)
        textNext = findViewById(R.id.descriptionTextViewNext)

        handler.postDelayed(switchRunnable, interval)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(switchRunnable)
    }

    private fun showNext() {
        val nextIndex = (currentIndex + 1) % images.size

        // Przygotuj next
        imageNext.setImageResource(images[nextIndex])
        imageNext.translationX = imageCurrent.width.toFloat()
        imageNext.alpha = 0f
        imageNext.visibility = ImageView.VISIBLE

        textNext.text = descriptions[nextIndex]
        textNext.translationX = textCurrent.width.toFloat()
        textNext.alpha = 0f
        textNext.visibility = TextView.VISIBLE

        // Animacje out (fade + przesunięcie)
        val imageOut = ObjectAnimator.ofFloat(imageCurrent, "translationX", 0f, -imageCurrent.width.toFloat())
        val imageFadeOut = ObjectAnimator.ofFloat(imageCurrent, "alpha", 1f, 0f)

        val textOut = ObjectAnimator.ofFloat(textCurrent, "translationX", 0f, -textCurrent.width.toFloat())
        val textFadeOut = ObjectAnimator.ofFloat(textCurrent, "alpha", 1f, 0f)

        // Animacje in (fade + przesunięcie)
        val imageIn = ObjectAnimator.ofFloat(imageNext, "translationX", imageNext.translationX, 0f)
        val imageFadeIn = ObjectAnimator.ofFloat(imageNext, "alpha", 0f, 1f)

        val textIn = ObjectAnimator.ofFloat(textNext, "translationX", textNext.translationX, 0f)
        val textFadeIn = ObjectAnimator.ofFloat(textNext, "alpha", 0f, 1f)

        // Połącz animacje
        AnimatorSet().apply {
            duration = animDuration
            playTogether(
                imageOut, imageFadeOut,
                imageIn, imageFadeIn,
                textOut, textFadeOut,
                textIn, textFadeIn
            )
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Zamiana current <-> next
                    imageCurrent.setImageDrawable(imageNext.drawable)
                    imageCurrent.translationX = 0f
                    imageCurrent.alpha = 1f
                    imageNext.visibility = ImageView.GONE

                    textCurrent.text = textNext.text
                    textCurrent.translationX = 0f
                    textCurrent.alpha = 1f
                    textNext.visibility = TextView.GONE
                }
            })
            start()
        }

        currentIndex = nextIndex
    }
}