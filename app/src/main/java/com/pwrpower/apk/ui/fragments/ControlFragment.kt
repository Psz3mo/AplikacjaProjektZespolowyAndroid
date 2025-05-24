package com.pwrpower.apk.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.pwrpower.apk.R
import com.pwrpower.apk.ble.BleManager

class ControlFragment(private val bleManager: BleManager) : Fragment() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_control, container, false)

        val openButton = view.findViewById<Button>(R.id.openButton)
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        val gpsButton = view.findViewById<Button>(R.id.gpsButton)

        openButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                bleManager.sendCommand("OPEN")
            } else {
                infoPermissions()
            }
        }

        closeButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                bleManager.sendCommand("CLOSE")
            } else {
                infoPermissions()
            }
        }

        gpsButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                bleManager.sendCommand("GPS")
            } else {
                infoPermissions()
            }
        }

        return view
    }

    private fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions += Manifest.permission.BLUETOOTH_SCAN
            permissions += Manifest.permission.BLUETOOTH_CONNECT
        }

        return permissions.toTypedArray()
    }

    private fun infoPermissions() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_req))
            .setMessage(getString(R.string.permission_req_text))
            .setPositiveButton(getString(R.string.permission_req_button1)) { _, _ ->
                permissionLauncher.launch(getRequiredPermissions())
            }
            .setNegativeButton(getString(R.string.permission_req_button2), null)
            .show()
    }
}