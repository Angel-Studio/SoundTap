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
package fr.angel.soundtap.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.util.Log
import android.view.Display
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import fr.angel.soundtap.GlobalHelper
import fr.angel.soundtap.VibratorHelper
import fr.angel.soundtap.data.enums.AutoPlayMode
import fr.angel.soundtap.data.enums.HapticFeedbackLevel
import fr.angel.soundtap.data.enums.WorkingMode
import fr.angel.soundtap.data.enums.isOnHeadsetConnectedActive
import fr.angel.soundtap.data.settings.customization.DEFAULT_DELAY_BETWEEN_EVENTS
import fr.angel.soundtap.data.settings.customization.DEFAULT_DOUBLE_PRESS_THRESHOLD
import fr.angel.soundtap.data.settings.customization.DEFAULT_LONG_PRESS_THRESHOLD
import fr.angel.soundtap.data.settings.customization.customizationSettingsDataStore
import fr.angel.soundtap.service.media.MediaReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AccessibilityServiceState(
	val isRunning: Boolean = false,
	val isActivated: Boolean = false,
	val volumeUpLastPressedTime: Long = 0,
	val volumeDownLastPressedTime: Long = 0,
	val lastEventTime: Long = 0,
) {
	val isVolumeUpPressed: Boolean
		get() = volumeUpLastPressedTime > 0
	val isVolumeDownPressed: Boolean
		get() = volumeDownLastPressedTime > 0

	fun isVolumeUpLongPressed(longPressThreshold: Long): Boolean =
		isVolumeUpPressed && (System.currentTimeMillis() - volumeUpLastPressedTime >= longPressThreshold)

	fun isVolumeDownLongPressed(longPressThreshold: Long): Boolean =
		isVolumeDownPressed && (System.currentTimeMillis() - volumeDownLastPressedTime >= longPressThreshold)

	// Both volume buttons pressed
	private val isBothVolumePressed: Boolean
		get() = isVolumeUpPressed && isVolumeDownPressed

	// Long press both volume buttons
	fun isBothVolumeLongPressed(doublePressThreshold: Long): Boolean =
		isBothVolumePressed &&
			(
				System.currentTimeMillis() - volumeUpLastPressedTime >= doublePressThreshold ||
					System.currentTimeMillis() - volumeDownLastPressedTime >= doublePressThreshold
			)
}

class SoundTapAccessibilityService : AccessibilityService() {
	private val scope by lazy { CoroutineScope(Dispatchers.IO) }
	private val listenerScope by lazy { CoroutineScope(Dispatchers.IO) }

	private lateinit var audioManager: AudioManager
	private lateinit var displayManager: DisplayManager
	private lateinit var vibratorHelper: VibratorHelper

	private var hapticFeedbackLevel = HapticFeedbackLevel.NONE
	private var longPressThreshold = DEFAULT_LONG_PRESS_THRESHOLD
	private var doublePressThreshold = DEFAULT_DOUBLE_PRESS_THRESHOLD
	private var workingMode = WorkingMode.SCREEN_ON_OFF
	private var autoPlayMode = AutoPlayMode.ON_HEADSET_CONNECTED
	private var preferredMediaPlayer: String? = null

	companion object {
		private const val TAG = "SoundTapAccessibilityService"

		private val _uiState = MutableStateFlow(AccessibilityServiceState())
		val uiState: StateFlow<AccessibilityServiceState> = _uiState.asStateFlow()

		private fun setRunning(isRunning: Boolean) {
			_uiState.value = _uiState.value.copy(isRunning = isRunning)
		}

		private fun setActivated(isActivated: Boolean) {
			_uiState.value = _uiState.value.copy(isActivated = isActivated)
		}

		fun toggleService() {
			if (_uiState.value.isActivated) {
				setActivated(false)
			} else {
				setActivated(true)
			}
		}
	}

