package io.github.auag0.hidemocklocation.app.utils

import java.lang.reflect.Method

object ReflectionUtils {
    fun <T> Class<*>.getFV(instance: Any? = null, name: String): T? {
        @Suppress("UNCHECKED_CAST")
        return this.getDeclaredField(name).get(instance) as T?
    }

    fun Class<*>.getM(name: String, vararg parameters: Class<*>): Method {
        return this.getDeclaredMethod(name, *parameters)
    }

    fun <T> Method.callM(instance: Any?, vararg args: Any): T? {
        @Suppress("UNCHECKED_CAST")
        return this.invoke(instance, *args) as T?
    }

    fun Any.getC(): Class<*> {
        return this.javaClass
    }
}