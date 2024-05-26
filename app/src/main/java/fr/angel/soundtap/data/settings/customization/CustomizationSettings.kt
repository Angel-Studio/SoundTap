package fr.angel.soundtap.data.settings.customization

import fr.angel.soundtap.data.enums.AutoPlayMode
import fr.angel.soundtap.data.enums.HapticFeedbackLevel
import fr.angel.soundtap.data.enums.WorkingMode
import kotlinx.serialization.Serializable

const val DEFAULT_LONG_PRESS_THRESHOLD = 400L
const val DEFAULT_DOUBLE_PRESS_THRESHOLD = 400L
const val DEFAULT_DELAY_BETWEEN_EVENTS = 1000L

@Serializable
data class CustomizationSettings(
	val longPressThreshold: Long = DEFAULT_LONG_PRESS_THRESHOLD,
	val hapticFeedbackLevel: HapticFeedbackLevel = HapticFeedbackLevel.NONE,
	val doublePressThreshold: Long = DEFAULT_DOUBLE_PRESS_THRESHOLD,
	val workingMode: WorkingMode = WorkingMode.SCREEN_ON_OFF,
	val autoPlayMode: AutoPlayMode = AutoPlayMode.ON_HEADSET_CONNECTED,
	val preferredMediaPlayer: String? = null,
	val autoPlay: Boolean = false,
)