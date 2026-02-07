package com.foregrounsdservice.calling.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.foregrounsdservice.calling.core.CallSession
import com.foregrounsdservice.calling.service.CallService

class CallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val name = TextView(this).apply {
            text = CallSession.userName
            textSize = 22f
        }

        val endBtn = Button(this).apply {
            text = "End Call"
            setOnClickListener {
                CallService.stop(this@CallActivity)
                finish()
            }
        }

        root.addView(name)
        root.addView(endBtn)
        setContentView(root)
    }
}
