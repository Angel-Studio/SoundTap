package fr.angel.soundtap

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings


object GlobalHelper {
	fun openAccessibilitySettings(context: Context) {
		val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		context.startActivity(intent)
	}

	fun openNotificationListenerSettings(context: Context) {
		val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
		context.startActivity(intent)
	}

	fun hasNotificationListenerPermission(context: Context): Boolean {
		val contentResolver = context.contentResolver
		val enabledNotificationListeners =
			Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
		val packageName = context.packageName
		return enabledNotificationListeners != null && enabledNotificationListeners.contains(
			packageName
		)
	}

	fun requestBatteryOptimization(context: Context) {
		val batteryOptimizationIntent = Intent().apply {
			action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
			data = Uri.parse("package:${context.packageName}")
		}

		context.startActivity(batteryOptimizationIntent)
	}
}