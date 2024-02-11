package io.github.auag0.hidemocklocation.app.detection

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Build
import android.provider.Settings
import io.github.auag0.hidemocklocation.app.utils.AnyUtils.toSafeString

class SettingsDetector(
    private val contentResolver: ContentResolver
) {
    @SuppressLint("ObsoleteSdkInt")
    fun getSecureDotGetStringMockLocation(): DetectResult {
        val mockLocation = Settings.Secure.getString(contentResolver, "mock_location")
        return DetectResult(
            mockLocation.toSafeString(),
            mockLocation.toBooleanStrictOrNull() == true,
            false,
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1
        )
    }
}