	override fun onKeyEvent(event: KeyEvent?): Boolean {
		// Skip the event if the service is not activated or that no media receiver is registered
		if (event == null ||
			_uiState.value.isActivated.not() ||
			(MediaReceiver.firstCallback == null && autoPlayMode.isOnHeadsetConnectedActive.not())
		) {
			return super.onKeyEvent(null)
		}

		// Filter events based on the working mode
		when (workingMode) {
			WorkingMode.SCREEN_ON_OFF -> {}
			WorkingMode.SCREEN_ON -> {
				if (displayManager.displays.any {
						it.state != Display.STATE_ON
					}
				) {
					return super.onKeyEvent(event)
				}
			}

			WorkingMode.SCREEN_OFF -> {
				if (displayManager.displays.any {
						it.state != Display.STATE_DOZE ||
							it.state != Display.STATE_OFF ||
							it.state != Display.STATE_DOZE_SUSPEND
					}
				) {
					return super.onKeyEvent(event)
				}
			}
		}

		val action = event.action
		val keyCode = event.keyCode

		when (action) {
			KeyEvent.ACTION_DOWN -> {
				when (keyCode) {
					KeyEvent.KEYCODE_VOLUME_UP -> {
						_uiState.value =
							_uiState.value.copy(volumeUpLastPressedTime = System.currentTimeMillis())
					}

					KeyEvent.KEYCODE_VOLUME_DOWN -> {
						_uiState.value =
							_uiState.value.copy(volumeDownLastPressedTime = System.currentTimeMillis())
					}
				}

				listenerScope.launch { listenForEvents() }

				return true
			}

			KeyEvent.ACTION_UP -> {
				when (keyCode) {
					KeyEvent.KEYCODE_VOLUME_UP -> {
						if (!_uiState.value.isVolumeUpLongPressed(longPressThreshold)) {
							audioManager.adjustStreamVolume(
								AudioManager.STREAM_MUSIC,
								AudioManager.ADJUST_RAISE,
								AudioManager.FLAG_SHOW_UI,
							)
						}
						_uiState.value = _uiState.value.copy(volumeUpLastPressedTime = 0)
					}

					KeyEvent.KEYCODE_VOLUME_DOWN -> {
						if (!_uiState.value.isVolumeDownLongPressed(longPressThreshold)) {
							audioManager.adjustStreamVolume(
								AudioManager.STREAM_MUSIC,
								AudioManager.ADJUST_LOWER,
								AudioManager.FLAG_SHOW_UI,
							)
						}
						_uiState.value = _uiState.value.copy(volumeDownLastPressedTime = 0)
					}
				}

				return true
			}

			else -> return super.onKeyEvent(event)
		}
	}

	override fun onCreate() {
		setRunning(true)
		setActivated(true)
		super.onCreate()

		audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
		vibratorHelper = VibratorHelper(this)
		displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

		scope.launch(Dispatchers.IO) {
			customizationSettingsDataStore.data.collect {
				hapticFeedbackLevel = it.hapticFeedbackLevel
				longPressThreshold = it.longPressThreshold
				doublePressThreshold = it.doublePressThreshold
				workingMode = it.workingMode
				autoPlayMode = it.autoPlayMode
				preferredMediaPlayer = it.preferredMediaPlayer
			}
		}

		try {
			MediaReceiver.register(this.application)
		} catch (e: Exception) {
			Log.e(TAG, "Failed to register media receiver, is the permission granted?", e)
			disableSelf()
		}
	}

	override fun onDestroy() {
		setRunning(false)
		MediaReceiver.unregister(this)

		// Release scope
		scope.cancel()
		listenerScope.cancel()

		super.onDestroy()
	}

	override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

	override fun onInterrupt() {}

	/**
	 * EVENT:
	 * Execute the event based on the state of the volume buttons.
	 * **/
	private suspend fun listenForEvents() {
		while (_uiState.value.isVolumeUpPressed || _uiState.value.isVolumeDownPressed) {
			// Delay between events
			delay(50)

			when (workingMode) {
				// Do nothing
				WorkingMode.SCREEN_ON_OFF -> {
					executeEvent()
				}
				// Skip the event if the screen is off
				WorkingMode.SCREEN_ON -> {
					if (displayManager.displays.any {
							it.state == Display.STATE_ON
						}
					) {
						executeEvent()
					}
				}
				// Skip the event if the screen is on
				WorkingMode.SCREEN_OFF -> {
					if (displayManager.displays.any {
							it.state == Display.STATE_DOZE ||
								it.state == Display.STATE_OFF ||
								it.state == Display.STATE_DOZE_SUSPEND
						}
					) {
						executeEvent()
					}
				}
			}
		}
	}

	private suspend fun executeEvent() {
		var event: (() -> Unit)? = null

		if (uiState.value.isBothVolumeLongPressed(doublePressThreshold)) {
			event = { bothVolumePressed() }
		} else {
			if (uiState.value.isVolumeUpLongPressed(longPressThreshold)) {
				event = { volumeUpLongPressed() }
			}
			if (uiState.value.isVolumeDownLongPressed(longPressThreshold)) {
				event = { volumeDownLongPressed() }
			}
		}

		event?.let {
			it()
			_uiState.value = _uiState.value.copy(lastEventTime = System.currentTimeMillis())
			delay(DEFAULT_DELAY_BETWEEN_EVENTS)
		}
	}

	/**
	 * ACTIONS:
	 * These are the actions that will be executed when the corresponding event is detected.
	 **/

	private fun volumeUpLongPressed() {
		vibratorHelper.createHapticFeedback(hapticFeedbackLevel)
		MediaReceiver.firstCallback?.skipToNext()
	}

	private fun volumeDownLongPressed() {
		vibratorHelper.createHapticFeedback(hapticFeedbackLevel)
		MediaReceiver.firstCallback?.skipToPrevious()
	}

	private fun bothVolumePressed() {
		if (MediaReceiver.firstCallback == null) {
			preferredMediaPlayer?.let {
				vibratorHelper.doubleClick()
				GlobalHelper.startMediaPlayer(context = this.application, packageName = it)
			}
		} else {
			vibratorHelper.createHapticFeedback(hapticFeedbackLevel)
			MediaReceiver.firstCallback?.togglePlayPause()
		}
	}
}
