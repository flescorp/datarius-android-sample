package kr.co.fles.datarius.sample.plugins

import android.app.Activity
import android.content.pm.PackageManager
import kr.co.fles.datarius.kotlin.core.Analytics
import kr.co.fles.datarius.kotlin.core.platform.Plugin
import kr.co.fles.datarius.kotlin.android.plugins.AndroidLifecycle
import kr.co.fles.datarius.kotlin.core.platform.plugins.logger.*

class AndroidRecordScreenPlugin : Plugin, AndroidLifecycle {

    override val type: Plugin.Type = Plugin.Type.Utility
    override lateinit var analytics: Analytics

    override fun onActivityStarted(activity: Activity?) {
        val packageManager = activity?.packageManager
        try {
            val info = packageManager?.getActivityInfo(
                activity.componentName,
                PackageManager.GET_META_DATA
            )
            val activityLabel = info?.loadLabel(packageManager)
            analytics.screen(activityLabel.toString())
        } catch (e: PackageManager.NameNotFoundException) {
            throw AssertionError("Activity Not Found: $e")
        } catch (e: Exception) {
            Analytics.segmentLog(
                "Unable to track screen view for ${activity.toString()}",
                kind = LogFilterKind.ERROR
            )
        }
    }

}