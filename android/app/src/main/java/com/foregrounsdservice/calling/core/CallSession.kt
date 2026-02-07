package com.foregrounsdservice.calling.core

object CallSession {
    var userName = "Unknown"
    var callType = "audio"
    var state = CallState.IDLE

    fun clear() {
        userName = "Unknown"
        callType = "audio"
        state = CallState.IDLE
    }
}
