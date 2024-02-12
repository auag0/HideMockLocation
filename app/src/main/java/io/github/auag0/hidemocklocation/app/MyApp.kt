package io.github.auag0.hidemocklocation.app

import android.app.Application

@Suppress("SameReturnValue")
class MyApp: Application() {
    companion object {
        @JvmStatic
        fun isModuleEnabled(): Boolean {
            return false
        }
    }
}