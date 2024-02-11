package io.github.auag0.hidemocklocation.app.detection

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import io.github.auag0.hidemocklocation.app.utils.AnyUtils.toSafeString

class LocationDetector(
    private val location: Location?
) {
    fun isMock(): DetectResult {
        val isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            location?.isMock
        } else null
        return DetectResult(
            isMock.toSafeString(),
            isMock == true,
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.R,
            false
        )
    }

    fun isFromMockProvider(): DetectResult {
        val isFromMockProvider = location?.isFromMockProvider
        return DetectResult(
            isFromMockProvider.toSafeString(),
            isFromMockProvider == true,
            false,
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S
        )
    }

    @SuppressLint("ObsoleteSdkInt")
    fun getExtrasDotGetBooleanMockLocation(): DetectResult {
        val mockLocation = location?.extras?.getBoolean("mockLocation")
        return DetectResult(
            mockLocation.toSafeString(),
            mockLocation == true,
            false,
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1
        )
    }
}