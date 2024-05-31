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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.angel.soundtap.R

enum class WorkingMode(
	@StringRes val title: Int,
	val selectedComposable: @Composable BoxScope.(selected: Boolean) -> Unit,
) {
	SCREEN_ON_OFF(
		title = R.string.working_mode_screen_on_off,
		selectedComposable = { selected ->
			val alpha by animateFloatAsState(
				if (selected) 1f else 0.2f,
				label = "alpha",
			)

			Icon(
				modifier =
					Modifier
						.padding(4.dp)
						.fillMaxWidth(0.5f)
						.aspectRatio(1f)
						.align(Alignment.TopCenter)
						.alpha(alpha),
				imageVector = Icons.Default.AllInclusive,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
			)
		},
	),
	SCREEN_ON(title = R.string.working_mode_screen_on, selectedComposable = { selected ->
		val alpha by animateFloatAsState(
			if (selected) 1f else 0.2f,
			label = "alpha",
		)

		Text(
			modifier =
				Modifier
					.padding(4.dp)
					.alpha(alpha),
			text = stringResource(id = R.string.awake),
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.primary,
			fontWeight = FontWeight.Black,
		)
	}),
	SCREEN_OFF(title = R.string.working_mode_screen_off, selectedComposable = { selected ->
		val alpha by animateFloatAsState(
			if (selected) 1f else 0.2f,
			label = "alpha",
		)

		Text(
			modifier =
				Modifier
					.padding(4.dp)
					.alpha(alpha),
			text = stringResource(id = R.string.sleep),
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.primary,
			fontWeight = FontWeight.Black,
		)
	}),
}
