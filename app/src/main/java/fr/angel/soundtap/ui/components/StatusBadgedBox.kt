/*
 * Copyright 2024 Angel Studio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.angel.soundtap.ui.components

import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun StatusBadgedBox(
	modifier: Modifier = Modifier,
	validIcon: ImageVector,
	invalidIcon: ImageVector = validIcon,
	valid: Boolean,
	onClick: () -> Unit,
) {
	val infiniteTransition = rememberInfiniteTransition(label = "scaleAnimation")
	val scaleAnimation by infiniteTransition.animateFloat(
		initialValue = 1f,
		targetValue = if (valid) 1f else 1.1f,
		animationSpec = infiniteRepeatable(
			animation = tween(
				delayMillis = 500,
				durationMillis = 1200,
				easing = EaseInElastic
			),
			repeatMode = RepeatMode.Reverse
		), label = "scaleAnimation"
	)
	BadgedBox(
		modifier = modifier
			.size(48.dp)
			.graphicsLayer(
				scaleX = scaleAnimation,
				scaleY = scaleAnimation
			)
			.background(
				if (valid) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
				MaterialTheme.shapes.medium
			),
		badge = {
			Icon(
				imageVector = if (valid) Icons.Default.CheckCircle else Icons.Default.Error,
				contentDescription = null,
				modifier = Modifier
					.size(16.dp)
					.offset(x = (-4).dp)
					.background(
						color = if (valid) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.error,
						shape = CircleShape
					),
				tint = if (valid) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onError
			)
		}
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clip(MaterialTheme.shapes.medium)
				.clickable(onClick = onClick)
		) {
			Icon(
				imageVector = if (valid) validIcon else invalidIcon,
				contentDescription = null,
				modifier = Modifier.align(Alignment.Center),
				tint = if (valid) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onError
			)
		}
	}
}
