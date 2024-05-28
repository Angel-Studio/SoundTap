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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.SettingsAccessibility
import androidx.compose.material.icons.outlined.Battery0Bar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.angel.soundtap.GlobalHelper
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.VibratorHelper
import fr.angel.soundtap.service.AccessibilityServiceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BottomControlBar(
	modifier: Modifier = Modifier,
	mainViewModel: MainViewModel = viewModel(),
	serviceUiState: AccessibilityServiceState,
) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

	val isSelectedServiceMethodRunning = serviceUiState.isRunning

	val permissionsGranted =
		mainUiState.hasNotificationListenerPermission && isSelectedServiceMethodRunning

	val isSelectedServiceMethodRunningAndActivated =
		isSelectedServiceMethodRunning && serviceUiState.isActivated

	Box(
		modifier =
			modifier
				.fillMaxWidth()
				.height(108.dp)
				.clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
				.background(MaterialTheme.colorScheme.surfaceContainerHigh),
		contentAlignment = Alignment.Center,
	) {
		Row(
			modifier =
				Modifier
					.fillMaxSize()
					.padding(horizontal = 24.dp, vertical = 8.dp),
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			StatusBadgedBox(
				validIcon = Icons.Outlined.Battery0Bar,
				invalidIcon = Icons.Default.BatteryAlert,
				valid = mainUiState.isBackgroundOptimizationDisabled,
				onClick = { GlobalHelper.requestBatteryOptimization(context = context) },
			)
			StatusBadgedBox(
				validIcon = Icons.Default.NotificationsNone,
				invalidIcon = Icons.Default.NotificationsOff,
				valid = mainUiState.hasNotificationListenerPermission,
				onClick = {
					GlobalHelper.openNotificationListenerSettings(
						context = context,
					)
				},
			)
			if (mainUiState.hasNotificationListenerPermission) {
				StatusBadgedBox(
					validIcon = Icons.Default.SettingsAccessibility,
					valid = serviceUiState.isRunning,
					onClick = { GlobalHelper.openAccessibilitySettings(context = context) },
				)
			}

			Spacer(modifier = Modifier.weight(1f))
			Spacer(modifier = Modifier.width(24.dp))

			val buttonBackgroundColor by animateColorAsState(
				targetValue = if (isSelectedServiceMethodRunningAndActivated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint,
				label = "Button Background Color",
			)

			var scale by remember { mutableFloatStateOf(1f) }
			val animatedScale by animateFloatAsState(
				targetValue = scale,
				label = "Button Scale",
				animationSpec =
					spring(
						dampingRatio = Spring.DampingRatioMediumBouncy,
						stiffness = Spring.StiffnessMedium,
					),
			)

			Button(
				modifier =
					Modifier
						.graphicsLayer(
							scaleX = 2f * animatedScale,
							scaleY = 2f * animatedScale,
							translationX = with(LocalDensity.current) { 16.dp.toPx() },
						)
						.aspectRatio(1f),
				onClick = {
					scale = 0.9f

					scope.launch {
						delay(150)
						scale = 1f
					}

					VibratorHelper(context = context).doubleClick()
					mainViewModel.onToggleService()
				},
				colors =
					ButtonDefaults.buttonColors(
						containerColor = buttonBackgroundColor,
					),
				enabled = permissionsGranted,
			) {
				AnimatedContent(
					targetState = isSelectedServiceMethodRunningAndActivated,
					label = "Toggle Service Button",
				) { running ->
					Text(
						text = if (running) "Pause" else "Start",
					)
				}
			}
		}
	}
}
