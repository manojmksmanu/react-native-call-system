package com.foregrounsdservice.calling.service

import android.app.Service
import android.content.Intent
import com.foregrounsdservice.calling.notification.CallNotification
import com.foregrounsdservice.calling.socket.SocketManager

class CoreService : Service() {

    override fun onCreate() {
        super.onCreate()

        // ✅ CREATE CHANNELS FIRST
        CallNotification.createChannels(this)

        // ✅ FOREGROUND SERVICE (NO PERMISSION CHECK HERE)
        startForeground(
            CallNotification.CORE_ID,
            CallNotification.core(this)
        )

        // ✅ DELAYED SOCKET CONNECT
        android.os.Handler(mainLooper).postDelayed({
            SocketManager.connect()
        }, 300)
    }

    override fun onDestroy() {
        SocketManager.disconnect()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
