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
package fr.angel.soundtap.ui

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import fr.angel.soundtap.GlobalHelper
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.R
import fr.angel.soundtap.service.SoundTapAccessibilityService
import kotlinx.coroutines.launch

data class OnboardingPage(
	val title: String,
	val description: AnnotatedString,
	val animationImage: Int? = null,
	val tintedAnimation: Boolean = false,
	val actionButtonOnClick: (() -> Unit)? = null,
	val actionButtonLabel: String? = null,
	val bottomContent: @Composable ColumnScope.() -> Unit = {},
	val nextButtonEnabled: Boolean = true,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
	modifier: Modifier = Modifier,
	mainViewModel: MainViewModel,
) {
	val context = LocalContext.current
	val density = LocalDensity.current

	val accessibilityServiceUiState by SoundTapAccessibilityService.uiState.collectAsStateWithLifecycle()
	val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

	var hasAcceptedPrivacyPolicy by rememberSaveable { mutableStateOf(false) }
	var hasAcceptedAccessibilityServiceConditions by rememberSaveable { mutableStateOf(false) }

	val notificationsPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

	val sheetState =
		rememberModalBottomSheetState(
			skipPartiallyExpanded = true,
		)
	val scope = rememberCoroutineScope()
	var showBottomSheet by remember { mutableStateOf(false) }
	val onboardingPages: List<OnboardingPage> =
		listOf(
			OnboardingPage(
				title = stringResource(id = R.string.onboarding_welcome_message),
				description =
					AnnotatedString(
						stringResource(R.string.onboarding_welcome_description),
					),
				nextButtonEnabled = hasAcceptedPrivacyPolicy,
				animationImage = R.raw.welcome_music,
				bottomContent = {
					Spacer(modifier = Modifier.weight(1f))
					Row(
						verticalAlignment = Alignment.CenterVertically,
					) {
						Checkbox(
							checked = hasAcceptedPrivacyPolicy,
							onCheckedChange = { hasAcceptedPrivacyPolicy = it },
						)
						Text(
							text =
								buildAnnotatedString {
									append(stringResource(R.string.onboarding_accept_privacy_policy))

									withLink(link = LinkAnnotation.Url(GlobalHelper.PRIVACY_POLICY_URL)) {
										withStyle(
											style =
												SpanStyle(
													color = MaterialTheme.colorScheme.primary,
													fontWeight = FontWeight.Bold,
												),
										) {
											append(" ${stringResource(R.string.onboarding_privacy_policy_link)} ")
										}
									}

									append(stringResource(R.string.onboarding_and))

									withLink(link = LinkAnnotation.Url(GlobalHelper.TERMS_OF_SERVICE_URL)) {
										withStyle(
											style =
												SpanStyle(
													color = MaterialTheme.colorScheme.primary,
													fontWeight = FontWeight.Bold,
												),
										) {
											append(" ${stringResource(R.string.onboarding_terms_of_service_link)}")
										}
									}

									append(".")
								},
							style = MaterialTheme.typography.bodyMedium,
						)
					}
					Spacer(modifier = Modifier.weight(1f))
				},
			),
			OnboardingPage(
				title = stringResource(id = R.string.onboarding_notification_access),
				description =
					AnnotatedString(
						stringResource(R.string.onboarding_notification_access_description),
					),
				nextButtonEnabled = uiState.hasNotificationListenerPermission,
				animationImage = R.raw.music_player,
				actionButtonLabel = stringResource(id = R.string.onboarding_open_notification_settings),
				actionButtonOnClick = { GlobalHelper.openNotificationListenerSettings(context) },
			),
			OnboardingPage(
				title = stringResource(id = R.string.onboarding_background_optimization),
				description =
					AnnotatedString(
						stringResource(R.string.onboarding_background_optimization_description),
					),
				nextButtonEnabled = uiState.isBackgroundOptimizationDisabled,
				animationImage = R.raw.battery,
				tintedAnimation = true,
				actionButtonLabel = stringResource(id = R.string.onboarding_open_battery_optimization),
				actionButtonOnClick = { GlobalHelper.requestBatteryOptimization(context) },
			),
			OnboardingPage(
				title = stringResource(id = R.string.onboarding_notifications),
				description =
					AnnotatedString(
						stringResource(R.string.onboarding_notifications_description),
					),
				nextButtonEnabled = notificationsPermissionState.status.isGranted,
				animationImage = R.raw.notifications,
				actionButtonLabel = stringResource(id = R.string.onboarding_open_notification_settings),
				actionButtonOnClick = { notificationsPermissionState.launchPermissionRequest() },
			),
			OnboardingPage(
				title = stringResource(id = R.string.onboarding_accessibility_service),
				description =
					AnnotatedString(
						stringResource(R.string.onboarding_accessibility_service_description),
					),
				nextButtonEnabled = accessibilityServiceUiState.isRunning && hasAcceptedAccessibilityServiceConditions,
				animationImage = R.raw.empty,
				actionButtonLabel = stringResource(id = R.string.onboarding_open_accessibility_settings),
				actionButtonOnClick = {
					if (hasAcceptedAccessibilityServiceConditions.not()) {
						showBottomSheet = true
					} else {
						GlobalHelper.openAccessibilitySettings(context)
					}
				},
			),
			OnboardingPage(
				title = stringResource(id = R.string.onboarding_done),
				description =
					AnnotatedString(
						stringResource(R.string.onboarding_done_description),
					),
				animationImage = R.raw.confetti,
				bottomContent = {
					Spacer(modifier = Modifier.weight(1f))
					Text(
						text = stringResource(id = R.string.onboarding_done_bottom_content),
						style = MaterialTheme.typography.bodyMedium,
					)
					Spacer(modifier = Modifier.weight(1f))
				},
			),
		)

	var currentPage by rememberSaveable { mutableIntStateOf(0) }

	val accessibilityTitle = stringResource(id = R.string.onboarding_accessibility_service)
	LaunchedEffect(key1 = currentPage) {
		if (hasAcceptedAccessibilityServiceConditions.not() &&
			currentPage == onboardingPages.indexOfLast { it.title == accessibilityTitle }
		) {
			showBottomSheet = true
		}
	}
	Scaffold(
		modifier = modifier.fillMaxSize(),
	) { contentPadding ->
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.Bottom,
		) {
			AnimatedContent(
				modifier = Modifier.weight(1f),
				targetState = currentPage,
				label = "Onboarding screen",
			) { page ->
				val onboardingPage = onboardingPages[page]
				TemplatePage(
					modifier = Modifier.fillMaxSize(),
					title = onboardingPage.title,
					description = onboardingPage.description,
					animationImage = onboardingPage.animationImage,
					tintedAnimation = onboardingPage.tintedAnimation,
					bottomContent = onboardingPage.bottomContent,
					actionButtonOnClick = onboardingPage.actionButtonOnClick,
					actionButtonLabel = onboardingPage.actionButtonLabel,
					nextButtonEnabled = onboardingPage.nextButtonEnabled,
				)
			}

			Row(
				modifier =
					Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp, horizontal = 32.dp),
				horizontalArrangement = Arrangement.End,
			) {
				AnimatedVisibility(visible = currentPage > 0) {
					TextButton(
						onClick = { currentPage = (currentPage - 1).coerceAtLeast(0) },
					) {
						Text(stringResource(id = R.string.previous))
					}
				}

				Spacer(modifier = Modifier.weight(1f))

				Button(
					onClick = {
						if (currentPage == onboardingPages.size - 1) {
							mainViewModel.onboardingCompleted()
							return@Button
						}
						currentPage = (currentPage + 1).coerceAtMost(onboardingPages.size - 1)
					},
					enabled = onboardingPages[currentPage].nextButtonEnabled,
				) {
					Text(
						modifier = Modifier.animateContentSize(),
						text =
							when (currentPage) {
								0 -> stringResource(id = R.string.start)
								onboardingPages.size - 1 -> stringResource(id = R.string.finish)
								else -> stringResource(id = R.string.next)
							},
					)
				}
			}
		}

		if (showBottomSheet) {
			ModalBottomSheet(
				onDismissRequest = { showBottomSheet = false },
				sheetState = sheetState,
				windowInsets =
					WindowInsets(
						bottom = BottomSheetDefaults.windowInsets.getBottom(density),
					),
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(16.dp),
				) {
					Text(
						stringResource(id = R.string.onboarding_accessibility_service_usage),
						style = MaterialTheme.typography.headlineMedium,
					)

					Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text(
							stringResource(id = R.string.onboarding_personal_sensitive_data),
							style = MaterialTheme.typography.titleSmall,
						)
						Text(
							stringResource(id = R.string.onboarding_personal_sensitive_data_description),
							style = MaterialTheme.typography.bodySmall,
						)
					}

					Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text(
							stringResource(id = R.string.onboarding_core_functionality),
							style = MaterialTheme.typography.titleSmall,
						)
						Text(
							stringResource(id = R.string.onboarding_core_functionality_description),
							style = MaterialTheme.typography.bodySmall,
						)
					}

					Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text(
							stringResource(id = R.string.onboarding_data_security_privacy),
							style = MaterialTheme.typography.titleSmall,
						)
						Text(
							stringResource(id = R.string.onboarding_data_security_privacy_description),
							style = MaterialTheme.typography.bodySmall,
						)
					}

					Text(
						stringResource(id = R.string.onboarding_accessibility_service_usage_consent),
						style = MaterialTheme.typography.bodySmall,
					)

					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically,
					) {
						TextButton(onClick = {
							scope.launch { sheetState.hide() }.invokeOnCompletion {
								if (!sheetState.isVisible) {
									showBottomSheet = false
								}
							}
						}) {
							Text(stringResource(id = R.string.decline))
						}
						Button(onClick = {
							hasAcceptedAccessibilityServiceConditions = true
							scope.launch { sheetState.hide() }.invokeOnCompletion {
								if (!sheetState.isVisible) {
									showBottomSheet = false
								}
							}
						}) {
							Text(stringResource(id = R.string.agree_continue))
						}
					}
				}
			}
		}
	}
}

