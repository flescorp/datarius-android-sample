package kr.co.fles.datarius.sample.plugins

import android.app.Activity
import android.os.Bundle
import kr.co.fles.datarius.kotlin.core.Analytics
import kr.co.fles.datarius.kotlin.core.platform.Plugin
import kr.co.fles.datarius.kotlin.android.plugins.AndroidLifecycle
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Used alongside with MyFirebaseService.kt
 *
 * Firebase Service handles the incoming push notification and fires the Push Notification Received event,
 * if we choose to create a notification based on the incoming push, then this plugin will act upon the
 * opening of that notification and fire the Push Notification Tapped event.
 */

object PushNotificationTracking: Plugin, AndroidLifecycle {
    override val type: Plugin.Type = Plugin.Type.Utility
    override lateinit var analytics: Analytics

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        val bundle = if (savedInstanceState == null) {
            activity?.intent?.extras
        } else {
            Bundle().apply {
                putAll(savedInstanceState)
                putAll(activity?.intent?.extras)
            }
        }
        checkPushNotification(bundle)
    }

    private fun checkPushNotification(bundle: Bundle?) {
        if (bundle != null) {
            if (bundle.containsKey("push_notification")) {
                analytics.track("Push Notification Tapped", buildJsonObject {
                    put("action", "Open")
                    val campaign = buildJsonObject {
                        put("medium", "Push")
                        put("source", "FCM")
                        put("name", bundle.getString("title"))
                        put("content", bundle.getString("content"))
                    }
                    put("campaign", campaign)
                })
            }
        }
    }
}