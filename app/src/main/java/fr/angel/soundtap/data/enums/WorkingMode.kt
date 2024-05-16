package fr.angel.soundtap.data.enums

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class WorkingMode(
	val title: String,
	val selectedComposable: @Composable BoxScope.(selected: Boolean) -> Unit,
) {
	SCREEN_ON_OFF(
		title = "Screen ON and OFF",
		selectedComposable = { selected ->
			val alpha by animateFloatAsState(
				if (selected) 1f else 0.2f,
				label = "alpha"
			)

			Icon(
				modifier = Modifier
					.padding(4.dp)
					.fillMaxWidth(0.5f)
					.aspectRatio(1f)
					.align(Alignment.TopCenter)
					.alpha(alpha),
				imageVector = Icons.Default.AllInclusive,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary
			)
		}
	),
	SCREEN_ON(title = "Screen ON", selectedComposable = { selected ->
		val alpha by animateFloatAsState(
			if (selected) 1f else 0.2f,
			label = "alpha"
		)

		Text(
			modifier = Modifier
				.padding(4.dp)
				.alpha(alpha),
			text = "ON",
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.primary,
			fontWeight = FontWeight.Black
		)

	}),
	SCREEN_OFF(title = "Screen OFF", selectedComposable = { selected ->
		val alpha by animateFloatAsState(
			if (selected) 1f else 0.2f,
			label = "alpha"
		)

		Text(
			modifier = Modifier
				.padding(4.dp)
				.alpha(alpha),
			text = "SLEEP",
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.primary,
			fontWeight = FontWeight.Black
		)
	}),
}