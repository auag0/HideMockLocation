package io.github.auag0.hidemocklocation.app

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
import io.github.auag0.hidemocklocation.R
import io.github.auag0.hidemocklocation.app.MyApp.Companion.isModuleEnabled
import io.github.auag0.hidemocklocation.app.detection.DetectResult
import io.github.auag0.hidemocklocation.app.detection.LocationDetector
import io.github.auag0.hidemocklocation.app.detection.SettingsDetector
import io.github.auag0.hidemocklocation.app.utils.AnyUtils.toSafeString

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
            showDetectionResultDialog(location)
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

    private fun showDetectionResultDialog(location: Location?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detection_result, null)
        val tableLayout: TableLayout = dialogView.findViewById(R.id.tableLayout)

        val locationDetector = LocationDetector(location)
        val settingsDetector = SettingsDetector(contentResolver)
        val results = mutableListOf<Pair<String, DetectResult?>>().apply {
            add("Location" to DetectResult(location.toSafeString()))
            add("android.location.Location" to null)
            add("isFromMockProvider()" to locationDetector.isFromMockProvider())
            add("isMock()" to locationDetector.isMock())
            add("getExtras().getBoolean(\"mockLocation\")" to locationDetector.getExtrasDotGetBooleanMockLocation())
            add("android.provider.Settings" to null)
            add("Secure.getString(\"mock_location\")" to settingsDetector.getSecureDotGetStringMockLocation())
        }

        results.forEach { (title, result) ->
            val layoutResId = when (result) {
                null -> R.layout.detection_title_row
                else -> R.layout.detection_result_row
            }
            val tableRow = layoutInflater.inflate(layoutResId, tableLayout, false) as TableRow
            tableRow.findViewById<TextView>(R.id.title).text = title

            result?.let {
                val content = tableRow.findViewById<TextView>(R.id.content)
                content.text = result.value
                val textColorResId = when (result.isDetected) {
                    true -> android.R.color.holo_red_light
                    false -> android.R.color.holo_green_light
                    null -> return@let
                }
                content.setTextColor(resources.getColor(textColorResId, null))
            }

            tableLayout.addView(tableRow)
        }

        val iconResId = when (results.any { it.second?.isDetected == true }) {
            true -> R.drawable.ic_batsu
            false -> R.drawable.ic_check
        }

        AlertDialog.Builder(this)
            .setIcon(iconResId)
            .setTitle(R.string.detection_result)
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