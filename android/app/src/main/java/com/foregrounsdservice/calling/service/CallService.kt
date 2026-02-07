package com.foregrounsdservice.calling.service

import android.app.Service
import android.content.Intent
import androidx.core.content.ContextCompat
import com.foregrounsdservice.calling.core.CallSession
import com.foregrounsdservice.calling.notification.CallNotification

class CallService : Service() {

    companion object {

        // ✅ Context based start (Activity / Application / RN bridge)
        fun start(context: android.content.Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, CallService::class.java)
            )
        }

        fun stop(context: android.content.Context) {
            context.stopService(
                Intent(context, CallService::class.java)
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        
    CallNotification.createChannels(this)

        startForeground(
            CallNotification.CALL_ID,
            CallNotification.call(this, CallSession.userName)
        )
    }

    override fun onDestroy() {
        CallSession.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
