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
package fr.angel.soundtap.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsItemCustomBottom(
	modifier: Modifier = Modifier,
	title: String,
	subtitle: String? = null,
	icon: ImageVector? = null,
	enabled: Boolean = true,
	expanded: Boolean = true,
	content: @Composable (() -> Unit)? = null,
	trailing: @Composable (() -> Unit)? = null,
	onClick: () -> Unit = {},
	onLongClick: () -> Unit = {},
) {
	val iconBackgroundColor by animateColorAsState(
		label = "iconBackgroundColor",
		targetValue =
			if (enabled) {
				MaterialTheme.colorScheme.primary
			} else {
				MaterialTheme.colorScheme.onSurface.copy(0.2f)
			},
	)
	val iconColor by animateColorAsState(
		label = "iconColor",
		targetValue =
			if (enabled) {
				MaterialTheme.colorScheme.onPrimary
			} else {
				MaterialTheme.colorScheme.onSurface.copy(0.5f)
			},
	)

	Surface(
		modifier =
			modifier
				.fillMaxWidth()
				.clip(MaterialTheme.shapes.medium)
				.combinedClickable(
					onClick = { if (enabled) onClick() },
					onLongClick = { if (enabled) onLongClick() },
				),
		shape = MaterialTheme.shapes.medium,
		tonalElevation =
			if (!enabled) {
				4.dp
			} else if (content != null) {
				64.dp
			} else {
				4.dp
			},
	) {
		Column(
			modifier =
				Modifier
					.fillMaxWidth()
					.padding(16.dp),
		) {
			Row(
				modifier =
					Modifier
						.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
			) {
				if (icon != null) {
					Box(
						modifier =
							Modifier
								.clip(CircleShape)
								.background(iconBackgroundColor),
					) {
						Icon(
							imageVector = icon,
							contentDescription = null,
							tint = iconColor,
							modifier = Modifier.padding(8.dp),
						)
					}
					Spacer(modifier = Modifier.width(16.dp))
				}
				Column(
					modifier =
						Modifier
							.weight(1f)
							.alpha(if (enabled) 1f else 0.6f),
				) {
					Text(
						text = title,
						style = MaterialTheme.typography.titleMedium,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
					if (subtitle != null) {
						Spacer(modifier = Modifier.height(4.dp))
						Text(
							text = subtitle,
							style = MaterialTheme.typography.labelMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
					}
				}
				if (trailing != null) {
					Spacer(modifier = Modifier.width(16.dp))
					trailing()
				}
			}
			if (content != null) {
				AnimatedVisibility(
					visible = expanded,
					enter = fadeIn() + expandVertically(),
					exit = fadeOut() + shrinkVertically(),
				) {
					Column(
						verticalArrangement = Arrangement.spacedBy(4.dp),
					) {
						Spacer(modifier = Modifier.height(16.dp))
						content()
					}
				}
			}
		}
	}
}
