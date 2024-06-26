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
package fr.angel.soundtap

import android.content.Context
import android.content.pm.ResolveInfo
import android.os.PowerManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.angel.soundtap.data.enums.AutoPlayMode
import fr.angel.soundtap.data.enums.HapticFeedbackLevel
import fr.angel.soundtap.data.enums.WorkingMode
import fr.angel.soundtap.data.models.BottomSheetState
import fr.angel.soundtap.data.settings.customization.ControlMediaAction
import fr.angel.soundtap.data.settings.customization.CustomControlMediaAction
import fr.angel.soundtap.data.settings.customization.CustomizationSettings
import fr.angel.soundtap.data.settings.customization.MediaAction
import fr.angel.soundtap.data.settings.settings.AppSettings
import fr.angel.soundtap.data.settings.stats.StatsSettings
import fr.angel.soundtap.navigation.Screens
import fr.angel.soundtap.service.SleepTimerService
import fr.angel.soundtap.service.SoundTapAccessibilityService
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
	val finishedInitializations: Boolean = false,
	val hasNotificationListenerPermission: Boolean = false,
	val isBackgroundOptimizationDisabled: Boolean = false,
	val playersPackages: Set<ResolveInfo> = emptySet(),
	val bottomSheetState: BottomSheetState = BottomSheetState.None,
	val bottomSheetVisible: Boolean = false,
	val customizationSettings: CustomizationSettings = CustomizationSettings(),
	val appSettings: AppSettings = AppSettings(),
	val statsSettings: StatsSettings = StatsSettings(),
	val defaultNavController: NavHostController? = null,
	val focusedNavController: NavHostController? = null,
) {
	val currentNavController: NavHostController
		get() = focusedNavController ?: defaultNavController!!
	val defaultScreen: Screens
		get() = if (appSettings.onboardingPageCompleted) Screens.App else Screens.Onboarding
}

