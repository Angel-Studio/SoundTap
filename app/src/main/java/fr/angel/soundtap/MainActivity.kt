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
package fr.angel.soundtap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.angel.soundtap.navigation.Screens
import fr.angel.soundtap.navigation.SoundTapNavGraph
import fr.angel.soundtap.service.SoundTapAccessibilityService
import fr.angel.soundtap.ui.components.BottomControlBar
import fr.angel.soundtap.ui.theme.FontPilowlava
import fr.angel.soundtap.ui.theme.SoundTapTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	private lateinit var mainViewModel: MainViewModel
	private var shouldKeepSplashScreenOn = mutableStateOf(true)

	@OptIn(ExperimentalMaterial3Api::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		installSplashScreen()
			.setKeepOnScreenCondition { shouldKeepSplashScreenOn.value }

		setContent {
			val scope = rememberCoroutineScope()

			mainViewModel = hiltViewModel<MainViewModel>()
			val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

			LaunchedEffect(key1 = Unit) {
				mainViewModel.updatePermissionStates(this@MainActivity)
			}

			val navController = rememberNavController()
			val currentBackStack by navController.currentBackStackEntryAsState()
			val currentDestination = currentBackStack?.destination
			val currentScreen: Screens =
				Screens.fromRoute(currentDestination?.route ?: uiState.defaultScreen.route)

			val serviceUiState by SoundTapAccessibilityService.uiState.collectAsState()

			LaunchedEffect(key1 = uiState.finishedInitializations) {
				shouldKeepSplashScreenOn.value = !uiState.finishedInitializations
			}

			LaunchedEffect(key1 = Unit) {
				mainViewModel.updatePermissionStates(this@MainActivity)
			}

			val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

			val bottomSheetState =
				rememberModalBottomSheetState(
					skipPartiallyExpanded =
						when (uiState.bottomSheetState) {
							else -> false // Prevents the sheet from being partially expanded
						},
					confirmValueChange = {
						when (uiState.bottomSheetState) {
							/**
							 * Prevents the sheet from being expanded
							 *
							 is BottomSheetManager.Companion.BottomSheetType.CreateList -> { it != SheetValue.Expanded } // Prevents the sheet from being expanded*/
							else -> true // Allows the sheet to be expanded
						}
					},
				).also { mainViewModel.setBottomSheetState(it) }

			SoundTapTheme {
				Scaffold(
					modifier =
						Modifier
							.fillMaxSize()
							.nestedScroll(scrollBehavior.nestedScrollConnection),
					topBar = {
						CenterAlignedTopAppBar(
							navigationIcon = {
								AnimatedVisibility(
									visible = currentScreen.showBackArrow,
									enter = scaleIn(),
									exit = scaleOut(),
								) {
									IconButton(onClick = { navController.popBackStack() }) {
										Icon(
											imageVector = Icons.AutoMirrored.Filled.ArrowBack,
											contentDescription = "Back",
										)
									}
								}
							},
							title = {
								Text(
									modifier =
										Modifier.padding(
											horizontal = 16.dp,
											vertical = 8.dp,
										),
									text = "SoundTap",
									style = MaterialTheme.typography.displayMedium,
									fontFamily = FontPilowlava,
									fontWeight = FontWeight.ExtraBold,
								)
							},
							scrollBehavior = scrollBehavior,
						)
					},
					bottomBar = {
						AnimatedVisibility(
							visible = currentScreen == Screens.App.Home,
							enter = slideInVertically { it } + expandVertically() + fadeIn(),
							exit = slideOutVertically { it } + shrinkVertically() + fadeOut(),
						) {
							BottomControlBar(
								serviceUiState = serviceUiState,
							)
						}
					},
				) { innerPadding ->
					SoundTapNavGraph(
						modifier = Modifier.padding(innerPadding),
						innerPadding = innerPadding,
						navController = navController,
					)

					if (uiState.bottomSheetVisible) {
						val sheetState = uiState.bottomSheetState
						ModalBottomSheet(
							onDismissRequest = {
								sheetState.onDismiss?.invoke()
								scope.launch { mainViewModel.hideBottomSheet() }
							},
							sheetState = bottomSheetState,
							windowInsets = WindowInsets(0),
						) {
							Column(
								modifier =
									Modifier
										.padding(horizontal = 16.dp)
										.fillMaxWidth(),
								horizontalAlignment = Alignment.CenterHorizontally,
							) {
								sheetState.displayName?.run {
									Text(
										text = this,
										style = MaterialTheme.typography.titleLarge,
										fontWeight = FontWeight.SemiBold,
									)
								}
								Spacer(modifier = Modifier.height(24.dp))
								sheetState.content(sheetState)
								Spacer(modifier = Modifier.navigationBarsPadding())
							}
						}
					}
				}
			}
		}
	}

	override fun onResume() {
		if (::mainViewModel.isInitialized) {
			mainViewModel.updatePermissionStates(this)
		}
		super.onResume()
	}
}
