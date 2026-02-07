package com.foregrounsdservice.calling.core

object CallSession {
    var userName: String = "Test User"
    var callType: String = "audio"

    // 🔥 MOST IMPORTANT (timer ke liye)
    var callStartTime: Long = 0L

    fun start(user: String, type: String) {
        userName = user
        callType = type
        callStartTime = System.currentTimeMillis()
    }

    fun clear() {
        callStartTime = 0L
    }
}
