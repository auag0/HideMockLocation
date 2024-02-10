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

        val btnDetectionTest: Button = findViewById(R.id.btn_detection_test)
        btnDetectionTest.setOnClickListener {
            detectionTest()
        }
    }

    private fun detectionTest() {
        val locationManager = getSystemService(LocationManager::class.java)
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, R.string.request_enable_location, Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 178)
            }
            Toast.makeText(this, R.string.request_location_permission, Toast.LENGTH_LONG).show()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.getCurrentLocation(
                LocationManager.GPS_PROVIDER,
                null,
                mainExecutor
            ) { showDetectionResult(it) }
        } else {
            @Suppress("DEPRECATION")
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                { showDetectionResult(it) },
                null
            )
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

        AlertDialog.Builder(this)
            .setTitle(R.string.detection_result)
            .setView(dialogView)
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
        detectionTest()
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