/*
 *
 *  * Copyright (c) 2024 Angel Studio
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package fr.angel.soundtap.data.settings.customization

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.ToggleOn
import androidx.compose.material.icons.rounded.UnfoldMoreDouble
import androidx.compose.ui.graphics.vector.ImageVector
import fr.angel.soundtap.data.enums.AutoPlayMode
import fr.angel.soundtap.data.enums.HapticFeedbackLevel
import fr.angel.soundtap.data.enums.WorkingMode
import fr.angel.soundtap.data.settings.customization.CustomizationSettings.Companion.ACTION_FAST_FORWARD
import fr.angel.soundtap.data.settings.customization.CustomizationSettings.Companion.ACTION_NEXT
import fr.angel.soundtap.data.settings.customization.CustomizationSettings.Companion.ACTION_PAUSE
import fr.angel.soundtap.data.settings.customization.CustomizationSettings.Companion.ACTION_PLAY
import fr.angel.soundtap.data.settings.customization.CustomizationSettings.Companion.ACTION_PLAY_PAUSE
import fr.angel.soundtap.data.settings.customization.CustomizationSettings.Companion.ACTION_PREVIOUS
import fr.angel.soundtap.data.settings.customization.CustomizationSettings.Companion.ACTION_REWIND
import fr.angel.soundtap.data.settings.customization.CustomizationSettings.Companion.ACTION_STOP
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

const val DEFAULT_LONG_PRESS_THRESHOLD = 400L
const val DEFAULT_DOUBLE_PRESS_THRESHOLD = 400L
const val DEFAULT_DELAY_BETWEEN_EVENTS = 1000L

@Serializable
data class CustomizationSettings(
	val longPressThreshold: Long = DEFAULT_LONG_PRESS_THRESHOLD,
	val hapticFeedbackLevel: HapticFeedbackLevel = HapticFeedbackLevel.MEDIUM,
	val doublePressThreshold: Long = DEFAULT_DOUBLE_PRESS_THRESHOLD,
	val workingMode: WorkingMode = WorkingMode.SCREEN_ON_OFF,
	val autoPlayMode: AutoPlayMode = AutoPlayMode.ON_HEADSET_CONNECTED,
	val preferredMediaPlayer: String? = null,
	val autoPlay: Boolean = false,
	// Control customization
	val longVolumeUpPressControlMediaAction: ControlMediaAction =
		ControlMediaAction(
			id = 0,
			title = "Long volume up press",
			icon = Icons.Rounded.KeyboardArrowUp,
			enabled = true,
			action = MediaAction.NEXT,
		),
	val longVolumeDownPressControlMediaAction: ControlMediaAction =
		ControlMediaAction(
			id = 1,
			title = "Long volume down press",
			icon = Icons.Rounded.KeyboardArrowDown,
			enabled = true,
			action = MediaAction.PREVIOUS,
		),
	val doubleVolumeLongPressControlMediaAction: ControlMediaAction =
		ControlMediaAction(
			id = 2,
			title = "Double volume long press",
			icon = Icons.Rounded.UnfoldMoreDouble,
			enabled = true,
			action = MediaAction.PLAY_PAUSE,
		),
) {
	companion object {
		const val ACTION_PLAY_PAUSE = "play_pause"
		const val ACTION_NEXT = "next"
		const val ACTION_PREVIOUS = "previous"
		const val ACTION_STOP = "stop"
		const val ACTION_FAST_FORWARD = "fast_forward"
		const val ACTION_REWIND = "rewind"
		const val ACTION_PLAY = "play"
		const val ACTION_PAUSE = "pause"
	}
}

enum class MediaAction(
	val action: String,
	val icon: ImageVector,
	val title: String,
) {
	PLAY_PAUSE(ACTION_PLAY_PAUSE, Icons.Rounded.ToggleOn, "Play/Pause"),
	NEXT(ACTION_NEXT, Icons.Rounded.SkipNext, "Next"),
	PREVIOUS(ACTION_PREVIOUS, Icons.Rounded.SkipPrevious, "Previous"),
	STOP(ACTION_STOP, Icons.Rounded.Stop, "Stop"),
	FAST_FORWARD(ACTION_FAST_FORWARD, Icons.Rounded.FastForward, "Fast forward"),
	REWIND(ACTION_REWIND, Icons.Rounded.FastRewind, "Rewind"),
	PLAY(ACTION_PLAY, Icons.Rounded.PlayArrow, "Play"),
	PAUSE(ACTION_PAUSE, Icons.Rounded.Pause, "Pause"),
}

@Serializable
data class ControlMediaAction(
	val id: Int,
	val title: String,
	val enabled: Boolean,
	val action: MediaAction,
	@Transient val icon: ImageVector? = null,
)
