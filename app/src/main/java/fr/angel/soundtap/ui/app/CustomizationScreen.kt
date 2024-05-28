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

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.VibratorHelper
import fr.angel.soundtap.animations.PERLIN_NOISE
import fr.angel.soundtap.data.enums.AutoPlayMode
import fr.angel.soundtap.data.enums.HapticFeedbackLevel
import fr.angel.soundtap.data.enums.WorkingMode
import fr.angel.soundtap.ui.components.settings.SettingsItemCustomBottom
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.CustomizationScreen(
	modifier: Modifier = Modifier,
	mainViewModel: MainViewModel,
	animatedVisibilityScope: AnimatedVisibilityScope,
	navigateToSettings: () -> Unit,
) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()

	val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

	var longPressDurationTempValue by remember { mutableFloatStateOf(uiState.customizationSettings.longPressThreshold.toFloat()) }

	LaunchedEffect(uiState.customizationSettings.longPressThreshold) {
		if (longPressDurationTempValue == 0f) {
			longPressDurationTempValue = uiState.customizationSettings.longPressThreshold.toFloat()
		}
	}

	Card(
		modifier =
			modifier
				.padding(8.dp)
				.fillMaxSize()
				.sharedElement(
					state =
						rememberSharedContentState(
							key = "Customize-card",
						),
					animatedVisibilityScope = animatedVisibilityScope,
				),
	) {
		Column(modifier = Modifier.fillMaxSize()) {
			Row(
				modifier =
					Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.surfaceContainerHighest)
						.padding(16.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp),
			) {
				Icon(
					modifier =
						Modifier
							.size(48.dp)
							.sharedElement(
								state =
									rememberSharedContentState(
										key = "Customize-icon",
									),
								animatedVisibilityScope = animatedVisibilityScope,
							),
					imageVector = Icons.Default.Tune,
					contentDescription = null,
				)

				Text(
					text = "Customization",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					modifier =
						Modifier
							.sharedBounds(
								rememberSharedContentState(
									key = "Customize",
								),
								animatedVisibilityScope = animatedVisibilityScope,
							),
				)
			}
			HorizontalDivider()
			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				contentPadding = PaddingValues(8.dp),
			) {
				item {
					SettingsItemCustomBottom(
						title = "Working mode",
						subtitle = "Select when the skipping action should be available.",
						icon = Icons.Default.ToggleOn,
						content = {
							Row(
								modifier = Modifier.fillMaxWidth(),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(4.dp),
							) {
								val settingsList = WorkingMode.entries

								settingsList.forEachIndexed { index, item ->

									val backgroundColor by animateColorAsState(
										targetValue =
											if (uiState.customizationSettings.workingMode.ordinal == index) {
												MaterialTheme.colorScheme.primary.copy(0.3f)
											} else {
												MaterialTheme.colorScheme.onSurface.copy(0.05f)
											},
										label = "backgroundColor",
									)

									val borderColor by animateColorAsState(
										targetValue =
											if (uiState.customizationSettings.workingMode.ordinal == index) {
												MaterialTheme.colorScheme.primary
											} else {
												MaterialTheme.colorScheme.onSurface.copy(0.1f)
											},
										label = "borderColor",
									)

									val cornerShape by animateFloatAsState(
										targetValue =
											if (uiState.customizationSettings.workingMode.ordinal == index) {
												16f
											} else {
												12f
											},
										label = "cornerShape",
									)

									Box(
										modifier =
											Modifier
												.weight(1f)
												.clip(RoundedCornerShape(cornerShape.dp))
												.aspectRatio(1f)
												.background(backgroundColor)
												.border(
													width = 1.dp,
													color = borderColor,
													shape = RoundedCornerShape(cornerShape.dp),
												)
												.clickable {
													VibratorHelper(context = context).click()
													scope.launch {
														mainViewModel.setWorkingMode(item)
													}
												}
												.padding(8.dp),
									) {
										item.selectedComposable(
											this,
											uiState.customizationSettings.workingMode == item,
										)
										Text(
											text = item.title,
											style = MaterialTheme.typography.bodyMedium,
											modifier =
												Modifier
													.padding(8.dp)
													.align(Alignment.BottomCenter),
										)
									}
								}
							}
						},
					)
				}
				item {
					SettingsItemCustomBottom(
						title = "Haptic feedback",
						subtitle = "Vibrate when an action is performed.",
						icon = Icons.Default.Vibration,
						content = {
							Row(
								modifier = Modifier.fillMaxWidth(),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(4.dp),
							) {
								val settingsList = HapticFeedbackLevel.entries

								settingsList.forEachIndexed { index, item ->

									val backgroundColor by animateColorAsState(
										targetValue =
											if (uiState.customizationSettings.hapticFeedbackLevel.ordinal == index) {
												MaterialTheme.colorScheme.primary.copy(0.3f)
											} else {
												MaterialTheme.colorScheme.onSurface.copy(0.05f)
											},
										label = "backgroundColor",
									)

									val borderColor by animateColorAsState(
										targetValue =
											if (uiState.customizationSettings.hapticFeedbackLevel.ordinal == index) {
												MaterialTheme.colorScheme.primary
											} else {
												MaterialTheme.colorScheme.onSurface.copy(0.1f)
											},
										label = "borderColor",
									)

									val cornerShape by animateFloatAsState(
										targetValue =
											if (uiState.customizationSettings.hapticFeedbackLevel.ordinal == index) {
												16f
											} else {
												12f
											},
										label = "cornerShape",
									)

									Box(
										modifier =
											Modifier
												.weight(1f)
												.clip(RoundedCornerShape(cornerShape.dp))
												.aspectRatio(1f)
												.background(backgroundColor)
												.border(
													width = 1.dp,
													color = borderColor,
													shape = RoundedCornerShape(cornerShape.dp),
												)
												.clickable {
													scope.launch {
														mainViewModel.setHapticFeedback(item)
														VibratorHelper(context = context).createHapticFeedback(
															item,
														)
													}
												},
									) {
										val selected =
											uiState.customizationSettings.hapticFeedbackLevel.ordinal == index
										val time by produceState(0f) {
											while (true) {
												withInfiniteAnimationFrameMillis {
													value = it / 100f
												}
											}
										}

										val shaderModifier =
											if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
												val shader =
													remember { RuntimeShader(PERLIN_NOISE) }

												if (selected && item != HapticFeedbackLevel.NONE) {
													Modifier
														.onSizeChanged { size ->
															shader.setFloatUniform(
																"resolution",
																size.width.toFloat() * (0.4f + 0.2f * index),
																size.height.toFloat() * (0.4f + 0.2f * index),
															)
														}
														.graphicsLayer {
															shader.setFloatUniform("time", time)
															renderEffect =
																RenderEffect
																	.createRuntimeShaderEffect(
																		shader,
																		"contents",
																	)
																	.asComposeRenderEffect()
														}
												} else {
													Modifier
												}
											} else {
												Modifier
											}

										Icon(
											imageVector = ImageVector.vectorResource(id = item.icon),
											contentDescription = null,
											tint = MaterialTheme.colorScheme.primary,
											modifier =
												Modifier
													.fillMaxSize()
													.alpha(
														animateFloatAsState(
															targetValue = if (selected) 1f else 0.2f,
															label = "iconAlpha",
														).value,
													)
													.then(shaderModifier),
										)
									}
								}
							}
						},
					)
				}
				item {
					SettingsItemCustomBottom(
						title = "Long press duration - ${longPressDurationTempValue.roundToInt()} ms",
						subtitle = "Set the duration for a long press action.",
						icon = Icons.Default.TouchApp,
						content = {
							val interactionSource = remember { MutableInteractionSource() }
							val isPressed by interactionSource.collectIsDraggedAsState()

							Row(
								modifier = Modifier.fillMaxWidth(),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(4.dp),
							) {
								IconButton(
									onClick = {
										longPressDurationTempValue -= 100
										VibratorHelper(context = context).tick()
										scope.launch {
											mainViewModel.setLongPressDuration(
												longPressDurationTempValue.toLong(),
											)
										}
									},
									enabled = longPressDurationTempValue > 300,
								) {
									Icon(
										imageVector = Icons.Default.Remove,
										contentDescription = null,
									)
								}
								Slider(
									modifier = Modifier.weight(1f),
									value = longPressDurationTempValue,
									onValueChange = {
										VibratorHelper(context = context).tick()
										longPressDurationTempValue = it
									},
									valueRange = 300f..2000f,
									steps = 16,
									interactionSource = interactionSource,
									onValueChangeFinished = {
										scope.launch {
											mainViewModel.setLongPressDuration(
												longPressDurationTempValue.toLong(),
											)
										}
									},
									thumb = {
										Box(
											modifier =
												Modifier
													.width(12.dp)
													.height(24.dp)
													.offset(x = 6.dp),
											contentAlignment = Alignment.Center,
										) {
											Surface(
												modifier =
													Modifier
														.fillMaxSize()
														.clip(MaterialTheme.shapes.small),
												tonalElevation = 12.dp,
											) { }

											Box(
												modifier =
													Modifier
														.fillMaxWidth(
															animateFloatAsState(
																targetValue = if (isPressed) 0.1f else 0.3f,
																label = "thumbScale",
															).value,
														)
														.fillMaxHeight()
														.clip(CircleShape)
														.background(MaterialTheme.colorScheme.primary),
											)
										}
									},
									track = {
										val rangeSpan = 2000f - 300f
										val adjustedTempValue = longPressDurationTempValue - 300f
										val selectedFraction = adjustedTempValue / rangeSpan

										Row(
											modifier =
												Modifier
													.fillMaxWidth()
													.height(12.dp),
											verticalAlignment = Alignment.CenterVertically,
											horizontalArrangement = Arrangement.spacedBy(4.dp),
										) {
											Box(
												modifier =
													Modifier
														.fillMaxWidth(selectedFraction)
														.fillMaxHeight()
														.clip(CircleShape)
														.background(MaterialTheme.colorScheme.primary),
											)
											Box(
												modifier =
													Modifier
														.weight(1f)
														.fillMaxHeight()
														.clip(CircleShape)
														.background(
															MaterialTheme.colorScheme.primary.copy(
																0.3f,
															),
														),
											)
										}
									},
								)
								IconButton(
									onClick = {
										longPressDurationTempValue += 100
										VibratorHelper(context = context).tick()
										scope.launch {
											mainViewModel.setLongPressDuration(
												longPressDurationTempValue.toLong(),
											)
										}
									},
									enabled = longPressDurationTempValue < 2000,
								) {
									Icon(
										imageVector = Icons.Default.Add,
										contentDescription = null,
									)
								}
							}
						},
					)
				}
				item {
					SettingsItemCustomBottom(
						title = "Auto play",
						subtitle = "Automatically resume your selected favorite media player music when you connect your headphones.",
						icon = Icons.Default.PlayCircleOutline,
						trailing = {
							Switch(
								checked = uiState.customizationSettings.autoPlay,
								onCheckedChange = {
									scope.launch {
										mainViewModel.setAutoPlay(it)
									}
								},
							)
						},
						onClick = {
							scope.launch {
								mainViewModel.setAutoPlay(!uiState.customizationSettings.autoPlay)
							}
						},
						content = {
							Column(
								modifier = Modifier.fillMaxWidth(),
								verticalArrangement = Arrangement.spacedBy(8.dp),
							) {
								Row(
									modifier = Modifier.fillMaxWidth(),
									verticalAlignment = Alignment.CenterVertically,
									horizontalArrangement = Arrangement.spacedBy(4.dp),
								) {
									val settingsList = AutoPlayMode.entries

									settingsList.forEachIndexed { index, item ->

										val backgroundColor by animateColorAsState(
											targetValue =
												if (uiState.customizationSettings.autoPlayMode.ordinal == index) {
													MaterialTheme.colorScheme.primary.copy(0.3f)
												} else {
													MaterialTheme.colorScheme.onSurface.copy(0.05f)
												},
											label = "backgroundColor",
										)

										val borderColor by animateColorAsState(
											targetValue =
												if (uiState.customizationSettings.autoPlayMode.ordinal == index) {
													MaterialTheme.colorScheme.primary
												} else {
													MaterialTheme.colorScheme.onSurface.copy(0.1f)
												},
											label = "borderColor",
										)

										val cornerShape by animateFloatAsState(
											targetValue =
												if (uiState.customizationSettings.autoPlayMode.ordinal == index) {
													16f
												} else {
													12f
												},
											label = "cornerShape",
										)

										Box(
											modifier =
												Modifier
													.weight(1f)
													.clip(RoundedCornerShape(cornerShape.dp))
													.aspectRatio(1f)
													.background(backgroundColor)
													.border(
														width = 1.dp,
														color = borderColor,
														shape = RoundedCornerShape(cornerShape.dp),
													)
													.clickable {
														VibratorHelper(context = context).click()
														scope.launch {
															mainViewModel.setAutoPlayMode(item)
														}
													}
													.padding(8.dp),
										) {
											item.selectedComposable(
												this,
												uiState.customizationSettings.autoPlayMode == item,
											)
											Text(
												text = item.title,
												style = MaterialTheme.typography.bodyMedium,
												modifier =
													Modifier
														.padding(8.dp)
														.align(Alignment.BottomCenter),
											)
										}
									}
								}
								Button(
									modifier = Modifier.fillMaxWidth(),
									onClick = navigateToSettings,
								) {
									Row(
										verticalAlignment = Alignment.CenterVertically,
										horizontalArrangement = Arrangement.spacedBy(8.dp),
									) {
										Icon(
											imageVector = Icons.Default.Tune,
											contentDescription = null,
										)
										Text(
											text = "Set preferred media player",
											style = MaterialTheme.typography.bodyMedium,
										)
									}
								}
							}
						},
					)
				}
			}
		}
	}
}
