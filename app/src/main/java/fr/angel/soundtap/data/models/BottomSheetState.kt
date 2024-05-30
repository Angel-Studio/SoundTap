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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

sealed class BottomSheetState(
	open val displayName: String?,
	open val onDismiss: (() -> Unit)? = null,
	val content: @Composable (BottomSheetState) -> Unit,
) {
	data object None : BottomSheetState(
		displayName = "Oops, something went wrong",
		content = { },
	)

	data class SetTimer(
		override val displayName: String? = "Set Sleep Timer",
		override val onDismiss: (() -> Unit)? = null,
		val onTimerSet: (Long) -> Unit,
	) : BottomSheetState(
			displayName = displayName,
			content = { state ->
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
					verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
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

								// Dismiss the bottom sheet
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
}
