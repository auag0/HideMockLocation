package io.github.auag0.hidemocklocation

import android.os.Bundle
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.auag0.hidemocklocation.XposedUtils.invokeOriginalMethod
import io.github.auag0.hidemocklocation.XposedUtils.replaceMethod

class Main : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookLocationMethods(lpparam.classLoader)
        hookSettingsMethods(lpparam.classLoader)
    }

    private fun hookLocationMethods(classLoader: ClassLoader) {
        val locationClass = XposedHelpers.findClass(
            "android.location.Location",
            classLoader
        )
        // Hooked android.location.Location isFromMockProvider()
        replaceMethod(locationClass, "isFromMockProvider", false)
        // Hooked android.location.Location isMock()
        replaceMethod(locationClass, "isMock", false)
        // Hooked android.location.Location getExtras()
        replaceMethod(locationClass, "getExtras") { param ->
            val extras: Bundle? = param.invokeOriginalMethod() as? Bundle
            if (extras?.getBoolean("mockLocation") == true) {
                extras.putBoolean("mockLocation", false)
            }
            return@replaceMethod extras
        }
    }

    private fun hookSettingsMethods(classLoader: ClassLoader) {
        // Hooked android.provider.Settings.* getStringForUser()
        val settingsClassNames = arrayOf(
            "android.provider.Settings.Secure",
            "android.provider.Settings.System",
            "android.provider.Settings.Global",
            "android.provider.Settings.NameValueCache"
        )
        settingsClassNames.forEach {
            val clazz = XposedHelpers.findClass(it, classLoader)
            replaceMethod(clazz, "getStringForUser") { param ->
                val name: String? = param.args[1] as? String
                if (name == "mock_location") {
                    return@replaceMethod "0"
                }
                return@replaceMethod param.invokeOriginalMethod()
            }
        }
    }
}