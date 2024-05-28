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
package fr.angel.soundtap.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.ui.OnboardingScreen
import fr.angel.soundtap.ui.app.App
import fr.angel.soundtap.ui.app.CustomizationScreen
import fr.angel.soundtap.ui.app.HistoryScreen
import fr.angel.soundtap.ui.app.SettingsScreen
import fr.angel.soundtap.ui.app.SupportScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SoundTapNavGraph(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel<MainViewModel>(),
    navController: NavHostController,
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    SharedTransitionLayout(
        modifier = Modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = uiState.defaultScreen.route,
            modifier = Modifier,
        ) {
            navigation(
                route = Screens.App.route,
                startDestination = Screens.App.Home.route,
            ) {
                composable(Screens.App.Home.route) {
                    App(
                        modifier = modifier,
                        mainViewModel = mainViewModel,
                        animatedVisibilityScope = this,
                        navigateToCustomization = { navController.navigate(Screens.App.Customization.route) },
                        navigateToHistory = { navController.navigate(Screens.App.History.route) },
                        navigateToSettings = { navController.navigate(Screens.App.Settings.route) },
                        navigateToSupport = { navController.navigate(Screens.App.Support.route) },
                    )
                }
                composable(Screens.App.Customization.route) {
                    CustomizationScreen(
                        modifier = modifier,
                        mainViewModel = mainViewModel,
                        animatedVisibilityScope = this,
                        navigateToSettings = { navController.navigate(Screens.App.Settings.route) },
                    )
                }
                composable(Screens.App.History.route) {
                    HistoryScreen(
                        modifier = modifier,
                        mainViewModel = mainViewModel,
                        animatedVisibilityScope = this,
                    )
                }
                composable(Screens.App.Settings.route) {
                    SettingsScreen(
                        modifier = modifier,
                        mainViewModel = mainViewModel,
                        animatedVisibilityScope = this,
                    )
                }
                composable(Screens.App.Support.route) {
                    SupportScreen(
                        modifier = modifier,
                        animatedVisibilityScope = this,
                    )
                }
            }

            composable(Screens.Onboarding.route) {
                OnboardingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    mainViewModel = mainViewModel,
                )
            }
        }
    }
}
