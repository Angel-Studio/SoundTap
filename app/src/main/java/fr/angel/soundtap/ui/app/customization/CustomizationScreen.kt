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
package fr.angel.soundtap.ui.app.customization

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.R
import fr.angel.soundtap.navigation.Screens

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CustomizationScreen(
	modifier: Modifier = Modifier,
	mainViewModel: MainViewModel,
	animatedVisibilityScope: AnimatedVisibilityScope,
	navigateToSettings: () -> Unit,
) {
	val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

	val navController = rememberNavController()
	val currentBackStack by navController.currentBackStackEntryAsState()
	val currentDestination = currentBackStack?.destination
	val currentScreen: Screens =
		Screens.fromRoute(currentDestination?.route ?: uiState.defaultScreen.route)

	LaunchedEffect(currentScreen) {
		if (currentScreen != Screens.App.Customization.Home) {
			mainViewModel.setFocusedNavController(navController)
		} else {
			mainViewModel.resetFocusedNavController()
		}
	}

	DisposableEffect(Unit) {
		onDispose {
			mainViewModel.resetFocusedNavController()
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
					text = stringResource(id = R.string.customization_title),
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
			NavHost(
				navController = navController,
				startDestination = Screens.App.Customization.Home.route,
				modifier =
					Modifier
						.fillMaxSize()
						.skipToLookaheadSize(),
			) {
				composable(
					route = Screens.App.Customization.Home.route,
					popEnterTransition = { slideInHorizontally(tween(250)) },
					exitTransition = { fadeOut(tween(250)) },
				) {
					CustomizationHome(
						modifier = Modifier.fillMaxSize(),
						mainViewModel = mainViewModel,
						navigateToSettings = navigateToSettings,
						navigateToControls = { navController.navigate(Screens.App.Customization.Controls.route) },
					)
				}

				composable(
					route = Screens.App.Customization.Controls.route,
					enterTransition = { slideInHorizontally(tween(250)) { it } },
					popExitTransition = {
						fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { it }
					},
				) {
					CustomizationControls(
						modifier = Modifier.fillMaxSize(),
						mainViewModel = mainViewModel,
					)
				}
			}
		}
	}
}
