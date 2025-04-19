package com.pwrpower.apk.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pwrpower.apk.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pwrpower.apk.ui.fragments.HomeFragment
import com.pwrpower.apk.ui.fragments.MapFragment
import com.pwrpower.apk.ui.fragments.SearchFragment
import com.pwrpower.apk.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    private var selectedItemId: Int = R.id.home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        selectedItemId = savedInstanceState?.getInt("SELECTED_ITEM_ID") ?: R.id.home
        bottomNav.selectedItemId = selectedItemId
        showFragmentFor(selectedItemId)

        bottomNav.setOnItemSelectedListener { item ->
            selectedItemId = item.itemId
            showFragmentFor(item.itemId)
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SELECTED_ITEM_ID", selectedItemId)
    }

    private fun showFragmentFor(itemId: Int) {
        val fragment = when (itemId) {
            R.id.home -> HomeFragment()
            R.id.search -> SearchFragment()
            R.id.map -> MapFragment()
            R.id.settings -> SettingsFragment()
            else -> HomeFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}