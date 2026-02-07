package com.foregrounsdservice.calling.bridge

import android.content.Intent
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.foregrounsdservice.calling.core.CallSession
import com.foregrounsdservice.calling.service.CallService
import com.foregrounsdservice.calling.service.CoreService
import com.foregrounsdservice.calling.ui.CallActivity

class CallBridgeModule(
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "CallBridge"

    /* -------------------------------
       CORE SERVICE
    -------------------------------- */

    @ReactMethod
    fun startCoreService() {
        ContextCompat.startForegroundService(
            reactContext,
            Intent(reactContext, CoreService::class.java)
        )
    }

    /* -------------------------------
       CALL SERVICE (NO ACTIVITY)
    -------------------------------- */

    @ReactMethod
    fun startCallService(user: String, type: String) {
        CallSession.userName = user
        CallSession.callType = type

        // ✅ ONLY service
        CallService.start(reactContext)
    }

    /* -------------------------------
       OPEN CALL UI (SAFE)
    -------------------------------- */

    @ReactMethod
    fun openCallScreen() {
        val intent = Intent(reactContext, CallActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        reactContext.startActivity(intent)
    }

    /* -------------------------------
       END CALL
    -------------------------------- */

    @ReactMethod
    fun endCall() {
        CallService.stop(reactContext)
    }
}
