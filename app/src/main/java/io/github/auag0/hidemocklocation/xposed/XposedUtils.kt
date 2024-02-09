package io.github.auag0.hidemocklocation.xposed

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge

object XposedUtils {
    fun XC_MethodHook.MethodHookParam.invokeOriginalMethod(): Any? {
        return XposedBridge.invokeOriginalMethod(
            this.method,
            this.thisObject,
            this.args
        )
    }

    fun replaceMethod(
        clazz: Class<*>,
        methodName: String,
        returnValue: Any? = null,
        replacement: (param: XC_MethodHook.MethodHookParam) -> Any? = { null }
    ) {
        XposedBridge.hookAllMethods(clazz, methodName, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                return replacement(param) ?: returnValue
            }
        })
    }
}