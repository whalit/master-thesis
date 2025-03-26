package com.example.master

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class Home : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var controller: IMapController
    private lateinit var btnVelo: MaterialButton
    private lateinit var btnWalk: MaterialButton
    private lateinit var btnConnect: MaterialButton
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }
    private val bluetoothLeScanner by lazy { bluetoothAdapter?.bluetoothLeScanner }
    private val handler = Handler()
    private var scanning = false
    private val SCAN_PERIOD: Long = 10000 // 10 seconds
    private val deviceList = mutableListOf<BluetoothDevice>() // List to store found devices
    private val deviceNames = mutableListOf<String>() // List to store device names
    private var scanningDialog: AlertDialog? = null // Dialog to show "Scanning Devices"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set up osmdroid configuration
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = "com.example.master"

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize MapView
        mapView = view.findViewById(R.id.map_view)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Initialize Map Controller
        controller = mapView.controller
        controller.setZoom(15.0)

        // Set default location
        val defaultLocation = GeoPoint(50.8503, 4.3517) // Brussels
        controller.setCenter(defaultLocation)
        addMarker(defaultLocation, "Default Location", "Brussels, Belgium")

        // Initialize Buttons
        btnVelo = view.findViewById(R.id.btnVelo)
        btnWalk = view.findViewById(R.id.btnWalk)
        btnConnect = view.findViewById(R.id.btnConnect)

        var isVeloActive = false
        var isWalkActive = false

        // Helper method to reset to the default map layer
         fun resetToDefaultLayer() {
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            Toast.makeText(requireContext(), "Default layer applied", Toast.LENGTH_SHORT).show()
        }

        // Helper method to reset button colors
        fun resetButtonColors() {
            btnVelo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
            btnWalk.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        btnVelo.setOnClickListener {
            if (isVeloActive) {
                resetToDefaultLayer()
                resetButtonColors()
                isVeloActive = false
            } else {
                setCyclingLayer()
                updateButtonColors(btnVelo, btnWalk)
                isVeloActive = true
                isWalkActive = false
            }
        }

        btnWalk.setOnClickListener {
            if (isWalkActive) {
                resetToDefaultLayer()
                resetButtonColors()
                isWalkActive = false
            } else {
                setWalkingLayer()
                updateButtonColors(btnWalk, btnVelo)
                isWalkActive = true
                isVeloActive = false
            }
        }



        // Button listener for Bluetooth scanning
        btnConnect.setOnClickListener {
            Log.d("HomeFragment", "Start button clicked")
            scanLeDevice()
        }

        // Request location permissions
        requestLocationPermission()

        return view
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showUserLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun scanLeDevice() {
        if (!scanning) {
            deviceList.clear()
            deviceNames.clear()
            showScanningDialog() // Show "Scanning Devices" dialog
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner?.stopScan(leScanCallback)
                dismissScanningDialog() // Dismiss dialog
                showDeviceListDialog() // Show popup with the list of devices
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
            dismissScanningDialog() // Dismiss dialog if scan is stopped manually
            Toast.makeText(requireContext(), "Scan stopped", Toast.LENGTH_SHORT).show()
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val deviceName = device.name ?: "Unknown Device"
            val deviceInfo = "$deviceName\n${device.address}"
            if (!deviceNames.contains(deviceInfo)) {
                deviceList.add(device)
                deviceNames.add(deviceInfo)
                Log.d("HomeFragment", "Found device: $deviceInfo")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("HomeFragment", "Scan failed: $errorCode")
            dismissScanningDialog() // Dismiss dialog on scan failure
            Toast.makeText(requireContext(), "Scan failed: $errorCode", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showScanningDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(LayoutInflater.from(context).inflate(R.layout.dialog_scanning, null))
        builder.setCancelable(false)
        scanningDialog = builder.create()
        scanningDialog?.show()
    }

    private fun dismissScanningDialog() {
        scanningDialog?.dismiss()
        scanningDialog = null
    }

    private fun showDeviceListDialog() {
        if (deviceList.isEmpty()) {
            Toast.makeText(requireContext(), "No devices found", Toast.LENGTH_SHORT).show()
            return
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select a Device")
        builder.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, deviceNames)
        ) { _, which ->
            val selectedDevice = deviceList[which]
            connectToDevice(selectedDevice)
        }
        builder.setPositiveButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun connectToDevice(device: BluetoothDevice) {
        device.connectGatt(requireContext(), false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("HomeFragment", "Connected to ${device.name ?: "Unknown"}")
                    Toast.makeText(requireContext(), "Connected to ${device.name}", Toast.LENGTH_SHORT).show()
                    gatt?.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("HomeFragment", "Disconnected from ${device.name ?: "Unknown"}")
                    Toast.makeText(requireContext(), "Disconnected from ${device.name}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showUserLocation() {
        val userLocation = GeoPoint(50.8503, 4.3517) // Brussels, Belgium
        controller.setCenter(userLocation)
        controller.setZoom(18.0)
        addMarker(userLocation, "You are here", "Your current location")
    }

    private fun setCyclingLayer() {
        mapView.setTileSource(
            XYTileSource(
                "CyclOSM",
                0, 19, 256, ".png",
                arrayOf("https://a.tile-cyclosm.openstreetmap.fr/cyclosm/"),
                "CyclOSM"
            )
        )
        Toast.makeText(requireContext(), "Cycling layer applied", Toast.LENGTH_SHORT).show()
    }

    private fun setWalkingLayer() {
        mapView.setTileSource(
            XYTileSource(
                "Humanitarian",
                0, 19, 256, ".png",
                arrayOf("https://a.tile.openstreetmap.fr/hot/"),
                "OpenStreetMap Humanitarian"
            )
        )
        Toast.makeText(requireContext(), "Walking layer applied", Toast.LENGTH_SHORT).show()
    }

    private fun addMarker(location: GeoPoint, title: String, description: String) {
        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        marker.subDescription = description
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    private fun updateButtonColors(activeButton: MaterialButton, inactiveButton: MaterialButton) {
        activeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        inactiveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showUserLocation()
                } else {
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanLeDevice()
                } else {
                    Toast.makeText(requireContext(), "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 2
    }
}
