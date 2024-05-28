/*
 * Copyright 2024 Angel Studio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.angel.soundtap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.KeyEvent

val supportedStartMediaPlayerPackages =
	listOf(
		"com.spotify.music",
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
		return enabledNotificationListeners != null &&
			enabledNotificationListeners.contains(
				packageName,
			)
	}

	@SuppressLint("BatteryLife")
	fun requestBatteryOptimization(context: Context) {
		val batteryOptimizationIntent =
			Intent().apply {
				action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
				data = Uri.parse("package:${context.packageName}")
			}

		context.startActivity(batteryOptimizationIntent)
	}

	fun startMediaPlayer(
		context: Context,
		packageName: String,
	) {
		val startMediaPlayer =
			Intent(Intent.ACTION_MEDIA_BUTTON).apply {
				putExtra(
					Intent.EXTRA_KEY_EVENT,
					KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY),
				)
				`package` = packageName
			}
		context.sendOrderedBroadcast(startMediaPlayer, null)
	}
}
