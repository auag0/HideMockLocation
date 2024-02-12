package io.github.auag0.hidemocklocation.app.detection

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
        val isFromMockProvider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            @Suppress("DEPRECATION")
            location?.isFromMockProvider
        } else null
        return DetectResult(
            isFromMockProvider.toSafeString(),
            isFromMockProvider == true,
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1,
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S
        )
    }

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