package io.github.auag0.hidemocklocation

import android.os.Bundle
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import de.robv.android.xposed.XposedBridge.hookAllMethods
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getStaticIntField
import de.robv.android.xposed.XposedHelpers.setIntField
import de.robv.android.xposed.XposedHelpers.setObjectField
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.auag0.hidemocklocation.XposedUtils.invokeOriginalMethod

class Main : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookLocationMethods(lpparam.classLoader)
        hookSettingsMethods(lpparam.classLoader)
    }

    private fun hookLocationMethods(classLoader: ClassLoader) {
        val locationClass = findClass(
            "android.location.Location",
            classLoader
        )
        // Hooked android.location.Location isFromMockProvider()
        hookAllMethods(locationClass, "isFromMockProvider", returnConstant(false))
        // Hooked android.location.Location isMock()
        hookAllMethods(locationClass, "isMock", returnConstant(false))
        // Hooked android.location.Location setIsFromMockProvider()
        hookAllMethods(locationClass, "setIsFromMockProvider", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val isFromMockProvider = param.args[0] as Boolean?
                if (isFromMockProvider == true) {
                    param.args[0] = false
                }
            }
        })
        // Hooked android.location.Location setMock()
        hookAllMethods(locationClass, "setMock", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val mock = param.args[0] as Boolean?
                if (mock == true) {
                    param.args[0] = false
                }
            }
        })
        // Hooked android.location.Location getExtras()
        hookAllMethods(locationClass, "getExtras", object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Bundle? {
                var extras: Bundle? = param.invokeOriginalMethod() as Bundle?
                extras = getPatchedBundle(extras)
                return extras
            }
        })
        // Hooked android.location.Location setExtras()
        hookAllMethods(locationClass, "setExtras", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val extras = param.args[0] as Bundle?
                param.args[0] = getPatchedBundle(extras)
            }
        })
        // Hooked android.location.Location set()
        hookAllMethods(locationClass, "set", object : XC_MethodHook() {
            val HAS_MOCK_PROVIDER_MASK = getStaticIntField(locationClass, "HAS_MOCK_PROVIDER_MASK")
            override fun afterHookedMethod(param: MethodHookParam) {
                var mFieldsMask = getIntField(param.thisObject, "mFieldsMask")
                mFieldsMask = mFieldsMask and HAS_MOCK_PROVIDER_MASK.inv()
                setIntField(param.thisObject, "mFieldsMask", mFieldsMask)

                var mExtras = getObjectField(param.thisObject, "mExtras") as Bundle?
                mExtras = getPatchedBundle(mExtras)
                setObjectField(param.thisObject, "mockLocation", mExtras)
            }
        })
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
            val clazz = findClass(it, classLoader)
            hookAllMethods(clazz, "getStringForUser", object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    val name: String? = param.args[1] as? String?
                    return when (name) {
                        "mock_location" -> "0"
                        else -> param.invokeOriginalMethod()
                    }
                }
            })
        }
    }

    /**
     * if "mockLocation" containsKey in the given bundle, set it to false
     *
     * @param origBundle original Bundle object
     * @return Bundle with "mockLocation" set to false
     */
    private fun getPatchedBundle(origBundle: Bundle?): Bundle? {
        if (origBundle?.containsKey("mockLocation") == true) {
            origBundle.putBoolean("mockLocation", false)
        }
        return origBundle
    }
}