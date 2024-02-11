package io.github.auag0.hidemocklocation.app.utils

object AnyUtils {
    fun Any?.toSafeString(): String {
        return this.toString()
    }
}