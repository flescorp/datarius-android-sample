package kr.co.fles.datarius.sample

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kr.co.fles.datarius.kotlin.android.Analytics
import kr.co.fles.datarius.kotlin.core.*
import kr.co.fles.datarius.sample.plugins.AndroidAdvertisingIdPlugin
import kr.co.fles.datarius.sample.plugins.AndroidRecordScreenPlugin
import kr.co.fles.datarius.sample.plugins.PushNotificationTracking
import kr.co.fles.datarius.kotlin.core.platform.Plugin
import kr.co.fles.datarius.kotlin.core.platform.policies.CountBasedFlushPolicy
import kr.co.fles.datarius.kotlin.core.platform.policies.FrequencyFlushPolicy
import kr.co.fles.datarius.kotlin.core.utilities.*

class MainApplication : Application() {
    companion object {
        lateinit var analytics: Analytics
    }

    override fun onCreate() {
        super.onCreate()

        analytics = Analytics(applicationContext) {
            this.collectDeviceId = true
            this.trackApplicationLifecycleEvents = true
            this.trackDeepLinks = true
            this.flushPolicies = listOf(
                CountBasedFlushPolicy(3), // Flush after 3 events
                FrequencyFlushPolicy(5000), // Flush after 5 secs
                UnmeteredFlushPolicy(applicationContext) // Flush if network is not metered
            )
            this.flushPolicies = listOf(UnmeteredFlushPolicy(applicationContext))
        }
        analytics.add(AndroidRecordScreenPlugin())
        analytics.add(object : Plugin {
            override val type: Plugin.Type = Plugin.Type.Enrichment
            override lateinit var analytics: Analytics

            override fun execute(event: BaseEvent): BaseEvent? {
                event.enableIntegration("AppsFlyer")
                event.disableIntegration("AppBoy")
                event.putInContext("foo", "bar")
                event.putInContextUnderKey("device", "android", true)
                event.removeFromContext("locale")
                return event
            }

        })
        analytics.add(PushNotificationTracking)

        analytics.add(AndroidAdvertisingIdPlugin(this))

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("DatariusSample", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("DatariusSample", token)
        })
    }
}