@Composable
private fun TemplatePage(
	modifier: Modifier = Modifier,
	animationImage: Int? = null,
	tintedAnimation: Boolean = false,
	title: String,
	description: AnnotatedString,
	actionButtonOnClick: (() -> Unit)? = null,
	actionButtonLabel: String? = null,
	bottomContent: @Composable ColumnScope.() -> Unit = {},
	nextButtonEnabled: Boolean = true,
) {
	Column(
		modifier = modifier.padding(vertical = 16.dp, horizontal = 32.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		if (animationImage != null) {
			val dynamicProperties =
				if (tintedAnimation) {
					rememberLottieDynamicProperties(
						rememberLottieDynamicProperty(
							property = LottieProperty.COLOR_FILTER,
							value =
								BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
									MaterialTheme.colorScheme.primary.toArgb(),
									BlendModeCompat.SRC_ATOP,
								),
							keyPath =
								arrayOf(
									"**",
								),
						),
					)
				} else {
					null
				}

			val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationImage))
			Spacer(modifier = Modifier.weight(1f))
			LottieAnimation(
				composition = composition,
				iterations = LottieConstants.IterateForever,
				renderMode = RenderMode.HARDWARE,
				isPlaying = true,
				modifier =
					Modifier
						.fillMaxWidth(0.8f)
						.fillMaxHeight(0.3f)
						.align(Alignment.CenterHorizontally),
				dynamicProperties = dynamicProperties,
			)
		}
		Spacer(modifier = Modifier.weight(1f))

		Text(
			text = title,
			style = MaterialTheme.typography.headlineMedium,
		)
		Text(
			text = description,
			style = MaterialTheme.typography.bodyMedium,
		)

		Spacer(modifier = Modifier.weight(1f))

		if (actionButtonOnClick != null) {
			FilledTonalButton(
				modifier = Modifier.fillMaxWidth(),
				onClick = actionButtonOnClick,
				enabled = !nextButtonEnabled,
			) {
				Text(if (nextButtonEnabled) "Done!" else actionButtonLabel!!)
			}
		}

		Spacer(modifier = Modifier.weight(1f))

		bottomContent()
	}
}
