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
package fr.angel.soundtap.ui.app

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.NotificationImportant
import androidx.compose.material.icons.outlined.SettingsAccessibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.angel.soundtap.GlobalHelper
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.service.SoundTapAccessibilityService
import fr.angel.soundtap.service.media.MediaReceiver
import fr.angel.soundtap.ui.components.GridCard
import fr.angel.soundtap.ui.components.InfoCard
import fr.angel.soundtap.ui.components.InfoCardType
import fr.angel.soundtap.ui.components.MediaCards

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.App(
	modifier: Modifier = Modifier,
	animatedVisibilityScope: AnimatedVisibilityScope,
	navigateToCustomization: () -> Unit,
	navigateToHistory: () -> Unit,
	navigateToSettings: () -> Unit,
	navigateToSupport: () -> Unit,
	mainViewModel: MainViewModel,
) {
	val context = LocalContext.current
	val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
	val accessibilityServiceState by SoundTapAccessibilityService.uiState.collectAsStateWithLifecycle()
	val mediaCallback = MediaReceiver.firstCallback
	var lastCallback by remember { mutableStateOf(mediaCallback) }

	LaunchedEffect(mediaCallback) {
		if (mediaCallback != null) {
			lastCallback = mediaCallback
		}
	}

	Column(
		modifier =
			modifier
				.fillMaxWidth()
				.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Text(
			modifier =
				Modifier
					.align(Alignment.CenterHorizontally)
					.padding(horizontal = 8.dp),
			text = "Take Control of Your Music",
			style = MaterialTheme.typography.labelLarge,
			fontWeight = FontWeight.SemiBold,
			textAlign = TextAlign.Center,
		)
		Spacer(modifier = Modifier.height(8.dp))

		when {
			uiState.hasNotificationListenerPermission.not() -> {
				InfoCard(
					modifier =
						Modifier
							.fillMaxWidth()
							.padding(horizontal = 8.dp),
					cardType = InfoCardType.Notification,
					icon = Icons.Outlined.NotificationImportant,
					title = "Notification Listener Permission",
					body =
						"SoundTap needs the Notification Listener permission to receive media information and control your music." +
							" Please enable it by pressing the button at the bottom of the screen.",
					onCardClick = {
						GlobalHelper.openNotificationListenerSettings(context = context)
					},
				)
			}

			accessibilityServiceState.isRunning.not() -> {
				InfoCard(
					modifier =
						Modifier
							.fillMaxWidth()
							.padding(horizontal = 8.dp),
					cardType = InfoCardType.Accessibility,
					icon = Icons.Outlined.SettingsAccessibility,
					title = "Accessibility Service",
					body =
						"SoundTap needs the Accessibility Service to receive volume key events." +
							" Please enable it by pressing the button at the bottom of the screen.",
					onCardClick = {
						GlobalHelper.openAccessibilitySettings(context = context)
					},
				)
			}

			else -> {
				MediaCards(
					modifier =
						Modifier
							.fillMaxWidth()
							.height(200.dp),
					mainViewModel = mainViewModel,
				)
			}
		}

		Row(
			modifier =
				Modifier
					.fillMaxWidth()
					.padding(horizontal = 8.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			GridCard(
				modifier = Modifier.weight(1f),
				icon = Icons.Default.Tune,
				label = "Customize",
				animatedVisibilityScope = animatedVisibilityScope,
				onClick = navigateToCustomization,
			)
			GridCard(
				modifier = Modifier.weight(1f),
				icon = Icons.Default.Settings,
				label = "Settings",
				animatedVisibilityScope = animatedVisibilityScope,
				onClick = navigateToSettings,
			)
		}

		Row(
			modifier =
				Modifier
					.fillMaxWidth()
					.padding(horizontal = 8.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			GridCard(
				modifier = Modifier.weight(1f),
				icon = Icons.Default.History,
				label = "History",
				animatedVisibilityScope = animatedVisibilityScope,
				onClick = navigateToHistory,
			)
			GridCard(
				modifier = Modifier.weight(1f),
				icon = Icons.Default.Support,
				label = "Support",
				animatedVisibilityScope = animatedVisibilityScope,
				onClick = navigateToSupport,
			)
		}

		Spacer(modifier = Modifier.height(8.dp))
	}
}
