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

package fr.angel.soundtap.ui.app.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.R
import fr.angel.soundtap.data.settings.customization.ControlMediaAction
import fr.angel.soundtap.data.settings.customization.CustomControlMediaAction
import fr.angel.soundtap.ui.components.settings.SettingsItemCustomBottom

@Composable
fun CustomizationControls(
	modifier: Modifier = Modifier,
	mainViewModel: MainViewModel,
) {
	val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

	var longPressDurationTempValue by remember { mutableFloatStateOf(uiState.customizationSettings.longPressThreshold.toFloat()) }

	LaunchedEffect(uiState.customizationSettings.longPressThreshold) {
		if (longPressDurationTempValue == 0f) {
			longPressDurationTempValue = uiState.customizationSettings.longPressThreshold.toFloat()
		}
	}

	val defaultControls =
		remember(uiState.customizationSettings) {
			listOf(
				uiState.customizationSettings.longVolumeUpPressControlMediaAction,
				uiState.customizationSettings.longVolumeDownPressControlMediaAction,
				uiState.customizationSettings.doubleVolumeLongPressControlMediaAction,
			)
		}

	LazyColumn(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(8.dp),
		contentPadding = PaddingValues(8.dp),
	) {
		items(defaultControls, key = { it.id }) { controlMediaAction ->
			ControlCard(
				modifier = Modifier.fillMaxWidth(),
				controlMediaAction = controlMediaAction,
				onToggle = { mainViewModel.toggleControlMediaAction(controlMediaAction) },
				onActionChange = { mainViewModel.changeControlMediaAction(controlMediaAction) },
			)
		}
		item {
			Row(
				modifier =
					Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp),
			) {
				HorizontalDivider(
					modifier = Modifier.weight(1f),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				Text(
					text = stringResource(R.string.customization_custom_actions).uppercase(),
					fontWeight = FontWeight.Bold,
					style = MaterialTheme.typography.titleMedium,
				)
				HorizontalDivider(
					modifier = Modifier.weight(1f),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
		item {
			Button(
				modifier =
					Modifier
						.fillMaxWidth()
						.heightIn(min = 56.dp),
				onClick = { mainViewModel.addCustomControlMediaAction() },
				shape = MaterialTheme.shapes.medium,
			) {
				Text(text = stringResource(R.string.customization_add_custom_action))
			}
		}
		items(uiState.customizationSettings.customMediaActions, key = { it.id }) { controlMediaAction ->
			CustomControlCard(
				modifier = Modifier.fillMaxWidth(),
				controlMediaAction = controlMediaAction,
				onToggle = { mainViewModel.toggleControlMediaAction(controlMediaAction) },
				onActionChange = { mainViewModel.changeControlMediaAction(controlMediaAction) },
				onRemove = { mainViewModel.removeCustomControlMediaAction(controlMediaAction) },
				onSequenceChange = { mainViewModel.editCustomControlMediaActionSequence(controlMediaAction) },
			)
		}
	}
}

@Composable
private fun ControlCard(
	modifier: Modifier = Modifier,
	controlMediaAction: ControlMediaAction,
	onToggle: (Boolean) -> Unit,
	onActionChange: () -> Unit,
) {
	SettingsItemCustomBottom(
		modifier = modifier,
		title = controlMediaAction.title,
		subtitle = controlMediaAction.action.title,
		icon = controlMediaAction.icon,
		trailing = {
			Switch(
				checked = controlMediaAction.enabled,
				onCheckedChange = onToggle,
			)
		},
		content = {
			Row(
				modifier =
					Modifier
						.fillMaxWidth()
						.clip(MaterialTheme.shapes.medium)
						.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
						.padding(vertical = 8.dp, horizontal = 16.dp),
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Icon(
					imageVector = controlMediaAction.action.icon,
					contentDescription = null,
				)
				Text(
					text = controlMediaAction.action.title,
					modifier = Modifier.weight(1f),
					fontWeight = FontWeight.Bold,
				)
				Spacer(modifier = Modifier.weight(1f))
				Button(
					onClick = onActionChange,
				) {
					Text(text = stringResource(R.string.change))
				}
			}
		},
	)
}

@Composable
private fun CustomControlCard(
	modifier: Modifier = Modifier,
	controlMediaAction: CustomControlMediaAction,
	onToggle: (Boolean) -> Unit,
	onActionChange: () -> Unit,
	onSequenceChange: () -> Unit,
	onRemove: () -> Unit,
) {
	SettingsItemCustomBottom(
		modifier = modifier,
		title = "Custom action",
		subtitle = controlMediaAction.action.title,
		icon = controlMediaAction.icon,
		trailing = {
			Switch(
				checked = controlMediaAction.enabled,
				onCheckedChange = onToggle,
			)
		},
		content = {
			Row(
				modifier =
					Modifier
						.fillMaxWidth()
						.clip(MaterialTheme.shapes.medium)
						.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
						.padding(vertical = 8.dp, horizontal = 16.dp),
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Icon(
					imageVector = controlMediaAction.action.icon,
					contentDescription = null,
				)
				Text(
					text = controlMediaAction.action.title,
					modifier = Modifier.weight(1f),
					fontWeight = FontWeight.Bold,
				)
				Spacer(modifier = Modifier.weight(1f))
				Button(
					onClick = onActionChange,
				) {
					Text(text = stringResource(R.string.change))
				}
			}
			Row(
				modifier =
					Modifier
						.fillMaxWidth()
						.clip(MaterialTheme.shapes.medium)
						.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
						.padding(vertical = 8.dp, horizontal = 16.dp),
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Icon(
					imageVector = Icons.Rounded.FormatListNumbered,
					contentDescription = null,
				)
				Text(
					text = stringResource(R.string.sequence),
					modifier = Modifier.weight(1f),
					fontWeight = FontWeight.Bold,
				)
				Spacer(modifier = Modifier.weight(1f))
				Button(
					onClick = onSequenceChange,
				) {
					Text(text = stringResource(R.string.edit))
				}
			}
			FilledTonalButton(
				modifier = Modifier.fillMaxWidth(),
				onClick = onRemove,
				shape = MaterialTheme.shapes.medium,
			) {
				Text(text = stringResource(R.string.remove))
			}
		},
	)
}