@HiltViewModel
class MainViewModel
	@Inject
	constructor(
		@ApplicationContext private val context: Context,
		private val customizationSettingsDataStore: DataStore<CustomizationSettings>,
		private val appSettingsDataStore: DataStore<AppSettings>,
		private val statsSettingsDataStore: DataStore<StatsSettings>,
		packageQueryHelper: PackageQueryHelper,
	) : ViewModel() {
		private val _uiState = MutableStateFlow(MainUiState())
		val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

		@OptIn(ExperimentalMaterial3Api::class)
		private lateinit var sheetState: SheetState

		init {
			// Load the customization settings
			viewModelScope.launch {
				customizationSettingsDataStore.data.collect { settings ->
					_uiState.value = _uiState.value.copy(customizationSettings = settings)
				}
			}

			// Load the app settings
			viewModelScope.launch {
				appSettingsDataStore.data.collect { settings ->
					_uiState.value = _uiState.value.copy(appSettings = settings)
				}
			}

			// Load the stats settings
			viewModelScope.launch {
				statsSettingsDataStore.data.collect { settings ->
					_uiState.value = _uiState.value.copy(statsSettings = settings)
				}
			}

			// Load the supported players
			_uiState.value =
				_uiState.value.copy(playersPackages = packageQueryHelper.getMediaPlayersInstalled())

			// Remove the splash screen
			viewModelScope.launch {
				delay(500)
				_uiState.value = _uiState.value.copy(finishedInitializations = true)
			}
		}

		fun updatePermissionStates(context: Context) {
			val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
			_uiState.value =
				_uiState.value.copy(
					hasNotificationListenerPermission =
						GlobalHelper.hasNotificationListenerPermission(
							context,
						),
					isBackgroundOptimizationDisabled =
						powerManager.isIgnoringBatteryOptimizations(
							context.packageName,
						),
				)
		}

		fun onToggleService() {
			SoundTapAccessibilityService.toggleService()
		}

		fun setHapticFeedback(item: HapticFeedbackLevel) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings ->
					settings.copy(
						hapticFeedbackLevel = item,
					)
				}
			}
		}

		fun setLongPressDuration(duration: Long) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings -> settings.copy(longPressThreshold = duration) }
			}
		}

		fun setWorkingMode(mode: WorkingMode) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings -> settings.copy(workingMode = mode) }
			}
		}

		fun toggleUnsupportedMediaPlayer(packageName: String) {
			viewModelScope.launch {
				appSettingsDataStore.updateData { settings ->
					val newSet = settings.unsupportedMediaPlayers.toMutableSet()
					if (newSet.contains(packageName)) {
						newSet.remove(packageName)
					} else {
						newSet.add(packageName)
					}
					settings.copy(unsupportedMediaPlayers = newSet)
				}
			}
		}

		fun onboardingCompleted() {
			viewModelScope.launch {
				appSettingsDataStore.updateData { settings -> settings.copy(onboardingPageCompleted = true) }
			}
		}

		fun setPreferredMediaPlayer(packageName: String?) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings ->
					settings.copy(
						preferredMediaPlayer = packageName,
					)
				}
			}
		}

		fun setAutoPlay(autoPlay: Boolean) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings -> settings.copy(autoPlay = autoPlay) }
			}
		}

		fun setAutoPlayMode(autoPlayMode: AutoPlayMode) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings -> settings.copy(autoPlayMode = autoPlayMode) }
			}
		}

		fun showBottomSheet(bottomSheetState: BottomSheetState) {
			_uiState.value = _uiState.value.copy(bottomSheetState = bottomSheetState, bottomSheetVisible = true)
		}

		@OptIn(ExperimentalMaterial3Api::class)
		suspend fun hideBottomSheet() {
			sheetState.hide()
			_uiState.value = _uiState.value.copy(bottomSheetVisible = false, bottomSheetState = BottomSheetState.None)
		}

		fun setSleepTimer(duration: Long) {
			// Start the sleep timer service
			SleepTimerService.startService(context, duration)
		}

		@OptIn(ExperimentalMaterial3Api::class)
		fun setBottomSheetState(sheetState: SheetState) {
			this.sheetState = sheetState
		}

		fun setDefaultNavController(navController: NavHostController) {
			_uiState.value = _uiState.value.copy(defaultNavController = navController)
		}

		fun setFocusedNavController(navController: NavHostController) {
			_uiState.value = _uiState.value.copy(focusedNavController = navController)
		}

		fun resetFocusedNavController() {
			_uiState.value = _uiState.value.copy(focusedNavController = null)
		}

		fun toggleControlMediaAction(action: ControlMediaAction) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings ->
					val newAction = action.copy(enabled = !action.enabled)
					when (action.id) {
						0 -> settings.copy(longVolumeUpPressControlMediaAction = newAction)
						1 -> settings.copy(longVolumeDownPressControlMediaAction = newAction)
						2 -> settings.copy(doubleVolumeLongPressControlMediaAction = newAction)
						else -> settings
					}
				}
			}
		}

		fun changeControlMediaAction(controlMediaAction: ControlMediaAction) {
			showBottomSheet(
				BottomSheetState.EditControlMediaAction(
					displayName = "Edit ${controlMediaAction.title} action",
					onSetAction = { newAction ->
						viewModelScope.launch {
							customizationSettingsDataStore.updateData { settings ->
								val newControlMediaAction = controlMediaAction.copy(action = newAction)
								when (controlMediaAction.id) {
									0 -> settings.copy(longVolumeUpPressControlMediaAction = newControlMediaAction)
									1 -> settings.copy(longVolumeDownPressControlMediaAction = newControlMediaAction)
									2 -> settings.copy(doubleVolumeLongPressControlMediaAction = newControlMediaAction)
									else -> settings
								}
							}
						}
					},
				),
			)
		}

		fun toggleControlMediaAction(action: CustomControlMediaAction) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings ->
					val newAction = action.copy(enabled = !action.enabled)
					settings.copy(
						customMediaActions =
							settings.customMediaActions.map {
								if (it.id == action.id) newAction else it
							},
					)
				}
			}
		}

		fun changeControlMediaAction(controlMediaAction: CustomControlMediaAction) {
			showBottomSheet(
				BottomSheetState.EditControlMediaAction(
					displayName = "Edit custom control action",
					onSetAction = { newAction ->
						viewModelScope.launch {
							customizationSettingsDataStore.updateData { settings ->
								val newControlMediaAction = controlMediaAction.copy(action = newAction)
								settings.copy(
									customMediaActions =
										settings.customMediaActions.map {
											if (it.id == controlMediaAction.id) newControlMediaAction else it
										},
								)
							}
						}
					},
				),
			)
		}

		fun addCustomControlMediaAction() {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings ->
					val newAction =
						CustomControlMediaAction(
							id = settings.customMediaActions.size + 100 * Random.nextInt(),
							action = MediaAction.NEXT,
							enabled = false,
						)
					settings.copy(
						customMediaActions =
							settings.customMediaActions.toMutableList().apply {
								add(newAction)
							},
					)
				}
			}
		}

		fun removeCustomControlMediaAction(action: CustomControlMediaAction) {
			viewModelScope.launch {
				customizationSettingsDataStore.updateData { settings ->
					settings.copy(
						customMediaActions = settings.customMediaActions.filter { it.id != action.id },
					)
				}
			}
		}

		fun editCustomControlMediaActionSequence(controlMediaAction: CustomControlMediaAction) {
			showBottomSheet(
				BottomSheetState.EditControlMediaActionSequence(
					displayName = "Edit custom control action sequence",
					customControlMediaAction = controlMediaAction,
					onSetSequence = { newSequence ->
						viewModelScope.launch {
							customizationSettingsDataStore.updateData { settings ->
								val newControlMediaAction = controlMediaAction.copy(eventsSequenceList = newSequence)
								settings.copy(
									customMediaActions =
										settings.customMediaActions.map {
											if (it.id == controlMediaAction.id) newControlMediaAction else it
										},
								)
							}
						}
					},
				),
			)
		}
	}
