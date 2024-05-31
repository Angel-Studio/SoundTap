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
package fr.angel.soundtap.ui.app

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.angel.soundtap.GlobalHelper
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.R
import fr.angel.soundtap.data.models.BottomSheetState
import fr.angel.soundtap.service.SleepTimerService
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
	innerPadding: PaddingValues,
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
				.verticalScroll(rememberScrollState())
				.padding(top = innerPadding.calculateTopPadding()),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Text(
			modifier =
				Modifier
					.align(Alignment.CenterHorizontally)
					.padding(horizontal = 8.dp),
			text = stringResource(id = R.string.app_take_control),
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
					title = stringResource(id = R.string.app_notification_listener_permission),
					body = stringResource(id = R.string.app_notification_listener_permission_description),
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
					title = stringResource(id = R.string.app_accessibility_service),
					body = stringResource(id = R.string.app_accessibility_service_description),
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
				animationId = "Customize",
				icon = Icons.Default.Tune,
				label = stringResource(id = R.string.app_customize),
				animatedVisibilityScope = animatedVisibilityScope,
				onClick = navigateToCustomization,
			)
			GridCard(
				modifier = Modifier.weight(1f),
				animationId = "Settings",
				icon = Icons.Default.Settings,
				label = stringResource(id = R.string.app_settings),
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
				animationId = "History",
				icon = Icons.Default.History,
				label = stringResource(id = R.string.app_history),
				animatedVisibilityScope = animatedVisibilityScope,
				onClick = navigateToHistory,
			)
			GridCard(
				modifier = Modifier.weight(1f),
				animationId = "Support",
				icon = Icons.Default.Support,
				label = stringResource(id = R.string.app_support),
				animatedVisibilityScope = animatedVisibilityScope,
				onClick = navigateToSupport,
			)
		}

		Row(
			modifier =
				Modifier
					.fillMaxWidth()
					.padding(horizontal = 8.dp)
					.clip(MaterialTheme.shapes.extraLarge)
					.background(MaterialTheme.colorScheme.surfaceVariant)
					.clickable { }
					.padding(vertical = 16.dp, horizontal = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				modifier =
					Modifier
						.padding(start = 8.dp, end = 16.dp)
						.weight(1f),
				text =
					if (SleepTimerService.isRunning) {
						stringResource(id = R.string.app_music_will_stop, GlobalHelper.formatTime(SleepTimerService.remainingTime))
					} else {
						stringResource(id = R.string.app_sleep_timer)
					},
				style = MaterialTheme.typography.titleMedium,
			)
			Button(
				shape = MaterialTheme.shapes.large,
				colors =
					if (SleepTimerService.isRunning) {
						ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.error,
							contentColor = MaterialTheme.colorScheme.onError,
						)
					} else {
						ButtonDefaults.buttonColors()
					},
				onClick = {
					if (SleepTimerService.isRunning) {
						SleepTimerService.cancelTimer(context)
					} else {
						mainViewModel.showBottomSheet(
							bottomSheetState =
								BottomSheetState.SetTimer(onTimerSet = { duration -> mainViewModel.setSleepTimer(duration) }),
						)
					}
				},
			) {
				Text(
					modifier = Modifier.animateContentSize(),
					text =
						when (SleepTimerService.isRunning) {
							true -> stringResource(id = R.string.app_cancel_timer)
							false -> stringResource(id = R.string.app_set_timer)
						},
					textAlign = TextAlign.Center,
				)
			}
		}

		Spacer(
			modifier =
				Modifier
					.height(8.dp)
					.padding(bottom = innerPadding.calculateBottomPadding()),
		)
	}
}
