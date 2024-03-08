package io.github.auag0.hidemocklocation

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

object XposedUtils {
    fun XC_MethodHook.MethodHookParam.invokeOriginalMethod(): Any? {
        return XposedBridge.invokeOriginalMethod(
            this.method,
            this.thisObject,
            this.args
        )
    }
}