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

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.R
import fr.angel.soundtap.animations.WOBBLE_SHADER
import fr.angel.soundtap.ui.components.SongRow

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HistoryScreen(
	modifier: Modifier = Modifier,
	mainViewModel: MainViewModel,
	animatedVisibilityScope: AnimatedVisibilityScope,
) {
	val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

	val songsList =
		remember(uiState.statsSettings.history) {
			uiState.statsSettings.history.toList().reversed()
		}
	val isHistoryEmpty = songsList.isEmpty()

	val totalSongsPlayed = uiState.statsSettings.totalSongsPlayed
	val totalSongsSkipped = uiState.statsSettings.totalSongsSkipped

	Card(
		modifier =
			modifier
				.padding(8.dp)
				.fillMaxSize()
				.sharedElement(
					state =
						rememberSharedContentState(
							key = "History-card",
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
										key = "History-icon",
									),
								animatedVisibilityScope = animatedVisibilityScope,
							),
					imageVector = Icons.Default.History,
					contentDescription = null,
				)

				Text(
					text = stringResource(id = R.string.history_title),
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					modifier =
						Modifier
							.sharedBounds(
								rememberSharedContentState(
									key = "History",
								),
								animatedVisibilityScope = animatedVisibilityScope,
							),
				)
			}
			HorizontalDivider()
			if (isHistoryEmpty) {
				val time by produceState(0f) {
					while (true) {
						withInfiniteAnimationFrameMillis {
							value = it / 1000f
						}
					}
				}

				val shaderModifier =
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
						val shader = remember { RuntimeShader(WOBBLE_SHADER) }

						Modifier
							.onSizeChanged { size ->
								shader.setFloatUniform(
									"resolution",
									size.width.toFloat(),
									size.height.toFloat(),
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

				Column(
					modifier =
						Modifier
							.fillMaxSize()
							.skipToLookaheadSize(),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					val composition by rememberLottieComposition(
						LottieCompositionSpec.RawRes(R.raw.empty),
					)

					LottieAnimation(
						composition = composition,
						iterations = LottieConstants.IterateForever,
						renderMode = RenderMode.HARDWARE,
						modifier =
							Modifier
								.fillMaxWidth(0.8f)
								.align(Alignment.CenterHorizontally)
								.then(shaderModifier),
					)
					Text(
						text = stringResource(id = R.string.history_no_history),
						modifier = Modifier.align(Alignment.CenterHorizontally),
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.SemiBold,
					)
					Text(
						text = stringResource(id = R.string.history_your_history_and_stats_will_be_displayed_here),
						modifier = Modifier.align(Alignment.CenterHorizontally),
						style = MaterialTheme.typography.bodyMedium,
					)
				}
			} else {
				LazyColumn(
					modifier = Modifier.skipToLookaheadSize(),
					verticalArrangement = Arrangement.spacedBy(4.dp),
					contentPadding = PaddingValues(4.dp),
				) {
					item {
						Row(
							modifier =
								Modifier
									.fillMaxWidth()
									.padding(vertical = 16.dp),
							horizontalArrangement = Arrangement.spacedBy(16.dp),
						) {
							Column(
								modifier = Modifier.weight(1f),
								verticalArrangement = Arrangement.spacedBy(8.dp),
								horizontalAlignment = Alignment.CenterHorizontally,
							) {
								Text(
									text = stringResource(id = R.string.history_total_songs_played),
									style = MaterialTheme.typography.labelSmall,
								)
								Text(
									text = totalSongsPlayed.toString(),
									style = MaterialTheme.typography.labelLarge,
									fontWeight = FontWeight.Black,
								)
							}
							Column(
								modifier = Modifier.weight(1f),
								verticalArrangement = Arrangement.spacedBy(8.dp),
								horizontalAlignment = Alignment.CenterHorizontally,
							) {
								Text(
									text = stringResource(id = R.string.history_total_songs_skipped),
									style = MaterialTheme.typography.labelSmall,
								)
								Text(
									text = totalSongsSkipped.toString(),
									style = MaterialTheme.typography.labelLarge,
									fontWeight = FontWeight.Black,
								)
							}
						}
					}
					items(songsList, key = { it.addedTime }) { item ->
						SongRow(
							modifier = Modifier.fillMaxWidth(),
							song = item,
						)
					}
				}
			}
		}
	}
}
