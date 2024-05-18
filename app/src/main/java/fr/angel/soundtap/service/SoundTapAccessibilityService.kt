package fr.angel.soundtap.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.os.PowerManager
import android.util.Log
import android.view.Display
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import fr.angel.soundtap.VibratorHelper
import fr.angel.soundtap.data.DataStore
import fr.angel.soundtap.data.enums.HapticFeedback
import fr.angel.soundtap.data.enums.WorkingMode
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
	fun isBothVolumeLongPressed(doublePressThreshold: Long): Boolean = isBothVolumePressed
			&& (System.currentTimeMillis() - volumeUpLastPressedTime >= doublePressThreshold
			|| System.currentTimeMillis() - volumeDownLastPressedTime >= doublePressThreshold)
}

class SoundTapAccessibilityService : AccessibilityService() {

	private val scope by lazy { CoroutineScope(Dispatchers.IO) }
	private val listenerScope by lazy { CoroutineScope(Dispatchers.IO) }

	private lateinit var audioManager: AudioManager
	private lateinit var wakeLock: PowerManager.WakeLock
	private lateinit var displayManager: DisplayManager
	private lateinit var vibratorHelper: VibratorHelper
	private lateinit var dataStore: DataStore

	private var hapticFeedback = HapticFeedback.NONE
	private var longPressThreshold = DEFAULT_LONG_PRESS_THRESHOLD
	private var doublePressThreshold = DEFAULT_DOUBLE_PRESS_THRESHOLD
	private var workingMode = WorkingMode.SCREEN_ON_OFF

	companion object {
		private const val TAG = "SoundTapAccessibilityService"

		const val DEFAULT_LONG_PRESS_THRESHOLD = 400L
		const val DEFAULT_DOUBLE_PRESS_THRESHOLD = 400L
		const val DEFAULT_DELAY_BETWEEN_EVENTS = 1000L

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
		if (event == null
			|| _uiState.value.isActivated.not()
			|| MediaReceiver.firstCallback == null
		) return super.onKeyEvent(null)

		// Filter events based on the working mode
		when (workingMode) {
			WorkingMode.SCREEN_ON_OFF -> {}
			WorkingMode.SCREEN_ON -> {
				if (displayManager.displays.any {
						it.state != Display.STATE_ON
					}) {
					return super.onKeyEvent(event)
				}
			}

			WorkingMode.SCREEN_OFF -> {
				if (displayManager.displays.any {
						it.state != Display.STATE_DOZE ||
								it.state != Display.STATE_OFF ||
								it.state != Display.STATE_DOZE_SUSPEND
					}) {
					return super.onKeyEvent(event)
				}
			}
		}

		val action = event.action
		val keyCode = event.keyCode

		val eventName = when (action) {
			KeyEvent.ACTION_DOWN -> "ACTION_DOWN"
			KeyEvent.ACTION_UP -> "ACTION_UP"
			else -> "UNKNOWN"
		}

		val keyName = when (keyCode) {
			KeyEvent.KEYCODE_VOLUME_UP -> "KEYCODE_VOLUME_UP"
			KeyEvent.KEYCODE_VOLUME_DOWN -> "KEYCODE_VOLUME_DOWN"
			else -> "UNKNOWN"
		}

		Log.i(TAG, "onKeyEvent: $eventName:$keyName")

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
								AudioManager.FLAG_SHOW_UI
							)
						}
						_uiState.value = _uiState.value.copy(volumeUpLastPressedTime = 0)
					}

					KeyEvent.KEYCODE_VOLUME_DOWN -> {
						if (!_uiState.value.isVolumeDownLongPressed(longPressThreshold)) {
							audioManager.adjustStreamVolume(
								AudioManager.STREAM_MUSIC,
								AudioManager.ADJUST_LOWER,
								AudioManager.FLAG_SHOW_UI
							)
						}
						_uiState.value = _uiState.value.copy(volumeDownLastPressedTime = 0)
					}
				}

				// listenerScope.cancel()

				return true
			}

			else -> return super.onKeyEvent(event)
		}
	}

	override fun onCreate() {
		setRunning(true)
		setActivated(true)
		super.onCreate()

		dataStore = DataStore(this.application)
		audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
		vibratorHelper = VibratorHelper(this)
		displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

		// Observe the data store settings to update the service settings
		scope.launch { dataStore.hapticFeedback.collect { hapticFeedback = it } }
		scope.launch { dataStore.longPressDuration.collect { longPressThreshold = it } }
		scope.launch { dataStore.doublePressDuration.collect { doublePressThreshold = it } }
		scope.launch { dataStore.workingMode.collect { workingMode = it } }

		try {
			MediaReceiver.register(this.application)
		} catch (e: Exception) {
			Log.e(TAG, "Failed to register media receiver, is the permission granted?", e)
			disableSelf()
		}

		// Acquire a wake lock to keep the service running
		wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
			newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SoundTap::ServiceWakeLock").apply {
				acquire(10 * 60 * 1000L)
				Log.i(TAG, "Wake lock acquired")
			}
		}

		// Observe the state of the volume buttons
		/*scope.launch(Dispatchers.Main) {

			while (true) {
				if (_uiState.value.isActivated) {
					listenForEvents()
				}
			}
		}*/
	}

	override fun onDestroy() {
		setRunning(false)
		MediaReceiver.unregister(this)

		// Release the wake lock
		wakeLock.release()

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

			Log.i(TAG, "Listening for events...")

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
						}) {
						executeEvent()
					}
				}
				// Skip the event if the screen is on
				WorkingMode.SCREEN_OFF -> {
					if (displayManager.displays.any {
							it.state == Display.STATE_DOZE ||
									it.state == Display.STATE_OFF ||
									it.state == Display.STATE_DOZE_SUSPEND
						}) {
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
		vibratorHelper.createHapticFeedback(hapticFeedback)
		MediaReceiver.firstCallback?.skipToNext()
	}

	private fun volumeDownLongPressed() {
		vibratorHelper.createHapticFeedback(hapticFeedback)
		MediaReceiver.firstCallback?.skipToPrevious()
	}

	private fun bothVolumePressed() {
		vibratorHelper.createHapticFeedback(hapticFeedback)
		MediaReceiver.firstCallback?.togglePlayPause()
	}
}