package com.foregrounsdservice.calling.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.foregrounsdservice.calling.core.CallSession
import com.foregrounsdservice.calling.service.CallService

class CallActivity : AppCompatActivity() {

    private lateinit var timer: TextView
    private var muted = false

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        val avatar = ImageView(this).apply {
            setImageResource(android.R.drawable.sym_def_app_icon)
            layoutParams = LinearLayout.LayoutParams(220, 220)
        }

        val name = TextView(this).apply {
            text = CallSession.userName
            textSize = 22f
            gravity = Gravity.CENTER
        }

        timer = TextView(this).apply {
            text = "00:00"
            textSize = 18f
            gravity = Gravity.CENTER
        }

        val muteBtn = Button(this).apply {
            text = "Mute"
            setOnClickListener {
                muted = !muted
                text = if (muted) "Unmute" else "Mute"
            }
        }

        val endBtn = Button(this).apply {
            text = "End Call"
            setOnClickListener {
                CallService.stop(this@CallActivity)
                finish()
            }
        }

        root.addView(avatar)
        root.addView(name)
        root.addView(timer)
        root.addView(muteBtn)
        root.addView(endBtn)

        setContentView(root)
    }

    override fun onStart() {
        super.onStart()
        handler.post(timerRunnable) // 🔥 START UI TIMER
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(timerRunnable) // 🔥 STOP UI TIMER
    }

    private fun updateTimer() {
        if (CallSession.callStartTime == 0L) {
            timer.text = "00:00"
            return
        }

        val elapsed =
            (System.currentTimeMillis() - CallSession.callStartTime) / 1000
        val min = elapsed / 60
        val sec = elapsed % 60
        timer.text = String.format("%02d:%02d", min, sec)
    }
}
