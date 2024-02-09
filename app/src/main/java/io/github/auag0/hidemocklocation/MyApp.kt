package io.github.auag0.hidemocklocation

import android.app.Application

class MyApp: Application() {
    companion object {
        @JvmStatic
        fun isModuleEnabled(): Boolean {
            return false
        }
    }
}