package fr.angel.soundtap

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.KeyEvent

val supportedStartMediaPlayerPackages = listOf(
	"com.spotify.music"
)

object GlobalHelper {

	const val PRIVACY_POLICY_URL =
		"https://github.com/Angel-Studio/SoundTap/blob/master/PRIVACY-POLICY.MD"
	const val TERMS_OF_SERVICE_URL =
		"https://github.com/Angel-Studio/SoundTap/blob/master/TERMS-CONDITIONS.MD"

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

	fun startMediaPlayer(context: Context, packageName: String) {
		val playSpotify = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
			putExtra(
				Intent.EXTRA_KEY_EVENT,
				KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY)
			)
			`package` = packageName
		}
		context.sendOrderedBroadcast(playSpotify, null)
	}
}