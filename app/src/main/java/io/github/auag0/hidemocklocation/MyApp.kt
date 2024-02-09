package io.github.auag0.hidemocklocation

import android.app.Application
import io.github.auag0.hidemocklocation.xposed.Logger

class MyApp: Application() {
    companion object {
        @JvmStatic
        fun isModuleEnabled(): Boolean {
            Logger.logD("isModuleNotActivitaled")
            return false
        }
    }
}