package com.pwrpower.apk.ble

import android.Manifest
import android.os.Handler
import android.os.Looper
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.pwrpower.apk.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class BleManager (private val context: Context, private val bleCallback: BleCallback)  {

    private val bluetoothAdapter: BluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var controlChar: BluetoothGattCharacteristic
    private lateinit var gpsChar: BluetoothGattCharacteristic
    private var timeoutRunnable: Runnable? = null

    private val handler = Handler(Looper.getMainLooper())
    private var scanCallback: ScanCallback? = null

    private lateinit var serviceUuid: UUID
    private lateinit var controlUuid: UUID
    private lateinit var gpsUuid: UUID
    private val secretToken: String = "SECRET_TOKEN_ABC123"
    private lateinit var bleName: String
    private val scanPeriod: Long = 10000 // 10 seconds

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN])
    fun startScan(bluetoothName: String, serviceUUID: String, controlUUID: String, gpsUUID: String) {
        this.bleName = bluetoothName
        this.serviceUuid = UUID.fromString(serviceUUID)
        this.controlUuid = UUID.fromString(controlUUID)
        this.gpsUuid = UUID.fromString(gpsUUID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                bleCallback.onError("BLUETOOTH_SCAN")
                return
            }
        }
        val scanner = bluetoothAdapter.bluetoothLeScanner ?: return
        scanCallback = object : ScanCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
            override fun onScanResult(type: Int, result: ScanResult) {
                val deviceName = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                        null
                    }
                    else {
                        result.device.name
                    }
                } catch (e: SecurityException) {
                    bleCallback.onError("BLUETOOTH_CONNECT")
                    return
                }
                if (deviceName == bleName) {
                    timeoutRunnable?.let { handler.removeCallbacks(it) }
                    scanner.stopScan(this)
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        bleCallback.onError("BLUETOOTH_CONNECT")
                        return
                    }
                    connect(result.device)
                }
            }

            override fun onScanFailed(code: Int) {
                bleCallback.onError("${context.getString(R.string.ble_manager_error1)} $code")
            }
        }

        scanner.startScan(scanCallback)
        timeoutRunnable = Runnable {
            bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
            bleCallback.onError(context.getString(R.string.ble_manager_error2))
        }
        handler.postDelayed(timeoutRunnable!!, scanPeriod)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connect(device: BluetoothDevice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                bleCallback.onError("BLUETOOTH_CONNECT")
                return
            }
        }
        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS || newState != BluetoothProfile.STATE_CONNECTED) {
                    bleCallback.onError(context.getString(R.string.ble_manager_error3))
                    gatt.close()
                    return
                }
                gatt.discoverServices()
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val service = gatt.getService(serviceUuid) ?: return
                controlChar = service.getCharacteristic(controlUuid)
                gpsChar = service.getCharacteristic(gpsUuid)
                gatt.setCharacteristicNotification(gpsChar, true)
                sendCommand(secretToken)
                bleCallback.onConnected()
            }

            @Deprecated("Deprecated in Java")
            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                if (characteristic.uuid == gpsUuid) {
                    val dataBytes = characteristic.value
                    if (dataBytes.size >= 16) {
                        val lat = ByteBuffer.wrap(dataBytes, 0, 8).order(ByteOrder.LITTLE_ENDIAN).double
                        val lon = ByteBuffer.wrap(dataBytes, 8, 8).order(ByteOrder.LITTLE_ENDIAN).double
                        bleCallback.onGpsReceived("$lat,$lon")
                    }
                }
            }


            override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    bleCallback.onError(context.getString(R.string.ble_manager_error4))
                }
            }
        })
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun sendCommand(cmd: String) {
        if (!::controlChar.isInitialized) {
            bleCallback.onError(context.getString(R.string.ble_manager_error5))
            return
        }
        controlChar.setValue(cmd.toByteArray())
        bluetoothGatt?.writeCharacteristic(controlChar)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun cleanup() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
    }

    interface BleCallback {
        fun onConnected()
        fun onGpsReceived(data: String)
        fun onError(msg: String)
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}