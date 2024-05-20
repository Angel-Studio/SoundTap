package fr.angel.soundtap.data.enums

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

enum class AutoPlayMode(
	val title: String,
	val selectedComposable: @Composable BoxScope.(selected: Boolean) -> Unit,
) {
	ON_HEADSET_CONNECTED(
		title = "On headset connected",
		selectedComposable = { selected ->
			val alpha by animateFloatAsState(
				if (selected) 1f else 0.2f,
				label = "alpha"
			)

			Icon(
				modifier = Modifier
					.padding(4.dp)
					.fillMaxWidth(0.25f)
					.aspectRatio(1f)
					.align(Alignment.TopCenter)
					.alpha(alpha),
				imageVector = Icons.Default.Headset,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary
			)
		}
	),
	ON_DOUBLE_VOLUME_LONG_PRESS(
		title = "On double volume long press",
		selectedComposable = { selected ->
			val alpha by animateFloatAsState(
				if (selected) 1f else 0.2f,
				label = "alpha"
			)

			Icon(
				modifier = Modifier
					.padding(4.dp)
					.fillMaxWidth(0.25f)
					.aspectRatio(1f)
					.align(Alignment.TopCenter)
					.alpha(alpha),
				imageVector = Icons.AutoMirrored.Filled.VolumeUp,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary
			)
		}
	),
	BOTH(
		title = "Both",
		selectedComposable = { selected ->
			val alpha by animateFloatAsState(
				if (selected) 1f else 0.2f,
				label = "alpha"
			)

			Row(
				modifier = Modifier
					.padding(4.dp)
					.fillMaxWidth(0.5f)
					.align(Alignment.TopCenter)
					.alpha(alpha),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
			) {
				Icon(
					modifier = Modifier.weight(1f),
					imageVector = Icons.Default.Headset,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary
				)
				Icon(
					modifier = Modifier.weight(1f),
					imageVector = Icons.AutoMirrored.Filled.VolumeUp,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary
				)
			}
		}
	)
}

val AutoPlayMode.isOnHeadsetConnectedActive: Boolean
	get() = this == AutoPlayMode.ON_HEADSET_CONNECTED || this == AutoPlayMode.BOTH

val AutoPlayMode.isOnDoubleVolumeLongPressActive: Boolean
	get() = this == AutoPlayMode.ON_DOUBLE_VOLUME_LONG_PRESS || this == AutoPlayMode.BOTH