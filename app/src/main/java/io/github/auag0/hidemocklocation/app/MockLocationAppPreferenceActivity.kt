package io.github.auag0.hidemocklocation.app

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.topjohnwu.superuser.ipc.RootService
import io.github.auag0.hidemocklocation.IMockLocationService

class MockLocationAppPreferenceActivity : Activity() {
    private var iMockService: IMockLocationService? = null
    private var isBound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iMockService = IMockLocationService.Stub.asInterface(service)
            updateUi()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            iMockService = null
            isBound = false
        }
    }

    private fun updateUi() {
        if (!isBound) {
            return
        }

    }

    override fun onStart() {
        super.onStart()
        startMockLocationService()
    }

    override fun onStop() {
        super.onStop()
        stopMockLocationService()
    }

    private fun startMockLocationService() {
        val intent = Intent(this, MockLocationService::class.java)
        RootService.bind(intent, connection)
    }

    private fun stopMockLocationService() {
        if (isBound) {
            RootService.unbind(connection)
            isBound = false
        }
    }
}