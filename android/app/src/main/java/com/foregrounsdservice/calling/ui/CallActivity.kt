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
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.foregrounsdservice.calling.core.CallSession
import com.foregrounsdservice.calling.service.CallService
import kotlin.random.Random

class CallActivity : AppCompatActivity() {

    private lateinit var timer: TextView
    private lateinit var statusText: TextView
    private lateinit var nameText: TextView
    private lateinit var muteBtn: FrameLayout
    private lateinit var muteBtnIcon: ImageView
    private lateinit var muteBtnText: TextView
    private lateinit var speakerBtn: FrameLayout
    private lateinit var speakerBtnIcon: ImageView
    private lateinit var speakerBtnText: TextView
    private lateinit var endBtn: FrameLayout
    private lateinit var endIcon: ImageView
    private lateinit var avatarCard: FrameLayout
    private lateinit var pulseView: View
    private lateinit var soundIndicator: LinearLayout
    private val soundBars = mutableListOf<View>()
    
    private var muted = false
    private var speakerOn = false
    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, 1000)
        }
    }

    private val soundAnimationRunnable = object : Runnable {
        override fun run() {
            if (!muted && CallSession.callStartTime != 0L) {
                animateSoundBars()
            }
            handler.postDelayed(this, 150)
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
            setPadding(40, 100, 40, 80)
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
            layoutParams = LinearLayout.LayoutParams(300, 300)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#E3F2FD"))
            }
        }

        avatarCard = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(260, 260).apply {
                setMargins(0, -280, 0, 0)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.WHITE)
            }
            elevation = 16f
        }

        val avatar = ImageView(this).apply {
            setImageResource(android.R.drawable.sym_def_app_icon)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(30, 30, 30, 30)
        }

        avatarCard.addView(avatar)

        nameText = TextView(this).apply {
            text = CallSession.userName
            textSize = 32f
            setTextColor(Color.parseColor("#111B21"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 40
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
            textSize = 20f
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

        // Sound Indicator
        soundIndicator = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            alpha = 0f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 24
            }
        }

        // Create 5 sound bars
        for (i in 0..4) {
            val bar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(8, 40).apply {
                    setMargins(4, 0, 4, 0)
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 8f
                    setColor(Color.parseColor("#00A884"))
                }
                scaleY = 0.3f
            }
            soundBars.add(bar)
            soundIndicator.addView(bar)
        }

        avatarContainer.addView(pulseView)
        avatarContainer.addView(avatarCard)
        avatarContainer.addView(nameText)
        avatarContainer.addView(statusText)
        avatarContainer.addView(timer)
        avatarContainer.addView(soundIndicator)

        // Control Buttons Section - All in one row
        val controlsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(20, 40, 20, 20)
        }

        // Mute Button (Left)
        val muteContainer = createButtonContainer().apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        muteBtn = createCircularButton(Color.WHITE, 8f, 110)
        muteBtn.setOnClickListener {
            muted = !muted
            updateMuteButton()
        }

        muteBtnIcon = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_btn_speak_now)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(28, 28, 28, 28)
            setColorFilter(Color.parseColor("#667781"))
        }

        muteBtn.addView(muteBtnIcon)

        muteBtnText = createButtonLabel("Mute", Color.parseColor("#667781"))

        muteContainer.addView(muteBtn)
        muteContainer.addView(muteBtnText)

        // End Call Button (Center) - Rotated 90 degrees
        val endContainer = createButtonContainer().apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.2f
            )
        }

        endBtn = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(150, 150)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#FF3B30"))
            }
            elevation = 16f
            setOnClickListener {
                animateEndCall()
            }
        }

        endIcon = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_call)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(38, 38, 38, 38)
            rotation = 180f  // 90 degree rotation - phone horizontal
            setColorFilter(Color.WHITE)
        }

        endBtn.addView(endIcon)

        val endText = TextView(this).apply {
            text = "End Call"
            textSize = 14f
            setTextColor(Color.parseColor("#FF3B30"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 14
            }
        }

        endContainer.addView(endBtn)
        endContainer.addView(endText)

        // Speaker Button (Right)
        val speakerContainer = createButtonContainer().apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        speakerBtn = createCircularButton(Color.WHITE, 8f, 110)
        speakerBtn.setOnClickListener {
            speakerOn = !speakerOn
            updateSpeakerButton()
        }

        speakerBtnIcon = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_lock_silent_mode_off)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(28, 28, 28, 28)
            setColorFilter(Color.parseColor("#667781"))
        }

        speakerBtn.addView(speakerBtnIcon)

        speakerBtnText = createButtonLabel("Speaker", Color.parseColor("#667781"))

        speakerContainer.addView(speakerBtn)
        speakerContainer.addView(speakerBtnText)

        controlsContainer.addView(muteContainer)
        controlsContainer.addView(endContainer)
        controlsContainer.addView(speakerContainer)

        root.addView(avatarContainer)
        root.addView(controlsContainer)

        setContentView(root)

        // Start animations
        startPulseAnimation()
        animateEntrance()
        handler.post(soundAnimationRunnable)
    }

    private fun createButtonContainer(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun createCircularButton(color: Int, elevation: Float, size: Int): FrameLayout {
        return FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(size, size)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
            }
            this.elevation = elevation
        }
    }

    private fun createButtonLabel(text: String, color: Int): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13f
            setTextColor(color)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 12
            }
        }
    }

    override fun onStart() {
        super.onStart()
        handler.post(timerRunnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(timerRunnable)
        handler.removeCallbacks(soundAnimationRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
        handler.removeCallbacks(soundAnimationRunnable)
    }

    private fun updateTimer() {
        if (CallSession.callStartTime == 0L) {
            timer.text = "00:00"
            statusText.text = "Calling..."
            timer.alpha = 0f
            soundIndicator.alpha = 0f
            return
        }

        // Show timer and hide calling status
        if (timer.alpha == 0f) {
            timer.animate().alpha(1f).setDuration(300).start()
            statusText.animate().alpha(0f).setDuration(300).start()
            soundIndicator.animate().alpha(1f).setDuration(300).start()
        }

        val elapsed = (System.currentTimeMillis() - CallSession.callStartTime) / 1000
        val min = elapsed / 60
        val sec = elapsed % 60
        timer.text = String.format("%02d:%02d", min, sec)
    }

    private fun animateSoundBars() {
        soundBars.forEachIndexed { index, bar ->
            val randomHeight = Random.nextInt(20, 60)
            bar.animate()
                .scaleY(randomHeight / 40f)
                .setDuration(150)
                .start()
        }
    }

    private fun updateMuteButton() {
        animateButtonPress(muteBtn)

        if (muted) {
            // Change to muted mic icon
            muteBtnIcon.setImageResource(android.R.drawable.ic_btn_speak_now)
            (muteBtn.background as GradientDrawable).setColor(Color.parseColor("#FF3B30"))
            muteBtnIcon.setColorFilter(Color.WHITE)
            muteBtnText.text = "Unmute"
            muteBtnText.setTextColor(Color.parseColor("#FF3B30"))
            
            // Stop sound bars animation
            soundBars.forEach { bar ->
                bar.animate().scaleY(0.3f).setDuration(200).start()
            }
        } else {
            // Change back to unmuted mic icon
            muteBtnIcon.setImageResource(android.R.drawable.ic_btn_speak_now)
            (muteBtn.background as GradientDrawable).setColor(Color.WHITE)
            muteBtnIcon.setColorFilter(Color.parseColor("#667781"))
            muteBtnText.text = "Mute"
            muteBtnText.setTextColor(Color.parseColor("#667781"))
        }
    }

    private fun updateSpeakerButton() {
        animateButtonPress(speakerBtn)

        if (speakerOn) {
            (speakerBtn.background as GradientDrawable).setColor(Color.parseColor("#00A884"))
            speakerBtnIcon.setColorFilter(Color.WHITE)
            speakerBtnText.text = "Speaker"
            speakerBtnText.setTextColor(Color.parseColor("#00A884"))
        } else {
            (speakerBtn.background as GradientDrawable).setColor(Color.WHITE)
            speakerBtnIcon.setColorFilter(Color.parseColor("#667781"))
            speakerBtnText.text = "Speaker"
            speakerBtnText.setTextColor(Color.parseColor("#667781"))
        }
    }

    private fun animateButtonPress(button: View) {
        button.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .setDuration(75)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(75)
                    .start()
            }
            .start()
    }

    private fun animateEndCall() {
        endBtn.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
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
        val pulseAnimator = ValueAnimator.ofFloat(1f, 1.2f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                pulseView.scaleX = scale
                pulseView.scaleY = scale
                pulseView.alpha = 1f - (scale - 1f) * 3
            }
        }
        pulseAnimator.start()
    }

    private fun animateEntrance() {
        // Avatar animation
        avatarCard.scaleX = 0f
        avatarCard.scaleY = 0f
        avatarCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Name fade in
        nameText.alpha = 0f
        nameText.animate()
            .alpha(1f)
            .setStartDelay(300)
            .setDuration(400)
            .start()

        // All buttons slide up animation
        val buttons = listOf(muteBtn, endBtn, speakerBtn)
        buttons.forEachIndexed { index, button ->
            button.translationY = 150f
            button.alpha = 0f
            button.animate()
                .translationY(0f)
                .alpha(1f)
                .setStartDelay(200L + (index * 80L))
                .setDuration(400)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }
}