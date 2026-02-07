package com.foregrounsdservice.calling.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.foregrounsdservice.calling.core.CallSession
import com.foregrounsdservice.calling.service.CallService

class CallActivity : AppCompatActivity() {

    private lateinit var timer: TextView
    private lateinit var statusText: TextView
    private lateinit var muteBtn: FrameLayout
    private lateinit var muteBtnIcon: ImageView
    private lateinit var muteBtnText: TextView
    private lateinit var endBtn: FrameLayout
    private lateinit var avatarCard: FrameLayout
    private lateinit var pulseView: View
    
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
        
        // Set status bar color
        window.statusBarColor = Color.parseColor("#F7F8FA")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#F7F8FA"))
            setPadding(40, 80, 40, 80)
        }

        // Avatar Section with pulse animation
        val avatarContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        // Pulse effect background
        pulseView = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(280, 280)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#E3F2FD"))
            }
        }

        avatarCard = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(240, 240).apply {
                setMargins(0, -260, 0, 0)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.WHITE)
            }
            elevation = 12f
        }

        val avatar = ImageView(this).apply {
            setImageResource(android.R.drawable.sym_def_app_icon)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(20, 20, 20, 20)
        }

        avatarCard.addView(avatar)

        val name = TextView(this).apply {
            text = CallSession.userName
            textSize = 28f
            setTextColor(Color.parseColor("#111B21"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            }
        }

        statusText = TextView(this).apply {
            text = "Calling..."
            textSize = 16f
            setTextColor(Color.parseColor("#667781"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
        }

        timer = TextView(this).apply {
            text = "00:00"
            textSize = 18f
            setTextColor(Color.parseColor("#00A884"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            alpha = 0f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 12
            }
        }

        avatarContainer.addView(pulseView)
        avatarContainer.addView(avatarCard)
        avatarContainer.addView(name)
        avatarContainer.addView(statusText)
        avatarContainer.addView(timer)

        // Control Buttons Section
        val controlsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 40, 0, 0)
        }

        // Mute Button
        val muteContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 48, 0)
            }
        }

        muteBtn = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(120, 120)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.WHITE)
            }
            elevation = 8f
            setOnClickListener {
                muted = !muted
                updateMuteButton()
            }
        }

        muteBtnIcon = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_btn_speak_now)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(30, 30, 30, 30)
            setColorFilter(Color.parseColor("#667781"))
        }

        muteBtn.addView(muteBtnIcon)

        muteBtnText = TextView(this).apply {
            text = "Mute"
            textSize = 14f
            setTextColor(Color.parseColor("#667781"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 12
            }
        }

        muteContainer.addView(muteBtn)
        muteContainer.addView(muteBtnText)

        // End Call Button
        val endContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        endBtn = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(120, 120)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#FF3B30"))
            }
            elevation = 8f
            setOnClickListener {
                animateEndCall()
            }
        }

        val endIcon = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_call)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(30, 30, 30, 30)
            rotation = 135f
            setColorFilter(Color.WHITE)
        }

        endBtn.addView(endIcon)

        val endText = TextView(this).apply {
            text = "End Call"
            textSize = 14f
            setTextColor(Color.parseColor("#FF3B30"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 12
            }
        }

        endContainer.addView(endBtn)
        endContainer.addView(endText)

        controlsContainer.addView(muteContainer)
        controlsContainer.addView(endContainer)

        root.addView(avatarContainer)
        root.addView(controlsContainer)

        setContentView(root)

        // Start animations
        startPulseAnimation()
        animateEntrance()
    }

    override fun onStart() {
        super.onStart()
        handler.post(timerRunnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(timerRunnable)
    }

    private fun updateTimer() {
        if (CallSession.callStartTime == 0L) {
            timer.text = "00:00"
            statusText.text = "Calling..."
            timer.alpha = 0f
            return
        }

        // Show timer and hide calling status
        if (timer.alpha == 0f) {
            timer.animate().alpha(1f).setDuration(300).start()
            statusText.animate().alpha(0f).setDuration(300).start()
        }

        val elapsed = (System.currentTimeMillis() - CallSession.callStartTime) / 1000
        val min = elapsed / 60
        val sec = elapsed % 60
        timer.text = String.format("%02d:%02d", min, sec)
    }

    private fun updateMuteButton() {
        muteBtn.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(75)
            .withEndAction {
                muteBtn.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(75)
                    .start()
            }
            .start()

        if (muted) {
            (muteBtn.background as GradientDrawable).setColor(Color.parseColor("#E3F2FD"))
            muteBtnIcon.setColorFilter(Color.parseColor("#00A884"))
            muteBtnText.text = "Unmute"
            muteBtnText.setTextColor(Color.parseColor("#00A884"))
        } else {
            (muteBtn.background as GradientDrawable).setColor(Color.WHITE)
            muteBtnIcon.setColorFilter(Color.parseColor("#667781"))
            muteBtnText.text = "Mute"
            muteBtnText.setTextColor(Color.parseColor("#667781"))
        }
    }

    private fun animateEndCall() {
        endBtn.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                endBtn.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction {
                        CallService.stop(this@CallActivity)
                        finish()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                    .start()
            }
            .start()
    }

    private fun startPulseAnimation() {
        val pulseAnimator = ValueAnimator.ofFloat(1f, 1.15f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                pulseView.scaleX = scale
                pulseView.scaleY = scale
                pulseView.alpha = 1f - (scale - 1f) * 2
            }
        }
        pulseAnimator.start()
    }

    private fun animateEntrance() {
        avatarCard.scaleX = 0f
        avatarCard.scaleY = 0f
        avatarCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        muteBtn.translationY = 100f
        muteBtn.alpha = 0f
        muteBtn.animate()
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(200)
            .setDuration(400)
            .start()

        endBtn.translationY = 100f
        endBtn.alpha = 0f
        endBtn.animate()
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(250)
            .setDuration(400)
            .start()
    }
}