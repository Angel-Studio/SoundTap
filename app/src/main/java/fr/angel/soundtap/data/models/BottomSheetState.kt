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

package fr.angel.soundtap.data.models

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.angel.soundtap.data.settings.customization.CustomControlMediaAction
import fr.angel.soundtap.data.settings.customization.HardwareButtonsEvent
import fr.angel.soundtap.data.settings.customization.MediaAction

sealed class BottomSheetState(
	open val displayName: String?,
	open val onDismiss: (() -> Unit)? = null,
	val content: @Composable (BottomSheetState, () -> Unit) -> Unit,
) {
	data object None : BottomSheetState(
		displayName = "Oops, something went wrong",
		content = { _, _ ->
			Text("An error occurred while trying to display the bottom sheet.")
		},
	)

	data class SetTimer(
		override val displayName: String? = "Set Sleep Timer",
		override val onDismiss: (() -> Unit)? = null,
		val onTimerSet: (Long) -> Unit,
	) : BottomSheetState(
			displayName = displayName,
			content = { state, hide ->
				val timerChoices: Map<String, Long> =
					mapOf(
						"5 minutes" to 5 * 60 * 1000,
						"10 minutes" to 10 * 60 * 1000,
						"15 minutes" to 15 * 60 * 1000,
						"30 minutes" to 30 * 60 * 1000,
						"1 hour" to 60 * 60 * 1000,
						"2 hours" to 2 * 60 * 60 * 1000,
					)

				Column(
					verticalArrangement = Arrangement.spacedBy(4.dp),
				) {
					timerChoices.forEach { (label, duration) ->
						Button(
							modifier = Modifier.fillMaxWidth(),
							shape = MaterialTheme.shapes.medium,
							colors =
								ButtonDefaults.buttonColors(
									containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
									contentColor = MaterialTheme.colorScheme.onSurface,
								),
							onClick = {
								onTimerSet(duration)
								hide()
								state.onDismiss?.invoke()
							},
						) {
							Text(
								modifier =
									Modifier
										.fillMaxWidth()
										.padding(vertical = 8.dp),
								text = label,
								textAlign = TextAlign.Start,
							)
						}
					}
				}
			},
		)

	data class EditControlMediaAction(
		override val displayName: String? = "Control Media Action",
		override val onDismiss: (() -> Unit)? = null,
		val onSetAction: (MediaAction) -> Unit,
	) : BottomSheetState(
			displayName = displayName,
			content = { state, hide ->
				val options = MediaAction.entries

				Column(
					verticalArrangement = Arrangement.spacedBy(4.dp),
				) {
					options.forEach { action ->
						Button(
							modifier = Modifier.fillMaxWidth(),
							shape = MaterialTheme.shapes.medium,
							colors =
								ButtonDefaults.buttonColors(
									containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
									contentColor = MaterialTheme.colorScheme.onSurface,
								),
							onClick = {
								onSetAction(action)

								hide()
								// Dismiss the bottom sheet
								state.onDismiss?.invoke()
							},
						) {
							Icon(
								imageVector = action.icon,
								contentDescription = null,
							)
							Spacer(modifier = Modifier.width(8.dp))
							Text(
								modifier =
									Modifier
										.fillMaxWidth()
										.padding(vertical = 8.dp),
								text = action.title,
								textAlign = TextAlign.Start,
							)
						}
					}
				}
			},
		)

	@OptIn(ExperimentalLayoutApi::class)
	data class EditControlMediaActionSequence(
		override val displayName: String? = "Edit Action Sequence",
		override val onDismiss: (() -> Unit)? = null,
		val customControlMediaAction: CustomControlMediaAction,
		val onSetSequence: (List<HardwareButtonsEvent>) -> Unit,
	) : BottomSheetState(
			displayName = displayName,
			content = { state, hide ->
				val sequence = remember { customControlMediaAction.eventsSequenceList.toMutableStateList() }

				Column(
					modifier =
						Modifier
							.fillMaxWidth()
							.verticalScroll(rememberScrollState()),
					verticalArrangement = Arrangement.spacedBy(16.dp),
				) {
					Text(
						text = "Create a sequence of hardware button events to trigger the action",
						style = MaterialTheme.typography.labelMedium,
					)
					FlowRow(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(4.dp),
						verticalArrangement = Arrangement.spacedBy(4.dp),
					) {
						HardwareButtonsEvent.entries.forEach { event ->
							Box(
								modifier =
									Modifier
										.size(48.dp)
										.clip(MaterialTheme.shapes.medium)
										.background(MaterialTheme.colorScheme.surfaceContainerHigh)
										.clickable(onClick = { sequence.add(event) }),
								contentAlignment = Alignment.Center,
							) {
								Icon(
									imageVector = event.icon,
									contentDescription = null,
								)
							}
						}
					}

					HorizontalDivider()

					Text(
						text = "The current sequence is:",
						style = MaterialTheme.typography.labelMedium,
					)
					FlowRow(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(4.dp),
						verticalArrangement = Arrangement.spacedBy(4.dp),
					) {
						if (sequence.isEmpty()) {
							Text(
								text = "No events in the sequence",
								style = MaterialTheme.typography.bodyMedium,
								fontWeight = FontWeight.Bold,
							)
						}
						sequence.forEachIndexed { index, event ->
							Row(
								horizontalArrangement = Arrangement.spacedBy(4.dp),
								verticalAlignment = Alignment.CenterVertically,
							) {
								if (index != 0) {
									Icon(
										modifier = Modifier.size(16.dp),
										imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
										contentDescription = null,
									)
								}
								Box(
									modifier =
										Modifier
											.size(48.dp)
											.clip(MaterialTheme.shapes.medium)
											.background(MaterialTheme.colorScheme.surfaceContainerHigh)
											.clickable(onClick = { sequence.remove(event) }),
									contentAlignment = Alignment.Center,
								) {
									Icon(
										imageVector = event.icon,
										contentDescription = null,
									)
								}
							}
						}
					}

					Row(
						modifier =
							Modifier
								.fillMaxWidth()
								.padding(top = 16.dp),
						horizontalArrangement = Arrangement.spacedBy(8.dp),
					) {
						FilledTonalButton(
							onClick = {
								hide()
								state.onDismiss?.invoke()
							},
						) {
							Text("Cancel")
						}
						Button(
							modifier = Modifier.weight(1f),
							onClick = {
								hide()
								onSetSequence(sequence)
								state.onDismiss?.invoke()
							},
						) {
							Text("Done")
						}
					}
				}
			},
		)
}
