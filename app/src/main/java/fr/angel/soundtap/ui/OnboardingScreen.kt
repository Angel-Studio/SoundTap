package fr.angel.soundtap.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
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

@OptIn(ExperimentalMaterial3Api::class)
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

	val sheetState = rememberModalBottomSheetState(
		skipPartiallyExpanded = true,
	)
	val scope = rememberCoroutineScope()
	var showBottomSheet by remember { mutableStateOf(false) }
	val onboardingPages: List<OnboardingPage> = listOf(
		OnboardingPage(
			title = "Welcome to SoundTap",
			description = AnnotatedString(
				"SoundTap is your ultimate companion for controlling your music playback effortlessly." +
						"\n\nWith intuitive volume button controls, customizable settings, and seamless integration with your favorite media players, SoundTap makes managing your music a breeze." +
						"\n\nJoin us on this journey to enhance your music listening experience!"
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
						text = buildAnnotatedString {
							append("I have read and agree to the ")

							withLink(link = LinkAnnotation.Url(GlobalHelper.PRIVACY_POLICY_URL)) {
								withStyle(
									style = SpanStyle(
										color = MaterialTheme.colorScheme.primary,
										fontWeight = FontWeight.Bold
									)
								) {
									append("privacy policy")
								}
							}

							append(" and the ")

							withLink(link = LinkAnnotation.Url(GlobalHelper.TERMS_OF_SERVICE_URL)) {
								withStyle(
									style = SpanStyle(
										color = MaterialTheme.colorScheme.primary,
										fontWeight = FontWeight.Bold
									)
								) {
									append("terms of service")
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
			title = "Access Media Controls",
			description = AnnotatedString(
				"SoundTap requires access to your notifications to detect media playback and control your music." +
						"The app does not collect any personal data or information." +
						"\n\nThis permission is necessary for SoundTap to function properly. Please enable the Notifications access permission to continue." +
						"\n\nTo enable the permission, tap the button below and enable SoundTap from the list."
			),
			nextButtonEnabled = uiState.hasNotificationListenerPermission,
			animationImage = R.raw.music_player,
			actionButtonLabel = "Open Notification Access Settings",
			actionButtonOnClick = { GlobalHelper.openNotificationListenerSettings(context) },
		),
		OnboardingPage(
			title = "Battery Optimization",
			description = AnnotatedString(
				"SoundTap runs in the background to provide you with seamless music playback controls. To ensure that SoundTap works reliably, please disable battery optimization for the app." +
						"\n\nThis will prevent the system from killing SoundTap in the background and ensure that you can control your music playback at all times." +
						"\n\nTo disable battery optimization, tap the button below and select 'Don't optimize' for SoundTap.",
			),
			nextButtonEnabled = uiState.isBackgroundOptimizationDisabled,
			animationImage = R.raw.battery,
			tintedAnimation = true,
			actionButtonLabel = "Disable Battery Optimization",
			actionButtonOnClick = { GlobalHelper.requestBatteryOptimization(context) },
		),
		OnboardingPage(
			title = "Accessibility Service",
			description = AnnotatedString(
				"SoundTap requires the Accessibility Service permission to detect volume button presses and control your music playback." +
						"\n\nThe Accessibility Service only allows SoundTap to detect volume button presses and does not collect any other personal data or information. It does NOT read the screen." +
						"\n\nTo enable the Accessibility Service, tap the button below and enable SoundTap from the list."
			),
			nextButtonEnabled = accessibilityServiceUiState.isRunning && hasAcceptedAccessibilityServiceConditions,
			animationImage = R.raw.empty,
			actionButtonLabel = "Open Accessibility Settings",
			actionButtonOnClick = {
				if (hasAcceptedAccessibilityServiceConditions.not()) {
					showBottomSheet = true
				} else {
					GlobalHelper.openAccessibilitySettings(context)
				}
			},
		),
		OnboardingPage(
			title = "All Set!",
			description = AnnotatedString(
				"Congratulations! You're all set to start using SoundTap." +
						"\n\nTo get started, press the volume up and down buttons simultaneously to toggle the music playback." +
						"\n\nIf you have any questions or need help, feel free to reach out to us."
			),
			animationImage = R.raw.confetti,
			bottomContent = {
				Spacer(modifier = Modifier.weight(1f))
				Text(
					text = "Happy listening!",
					style = MaterialTheme.typography.bodyMedium,
				)
				Spacer(modifier = Modifier.weight(1f))
			},
		),
	)

	var currentPage by rememberSaveable { mutableIntStateOf(0) }

	LaunchedEffect(key1 = currentPage) {
		if (hasAcceptedAccessibilityServiceConditions.not()
			&& currentPage == onboardingPages.indexOfLast { it.title == "Accessibility Service" }
		) {
			showBottomSheet = true
		}
	}
	Scaffold(
		modifier = modifier.fillMaxSize()
	) { contentPadding ->
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.Bottom
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
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 16.dp, horizontal = 32.dp),
				horizontalArrangement = Arrangement.End
			) {
				AnimatedVisibility(visible = currentPage > 0) {
					TextButton(
						onClick = { currentPage = (currentPage - 1).coerceAtLeast(0) }
					) {
						Text("Previous")
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
					enabled = onboardingPages[currentPage].nextButtonEnabled
				) {
					Text(
						modifier = Modifier.animateContentSize(),
						text = when (currentPage) {
							0 -> "Start"
							onboardingPages.size - 1 -> "Finish"
							else -> "Next"
						}
					)
				}
			}
		}

		if (showBottomSheet) {
			ModalBottomSheet(
				onDismissRequest = { showBottomSheet = false },
				sheetState = sheetState,
				windowInsets = WindowInsets(
					bottom = BottomSheetDefaults.windowInsets.getBottom(density)
				)
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					Text(
						"Accessibility Service Usage",
						style = MaterialTheme.typography.headlineMedium
					)

					Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text(
							"Personal and Sensitive User Data:",
							style = MaterialTheme.typography.titleSmall
						)
						Text(
							"SoundTap uses the accessibility service to register volume button click events only. This allows you to control your music playback using volume buttons without unlocking your phone or switching apps. No personal or sensitive user data is collected, stored, or shared.",
							style = MaterialTheme.typography.bodySmall
						)
					}

					Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text(
							"Core Functionality:",
							style = MaterialTheme.typography.titleSmall
						)
						Text(
							"To function properly, SoundTap requires accessibility permission to detect volume button presses. This permission is necessary for the core functionality of the app and ensures that you can seamlessly control your music playback. By granting this permission, you acknowledge and consent to its use for this purpose only.",
							style = MaterialTheme.typography.bodySmall
						)
					}

					Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text(
							"Data Security & Privacy:",
							style = MaterialTheme.typography.titleSmall
						)
						Text(
							"SoundTap strictly uses the accessibility service for registering volume button events and does not access, collect, or transmit any personal or sensitive data. Your privacy is our top priority." +
									"\n\nSoundTap does not collect or share any personal data or information through the accessibility service. All operations are conducted locally on your device, ensuring your data remains secure and private." +
									"\n\nFor more information, please refer to our Privacy Policy.",
							style = MaterialTheme.typography.bodySmall
						)
					}

					Text(
						"By enabling the accessibility service for SoundTap, you consent to the app using this service solely for detecting volume button events to control media playback. You can disable this permission at any time through your device's accessibility settings.",
						style = MaterialTheme.typography.bodySmall
					)

					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						TextButton(onClick = {
							scope.launch { sheetState.hide() }.invokeOnCompletion {
								if (!sheetState.isVisible) {
									showBottomSheet = false
								}
							}
						}) {
							Text("Decline")
						}
						Button(onClick = {
							hasAcceptedAccessibilityServiceConditions = true
							scope.launch { sheetState.hide() }.invokeOnCompletion {
								if (!sheetState.isVisible) {
									showBottomSheet = false
								}
							}
						}) {
							Text("Agree & Continue")
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
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		if (animationImage != null) {

			val dynamicProperties = if (tintedAnimation) {
				rememberLottieDynamicProperties(
					rememberLottieDynamicProperty(
						property = LottieProperty.COLOR_FILTER,
						value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
							MaterialTheme.colorScheme.primary.toArgb(),
							BlendModeCompat.SRC_ATOP
						),
						keyPath = arrayOf(
							"**"
						)
					)
				)
			} else null

			val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationImage))
			Spacer(modifier = Modifier.weight(1f))
			/*LottieAnimation(
				composition = composition,
				iterations = LottieConstants.IterateForever,
				renderMode = RenderMode.HARDWARE,
				isPlaying = true,
				modifier = Modifier
					.fillMaxWidth(0.8f)
					.fillMaxHeight(0.3f)
					.align(Alignment.CenterHorizontally),
				dynamicProperties = dynamicProperties
			)*/
		}
		Spacer(modifier = Modifier.weight(1f))

		Text(
			text = title,
			style = MaterialTheme.typography.headlineMedium
		)
		Text(
			text = description,
			style = MaterialTheme.typography.bodyMedium
		)

		Spacer(modifier = Modifier.weight(1f))

		if (actionButtonOnClick != null) {
			FilledTonalButton(
				modifier = Modifier.fillMaxWidth(),
				onClick = actionButtonOnClick,
				enabled = !nextButtonEnabled
			) {
				Text(if (nextButtonEnabled) "Done!" else actionButtonLabel!!)
			}
		}

		Spacer(modifier = Modifier.weight(1f))

		bottomContent()
	}
}