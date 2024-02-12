package io.github.auag0.hidemocklocation.common

import android.util.Log

object Logger {
    private const val TAG = "HideMockLocation"

    fun logD(msg: String?) {
        Log.d(TAG, msg.toString())
    }

    fun logI(msg: String?) {
        Log.i(TAG, msg.toString())
    }

    fun logE(msg: String?) {
        Log.e(TAG, msg.toString())
    }
}