package com.pwrpower.apk.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pwrpower.apk.R
import com.pwrpower.apk.api.CarInfoResponse
import com.pwrpower.apk.api.CarStatusModel
import com.pwrpower.apk.api.LocationModel
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.SendLocationResponse
import com.pwrpower.apk.api.UpdateCarStatusResponse
import com.pwrpower.apk.ble.BleManager
import com.pwrpower.apk.ui.bottomSheet.ReviewsBottomSheet
import com.pwrpower.apk.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarInfoFragment : Fragment(), BleManager.BleCallback {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var bleManager: BleManager
    private lateinit var bluetoothName: String
    private lateinit var serviceUUID: String
    private lateinit var controlUUID: String
    private lateinit var gpsUUID: String
    private var controlFragment: ControlFragment? = null
    private var carCostAmount: Double = 0.0
    private var carNameValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_car_info, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.carInfoToolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this@CarInfoFragment.requireContext(), R.color.onBackground))
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        val reviewsButton: TextView = view.findViewById(R.id.showReviewsButton)
        reviewsButton.setOnClickListener {
            val bottomSheet = ReviewsBottomSheet()
            val bundle = Bundle()
            bundle.putString("carId", arguments?.getString("carId"))
            bottomSheet.arguments = bundle
            bottomSheet.show((activity as AppCompatActivity).supportFragmentManager, "ReviewsBottomSheet")
        }

        bleManager = BleManager(requireContext(), this)

        val requiredPermissions = getRequiredPermissions()

        val connectButton = view.findViewById<TextView>(R.id.connectButton)
        connectButton.setOnClickListener {
            if (!isBluetoothEnabled()) {
                requestEnableBluetooth()
            }
            else if (!isLocationEnabled()) {
                requestEnableLocation()
            }
            else if (hasPermissions()) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.dialog_connecting1))
                    .setMessage(getString(R.string.dialog_connecting2))
                    .setPositiveButton(getString(R.string.dialog_connecting3)) { _, _ ->
                        RetrofitInstance.api.updateCarStatus(arguments?.getString("carId")?.toInt() ?: 0,CarStatusModel("rented")).enqueue(object : Callback<UpdateCarStatusResponse> {
                            override fun onResponse(call: Call<UpdateCarStatusResponse>, response: Response<UpdateCarStatusResponse>) {
                                if (response.isSuccessful) {
                                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                        try {
                                            bleManager.startScan(bluetoothName, serviceUUID, controlUUID, gpsUUID)
                                        } catch (e: SecurityException) {
                                            infoPermissions()
                                        }
                                    } else {
                                        infoPermissions()
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<UpdateCarStatusResponse>, t: Throwable) {
                                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    .setNegativeButton(getString(R.string.dialog_connecting4), null)
                    .show()
            }
            else {
                permissionLauncher.launch(requiredPermissions)
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                try {
                    bleManager.startScan(bluetoothName, serviceUUID, controlUUID, gpsUUID)
                } catch (e: SecurityException) {
                    infoPermissions()
                }
            } else {
                infoPermissions()
            }
        }

        downloadCarInfo(view)

        return view
    }

    private fun downloadCarInfo(view: View) {
        RetrofitInstance.api.getCarDetails(arguments?.getString("carId")?.toInt() ?: 0).enqueue(object : Callback<CarInfoResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<CarInfoResponse>, response: Response<CarInfoResponse>) {
                if (response.isSuccessful) {
                    val carInfo = response.body()
                    if (carInfo != null) {
                        val car = carInfo.car
                        val carName: TextView = view.findViewById(R.id.carName)
                        val carCost: TextView = view.findViewById(R.id.carCost)
                        val carRating: RatingBar = view.findViewById(R.id.ratingBar)
                        val noRating: TextView = view.findViewById(R.id.noRating)
                        val fuelType: TextView = view.findViewById(R.id.fuel_type)
                        val paint: TextView = view.findViewById(R.id.car_paint)
                        val seats: TextView = view.findViewById(R.id.car_seats)
                        val burns: TextView = view.findViewById(R.id.car_burns)
                        val year: TextView = view.findViewById(R.id.car_year)

                        carCostAmount = car?.price ?: 0.0
                        carNameValue = car?.brand + " " + car?.model

                        carName.text = car?.brand + " " + car?.model
                        carCost.text = getString(R.string.cost1) + " " + car?.price.toString() + " " + getString(R.string.cost2)
                        if(car?.rating != 0.0) {
                            carRating.rating = car?.rating?.toFloat() ?: 0f
                            noRating.visibility = View.GONE
                        } else {
                            carRating.visibility = View.GONE
                            noRating.visibility = View.VISIBLE
                        }
                        fuelType.text = car?.fuelType
                        paint.text = car?.color
                        seats.text = car?.seats.toString()
                        burns.text = car?.fuelConsumption.toString() + " " + getString(R.string.liters)
                        year.text = car?.year.toString()

                        bluetoothName = car?.bluetoothName ?: ""
                        serviceUUID = car?.serviceUUID ?: ""
                        controlUUID = car?.controlUUID ?: ""
                        gpsUUID = car?.gpsUUID ?: ""
                    }
                    else {
                        Toast.makeText(requireContext(), getString(R.string.car_is_null), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CarInfoResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE
    }

    override fun onConnected() {
        activity?.runOnUiThread {
            controlFragment = ControlFragment(bleManager)
            val bundle = Bundle()
            bundle.putString("carId", arguments?.getString("carId") ?: "0")
            bundle.putDouble("carCost", carCostAmount)
            bundle.putString("carName", carNameValue)
            controlFragment!!.arguments = bundle

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, controlFragment!!)
                .addToBackStack(null)
                .commit()

        }
    }

    override fun onGpsReceived(data: String) {
        val parts = data.split(",")
        val location = LocationModel(parts[0].toDouble(), parts[1].toDouble())

        RetrofitInstance.api.sendCarLocation(arguments?.getString("carId")?.toInt() ?: 0, location).enqueue(object : Callback<SendLocationResponse> {
            override fun onResponse(call: Call<SendLocationResponse>, response: Response<SendLocationResponse>) { }

            override fun onFailure(call: Call<SendLocationResponse>, t: Throwable) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        requireActivity().runOnUiThread {
            controlFragment?.updateMarkerPosition(parts[0].toDouble(), parts[1].toDouble())
        }
    }

    override fun onError(msg: String) {
        activity?.runOnUiThread {
            if(msg == "BLUETOOTH_CONNECT" || msg == "BLUETOOTH_SCAN") {
                infoPermissions()
                return@runOnUiThread
            }
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDestroy() {
        super.onDestroy()
        bleManager.cleanup()
    }

    private fun hasPermissions(): Boolean {
        return getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
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

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), getString(R.string.bluetooth_on), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.bluetooth_off), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.isEnabled == true
    }

    private fun requestEnableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(enableBtIntent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    private fun requestEnableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
}