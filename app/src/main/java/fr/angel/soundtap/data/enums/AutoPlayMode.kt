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
package fr.angel.soundtap.data.enums

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import fr.angel.soundtap.R

enum class AutoPlayMode(
	@StringRes val title: Int,
	val selectedComposable: @Composable BoxScope.(selected: Boolean) -> Unit,
) {
	ON_HEADSET_CONNECTED(
		title = R.string.auto_play_mode_on_headset_connected,
		selectedComposable = { selected ->
			val alpha by animateFloatAsState(
				if (selected) 1f else 0.2f,
				label = "alpha",
			)

			Icon(
				modifier =
					Modifier
						.padding(4.dp)
						.fillMaxWidth(0.25f)
						.aspectRatio(1f)
						.align(Alignment.TopCenter)
						.alpha(alpha),
				imageVector = Icons.Default.Headset,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
			)
		},
	),
	ON_DOUBLE_VOLUME_LONG_PRESS(
		title = R.string.auto_play_mode_on_double_volume_long_press,
		selectedComposable = { selected ->
			val alpha by animateFloatAsState(
				if (selected) 1f else 0.2f,
				label = "alpha",
			)

			Icon(
				modifier =
					Modifier
						.padding(4.dp)
						.fillMaxWidth(0.25f)
						.aspectRatio(1f)
						.align(Alignment.TopCenter)
						.alpha(alpha),
				imageVector = Icons.AutoMirrored.Filled.VolumeUp,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
			)
		},
	),
	BOTH(
		title = R.string.auto_play_mode_both,
		selectedComposable = { selected ->
			val alpha by animateFloatAsState(
				if (selected) 1f else 0.2f,
				label = "alpha",
			)

			Row(
				modifier =
					Modifier
						.padding(4.dp)
						.fillMaxWidth(0.5f)
						.align(Alignment.TopCenter)
						.alpha(alpha),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
			) {
				Icon(
					modifier = Modifier.weight(1f),
					imageVector = Icons.Default.Headset,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
				)
				Icon(
					modifier = Modifier.weight(1f),
					imageVector = Icons.AutoMirrored.Filled.VolumeUp,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
				)
			}
		},
	),
}

val AutoPlayMode.isOnHeadsetConnectedActive: Boolean
	get() = this == AutoPlayMode.ON_HEADSET_CONNECTED || this == AutoPlayMode.BOTH

val AutoPlayMode.isOnDoubleVolumeLongPressActive: Boolean
	get() = this == AutoPlayMode.ON_DOUBLE_VOLUME_LONG_PRESS || this == AutoPlayMode.BOTH
