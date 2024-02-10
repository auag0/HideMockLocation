package io.github.auag0.hidemocklocation

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import io.github.auag0.hidemocklocation.MyApp.Companion.isModuleEnabled

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateStatus()

        findViewById<Button>(R.id.btn_detection_test).setOnClickListener {
            runDetectionTest()
        }
    }

    private fun runDetectionTest() {
        val locationManager = getSystemService(LocationManager::class.java)
        if (!isGpsEnabled(locationManager)) {
            Toast.makeText(this, R.string.request_enable_location, Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }
        if (!hasLocationPermission()) {
            requestLocationPermission()
            Toast.makeText(this, R.string.request_location_permission, Toast.LENGTH_LONG).show()
            return
        }
        getCurrentLocation(locationManager) { location ->
            showDetectionResult(location)
        }
    }

    private fun isGpsEnabled(locationManager: LocationManager): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun hasLocationPermission(): Boolean {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 178)
        }
    }

    private fun getCurrentLocation(
        locationManager: LocationManager,
        callback: (location: Location?) -> Unit
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    mainExecutor
                ) { callback(it) }
            } else {
                @Suppress("DEPRECATION")
                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    { callback(it) },
                    null
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            callback(null)
        }
    }

    private fun showDetectionResult(location: Location?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detection_result, null)
        val tableLayout: TableLayout = dialogView.findViewById(R.id.tableLayout)

        val results = mutableListOf<Pair<String, String>>().apply {
            add("Location" to location.toString())
            add("android.location.Location" to "")
            add("isFromMockProvider()" to location?.isFromMockProvider.toString())
            add("isMock()" to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) location?.isMock.toString() else "Not supported")
            add("getExtras().getBoolean(\"mockLocation\")" to location?.extras?.getBoolean("mockLocation").toString())
            add("android.provider.Settings" to "")
            add("Secure.getString(\"mock_location\")" to Settings.Secure.getString(contentResolver, "mock_location"))
        }

        results.forEach { (title, content) ->
            val layoutId =
                if (content.isEmpty()) R.layout.detection_title_row else R.layout.detection_result_row
            val tableRow = layoutInflater.inflate(layoutId, tableLayout, false) as TableRow
            tableRow.findViewById<TextView>(R.id.title).text = title
            if (content.isNotEmpty()) {
                tableRow.findViewById<TextView>(R.id.content).text = content
            }
            tableLayout.addView(tableRow)
        }

        val iconRes = if (results.any { it.second.trim() == "1" || it.second.trim() == "true" }) {
            R.drawable.ic_batsu
        } else R.drawable.ic_check

        AlertDialog.Builder(this)
            .setTitle(R.string.detection_result)
            .setIcon(iconRes)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != 178) {
            return
        }
        if (grantResults.firstOrNull() != PackageManager.PERMISSION_GRANTED) {
            return
        }
        runDetectionTest()
    }

    private fun updateStatus() {
        val tvStatusTitle: TextView = findViewById(R.id.tv_status_title)
        val ivStatusIcon: ImageView = findViewById(R.id.iv_status_icon)
        if (isModuleEnabled()) {
            tvStatusTitle.setText(R.string.status_activated)
            ivStatusIcon.setImageResource(R.drawable.ic_check)
        } else {
            tvStatusTitle.setText(R.string.status_not_activated)
            ivStatusIcon.setImageResource(R.drawable.ic_batsu)
        }
    }
}