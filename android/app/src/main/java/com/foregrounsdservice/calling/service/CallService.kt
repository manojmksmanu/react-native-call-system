package com.foregrounsdservice.calling.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.foregrounsdservice.calling.core.CallSession
import com.foregrounsdservice.calling.notification.CallNotification

class CallService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            updateNotification()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (CallSession.callStartTime == 0L) {
            CallSession.callStartTime = System.currentTimeMillis()
        }

        startForeground(
            CallNotification.CALL_ID,
            buildNotification()
        )

        handler.post(timerRunnable)
    }

    private fun buildNotification(): android.app.Notification {
        val elapsedSec =
            (System.currentTimeMillis() - CallSession.callStartTime) / 1000
        val min = elapsedSec / 60
        val sec = elapsedSec % 60
        val timeText = String.format("%02d:%02d", min, sec)

        return CallNotification.call(
            this,
            CallSession.userName,
            timeText
        )
    }

    private fun updateNotification() {
        val nm =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(CallNotification.CALL_ID, buildNotification())
    }

    override fun onDestroy() {
        handler.removeCallbacks(timerRunnable)
        CallSession.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    companion object {
        fun start(context: Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, CallService::class.java)
            )
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, CallService::class.java))
        }
    }
}
