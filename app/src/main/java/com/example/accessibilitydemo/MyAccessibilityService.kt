package com.example.accessibilitydemo

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 *
 *
 * created by dzb at 2022/7/10
 */
class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("hello", "enter 1 ")
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.d("hello", "enter")
        }
    }

    override fun onInterrupt() {
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("hello", "onCreate")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
//        setServiceInfo()
    }

    private fun setServiceInfo() {
        val info = serviceInfo
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.notificationTimeout = 600
        info.packageNames = arrayOf("com.tencent.wework")
        serviceInfo = info
    }


}