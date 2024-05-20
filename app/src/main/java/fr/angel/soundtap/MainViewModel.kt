package fr.angel.soundtap

import android.content.Context
import android.content.pm.ResolveInfo
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.angel.soundtap.data.DataStore
import fr.angel.soundtap.data.enums.AutoPlayMode
import fr.angel.soundtap.data.enums.HapticFeedback
import fr.angel.soundtap.data.enums.WorkingMode
import fr.angel.soundtap.data.models.Song
import fr.angel.soundtap.navigation.Screens
import fr.angel.soundtap.service.SoundTapAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
	val finishedInitializations: Boolean = false,

	val onboardingPageCompleted: Boolean = true,
	val stateText: String = "",
	val hasNotificationListenerPermission: Boolean = false,
	val isBackgroundOptimizationDisabled: Boolean = false,
	val supportedPlayers: Set<String> = emptySet(),
	val playersPackages: Set<ResolveInfo> = emptySet(),
	val hapticFeedback: HapticFeedback = HapticFeedback.MEDIUM,
	val workingMode: WorkingMode = WorkingMode.SCREEN_ON_OFF,
	val longPressDuration: Long = 400L,
	val history: Set<Song> = emptySet(),
	val totalSongsPlayed: Int = 0,
	val totalSongsSkipped: Int = 0,
	val preferredMediaPlayer: String = "",
	val autoPlay: Boolean = false,
	val autoPlayMode: AutoPlayMode = AutoPlayMode.ON_HEADSET_CONNECTED,
) {
	val defaultScreen: Screens
		get() = if (onboardingPageCompleted) Screens.App else Screens.Onboarding
}

@HiltViewModel
class MainViewModel @Inject constructor(
	packageQueryHelper: PackageQueryHelper,
	private val dataStore: DataStore,
) : ViewModel() {

	private val scope by lazy { CoroutineScope(Dispatchers.Main) }
	private val _uiState = MutableStateFlow(MainUiState())
	val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

	init {
		scope.launch {
			dataStore.onboardingCompleted.collect { state ->
				_uiState.value = _uiState.value.copy(onboardingPageCompleted = state)

				delay(250)
				_uiState.value = _uiState.value.copy(finishedInitializations = true)
			}
		}
		scope.launch {
			dataStore.unsupportedMediaPlayers.collect { state ->
				_uiState.value = _uiState.value.copy(supportedPlayers = state)
			}
		}
		scope.launch {
			dataStore.hapticFeedback.collect { state ->
				_uiState.value = _uiState.value.copy(hapticFeedback = state)
			}
		}
		scope.launch {
			dataStore.workingMode.collect { state ->
				_uiState.value = _uiState.value.copy(workingMode = state)
			}
		}
		scope.launch {
			dataStore.longPressDuration.collect { state ->
				_uiState.value = _uiState.value.copy(longPressDuration = state)
			}
		}
		scope.launch {
			dataStore.history.collect { state ->
				_uiState.value = _uiState.value.copy(history = state)
			}
		}
		scope.launch {
			dataStore.totalSongsPlayed.collect { state ->
				_uiState.value = _uiState.value.copy(totalSongsPlayed = state)
			}
		}
		scope.launch {
			dataStore.totalSongsSkipped.collect { state ->
				_uiState.value = _uiState.value.copy(totalSongsSkipped = state)
			}
		}
		scope.launch {
			dataStore.preferredMediaPlayer.collect { state ->
				if (state.isNullOrBlank() || state !in supportedStartMediaPlayerPackages) {
					setPreferredMediaPlayer(
						_uiState.value.playersPackages
							.firstOrNull { supportedStartMediaPlayerPackages.contains(it.activityInfo.packageName) }
							?.activityInfo?.packageName
					)
					return@collect
				}
				_uiState.value = _uiState.value.copy(preferredMediaPlayer = state)
			}
		}
		scope.launch {
			dataStore.autoPlayEnabled.collect { state ->
				_uiState.value = _uiState.value.copy(autoPlay = state)
			}
		}
		scope.launch {
			dataStore.autoPlayMode.collect { state ->
				_uiState.value = _uiState.value.copy(autoPlayMode = state)
			}
		}

		_uiState.value = _uiState.value.copy(
			playersPackages = packageQueryHelper.getMediaPlayersInstalled()
		)
	}

	fun updatePermissionStates(context: Context) {
		val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
		_uiState.value = _uiState.value.copy(
			hasNotificationListenerPermission = GlobalHelper.hasNotificationListenerPermission(
				context
			),
			isBackgroundOptimizationDisabled = powerManager.isIgnoringBatteryOptimizations(context.packageName)
		)
	}

	fun onToggleService() {
		SoundTapAccessibilityService.toggleService()
	}

	fun setHapticFeedback(item: HapticFeedback) {
		scope.launch {
			dataStore.setHapticFeedback(item)
		}
	}

	fun setLongPressDuration(duration: Long) {
		scope.launch {
			dataStore.setLongPressDuration(duration)
		}
	}

	fun setWorkingMode(mode: WorkingMode) {
		scope.launch {
			dataStore.setWorkingMode(mode)
		}
	}

	fun toggleUnsupportedMediaPlayer(packageName: String) {
		scope.launch {
			dataStore.toggleUnsupportedMediaPlayer(packageName)
		}
	}

	fun onboardingCompleted() {
		scope.launch {
			dataStore.setOnboardingCompleted()
		}
	}

	fun setPreferredMediaPlayer(packageName: String?) {
		scope.launch {
			dataStore.setPreferredMediaPlayer(packageName)
		}
	}

	fun setAutoPlay(autoPlay: Boolean) {
		scope.launch {
			dataStore.setAutoPlayEnabled(autoPlay)
		}
	}

	fun setAutoPlayMode(autoPlayMode: AutoPlayMode) {
		scope.launch {
			dataStore.setAutoPlayMode(autoPlayMode)
		}
	}
}