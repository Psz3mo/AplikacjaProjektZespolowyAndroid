package com.pwrpower.apk.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pwrpower.apk.R
import com.pwrpower.apk.api.AddPaymentResponse
import com.pwrpower.apk.api.CarStatusModel
import com.pwrpower.apk.api.PaymentModel
import com.pwrpower.apk.api.RentDetailsModel
import com.pwrpower.apk.api.RentResponse
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.UpdateCarStatusResponse
import com.pwrpower.apk.ble.BleManager
import com.pwrpower.apk.ui.main.MainActivity
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.ceil
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ControlFragment(private val bleManager: BleManager) : Fragment()  {

    private lateinit var mapView: MapView
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var markerSource: GeoJsonSource
    private var secondsElapsed = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable
    private var actualDateTime: String = ""
    private var endDateTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(
            requireContext(),
            null,
            WellKnownTileServer.MapLibre
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_control, container, false)

        sharedPreferences = this@ControlFragment.requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)

        val openButton = view.findViewById<Button>(R.id.openDoorButton)
        val closeButton = view.findViewById<Button>(R.id.closeDoorButton)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            val darkModeEnabled = sharedPreferences.getBoolean("DARK_MODE", false)
            val styleUrl = if (darkModeEnabled) {
                "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
            } else {
                "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"
            }

            map.setStyle(styleUrl) { style ->
                val markerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.car_round_icon)
                val bitmap = markerDrawable?.toBitmap()

                if (bitmap != null) {
                    style.addImage("marker", bitmap)
                }

                markerSource = GeoJsonSource("marker-source", Feature.fromGeometry(Point.fromLngLat(0.0, 0.0)))
                style.addSource(markerSource)

                val symbolLayer = SymbolLayer("marker-layer", "marker-source").withProperties(
                    iconImage("marker"),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
                )
                style.addLayer(symbolLayer)

                map.cameraPosition = CameraPosition.Builder().target(LatLng(52.2297, 21.0122)).zoom(15.0).build()
            }
        }

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

        val disconnectButton = view.findViewById<Button>(R.id.disconnectButton)
        disconnectButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_end_rental1))
                .setMessage(getString(R.string.dialog_end_rental2))
                .setPositiveButton(getString(R.string.dialog_end_rental3)) { _, _ ->
                    RetrofitInstance.api.updateCarStatus(arguments?.getString("carId")?.toInt() ?: 0, CarStatusModel("activated")).enqueue(object : Callback<UpdateCarStatusResponse> {
                        override fun onResponse(call: Call<UpdateCarStatusResponse>, response: Response<UpdateCarStatusResponse>) {
                            if (response.isSuccessful) {
                                disconnect()
                            } else {
                                Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<UpdateCarStatusResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton(getString(R.string.dialog_end_rental4), null)
                .show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            this.isEnabled = false

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_end_rental1))
                .setMessage(getString(R.string.dialog_end_rental2))
                .setPositiveButton(getString(R.string.dialog_end_rental3)) { _, _ ->
                    val carId = arguments?.getString("carId")?.toIntOrNull()
                    if (carId != null) {
                        RetrofitInstance.api.updateCarStatus(carId, CarStatusModel("activated")).enqueue(object : Callback<UpdateCarStatusResponse> {
                            override fun onResponse(call: Call<UpdateCarStatusResponse>, response: Response<UpdateCarStatusResponse>) {
                                if (response.isSuccessful) {
                                   disconnect()
                                } else {
                                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<UpdateCarStatusResponse>, t: Throwable) {
                                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
                .setNegativeButton(getString(R.string.dialog_end_rental4)) { _, _ ->
                    this.isEnabled = true
                }
                .show()
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

    fun updateMarkerPosition(lat: Double, lon: Double) {
        if (::markerSource.isInitialized) {
            val newPoint = Point.fromLngLat(lon, lat)
            val feature = Feature.fromGeometry(newPoint)

            activity?.runOnUiThread {
                markerSource.setGeoJson(feature)

                mapView.getMapAsync { map ->
                    val currentLatLng = LatLng(lat, lon)

                    val visibleRegion = map.projection.visibleRegion
                    val bounds = visibleRegion.latLngBounds

                    if (!bounds.contains(currentLatLng)) {
                        val cameraPosition = CameraPosition.Builder()
                            .target(currentLatLng)
                            .zoom(map.cameraPosition.zoom)
                            .build()
                        map.cameraPosition = cameraPosition
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE

        actualDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val timerTextView = view.findViewById<TextView>(R.id.estimatedTime)

        timerRunnable = object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                val hours = secondsElapsed / 3600
                val minutes = (secondsElapsed % 3600) / 60
                val seconds = secondsElapsed % 60

                timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                secondsElapsed++
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(timerRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun disconnect() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            handler.removeCallbacks(timerRunnable)
            endDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            bleManager.disconnect()
            summary()
        } else {
            infoPermissions()
        }
    }

    private fun summary(){
        val carCost = arguments?.getDouble("carCost") ?: 0.0

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val start = LocalDateTime.parse(actualDateTime, formatter)
        val end = LocalDateTime.parse(endDateTime, formatter)

        val diffInSeconds = Duration.between(start, end).seconds
        val minutes = kotlin.math.ceil(diffInSeconds / 60.0).toInt()
        val fullCarCost = carCost * minutes

        val userId = sharedPreferences.getInt("accountId", 0)

        RetrofitInstance.api.addPayment(userId, PaymentModel(-fullCarCost, "Payment for car rental")).enqueue(object : Callback<AddPaymentResponse> {
            override fun onResponse(call: Call<AddPaymentResponse>, response: Response<AddPaymentResponse>) {
                if (response.isSuccessful) {
                    val transaction = response.body()
                    if (transaction != null) {
                        RetrofitInstance.api.rentCar(userId, RentDetailsModel(arguments?.getString("carId")?.toInt() ?: 0, actualDateTime, endDateTime, fullCarCost, transaction.status)).enqueue(object : Callback<RentResponse> {
                            override fun onResponse(call: Call<RentResponse>, response: Response<RentResponse>) {
                                if (response.isSuccessful) {
                                    val rentResponse = response.body()
                                    if (rentResponse != null) {
                                        val summaryFragment = SummaryRentFragment()
                                        val bundle = Bundle()
                                        bundle.putString("startDateTime", actualDateTime)
                                        bundle.putString("endDateTime", endDateTime)
                                        bundle.putDouble("fullCarCost", fullCarCost)
                                        bundle.putString("carName", arguments?.getString("carName"))
                                        bundle.putDouble("carCost", carCost)
                                        bundle.putInt("minutes", minutes)
                                        bundle.putInt("payment", transaction.status)
                                        summaryFragment.arguments = bundle
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.container, summaryFragment)
                                            .commit()
                                    } else {
                                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<RentResponse>, t: Throwable) {
                                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddPaymentResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}