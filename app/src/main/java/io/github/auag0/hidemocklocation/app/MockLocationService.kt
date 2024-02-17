package io.github.auag0.hidemocklocation.app

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.AppOpsManagerHidden
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import com.topjohnwu.superuser.ipc.RootService
import dev.rikka.tools.refine.Refine
import io.github.auag0.hidemocklocation.IMockLocationService


class MockLocationService : RootService() {
    companion object {
        private val MOCK_LOCATION_APP_OPS = intArrayOf(AppOpsManagerHidden.OP_MOCK_LOCATION)
        private const val ACCESS_MOCK_LOCATION = "android.permission.ACCESS_MOCK_LOCATION"
        private val DISABLED_COMPONENTS_FLAGS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                PackageManager.MATCH_DISABLED_COMPONENTS
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_DISABLED_COMPONENTS
            }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private val binder = object : IMockLocationService.Stub() {
        override fun writeMockLocation(mockLocationAppName: String) {
            this@MockLocationService.writeMockLocation(mockLocationAppName)
        }

        override fun getCurrentMockLocationApp(): String? {
            return this@MockLocationService.getCurrentMockLocationApp()
        }

        override fun removeAllMockLocations() {
            this@MockLocationService.removeAllMockLocations()
        }

        override fun removeMockLocationForApp(appName: String) {
            this@MockLocationService.removeMockLocationForApp(appName)
        }

        override fun getMockLocationApps(): List<PackageInfo> {
            return this@MockLocationService.getMockLocationApps()
        }
    }

    private fun writeMockLocation(mockLocationAppName: String) {
        removeAllMockLocations()
        if (mockLocationAppName.isNotBlank()) {
            try {
                val flags = DISABLED_COMPONENTS_FLAGS
                val appInfo = packageManager.getApplicationInfo(mockLocationAppName, flags)
                val aom = getSystemService(AppOpsManager::class.java)
                val appOpsManager = Refine.unsafeCast<AppOpsManagerHidden>(aom)
                appOpsManager.setMode(
                    AppOpsManagerHidden.OP_MOCK_LOCATION,
                    appInfo.uid,
                    mockLocationAppName,
                    AppOpsManager.MODE_ALLOWED
                )
            } catch (ignore: PackageManager.NameNotFoundException) {
            }
        }
    }

    private fun getCurrentMockLocationApp(): String? {
        val aom = getSystemService(AppOpsManager::class.java)
        val appOpsManager = Refine.unsafeCast<AppOpsManagerHidden>(aom)
        val packageOps = appOpsManager.getPackagesForOps(MOCK_LOCATION_APP_OPS)
        return packageOps.firstOrNull { packageOp ->
            packageOp.ops.getOrNull(0)?.mode == AppOpsManager.MODE_ALLOWED
        }?.packageName
    }

    private fun removeAllMockLocations() {
        val aom = getSystemService(AppOpsManager::class.java)
        val appOpsManager = Refine.unsafeCast<AppOpsManagerHidden>(aom)
        val packageOps = appOpsManager.getPackagesForOps(MOCK_LOCATION_APP_OPS)
        packageOps?.forEach { packageOp ->
            if (packageOp.ops.getOrNull(0)?.mode != AppOpsManager.MODE_ERRORED) {
                removeMockLocationForApp(packageOp.packageName)
            }
        }
    }

    private fun removeMockLocationForApp(appName: String) {
        try {
            val flags = DISABLED_COMPONENTS_FLAGS
            val ai = packageManager.getApplicationInfo(appName, flags)
            val aom = getSystemService(AppOpsManager::class.java)
            val appOpsManager = Refine.unsafeCast<AppOpsManagerHidden>(aom)
            appOpsManager.setMode(
                AppOpsManagerHidden.OP_MOCK_LOCATION,
                ai.uid,
                appName,
                AppOpsManager.MODE_ERRORED
            )
        } catch (ignore: PackageManager.NameNotFoundException) {
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getMockLocationApps(): List<PackageInfo> {
        val apps = mutableListOf<PackageInfo>()
        val packages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        packages.forEach { packageInfo ->
            packageInfo.requestedPermissions?.forEach permLoop@{ requestedPermission ->
                if (requestedPermission == ACCESS_MOCK_LOCATION) {
                    apps.add(packageInfo)
                    return@permLoop
                }
            }
        }
        return apps
    }
}