package fr.angel.soundtap.data

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fr.angel.soundtap.data.enums.HapticFeedback
import fr.angel.soundtap.data.enums.WorkingMode
import fr.angel.soundtap.data.models.Song
import fr.angel.soundtap.data.models.toSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val DATA_STORE = "storeData"

class DataStore @Inject constructor(
	private val application: Application,
) {
	constructor(application: Context) : this(application as Application)

	companion object {
		private val Context.dataStore by preferencesDataStore(DATA_STORE)

		/* KEYS */
		val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

		/* PREFERENCES */
		val HAPTIC_FEEDBACK = intPreferencesKey("haptic_feedback")
		val WORKING_MODE = intPreferencesKey("working_mode")
		val LONG_PRESS_DURATION = longPreferencesKey("long_press_duration")
		val DOUBLE_PRESS_DURATION = longPreferencesKey("double_press_duration")

		/* SETTINGS */
		val REMOVED_SUPPORTED_MEDIA_PLAYERS = stringSetPreferencesKey("supported_media_players")
		val PREFERRED_MEDIA_PLAYER = stringPreferencesKey("preferred_media_player")

		/* ANALYTICS */
		val HISTORY = stringSetPreferencesKey("history")
		val TOTAL_SONGS_PLAYED = intPreferencesKey("total_songs_played")
		val TOTAL_SONGS_SKIPPED = intPreferencesKey("total_songs_skipped")
	}

	suspend fun clearHistory() {
		application.dataStore.edit { preferences ->
			preferences.remove(HISTORY)
		}
	}

	suspend fun addToHistory(value: Song) {
		application.dataStore.edit { preferences ->
			val history = preferences[HISTORY] ?: emptySet()

			// Check if the song is already the last one in the history
			if (history.isNotEmpty() && history.last().toSong() == value) return@edit
			if (value.isPartial()) return@edit

			val newHistory = history.toMutableSet().apply {
				add(value.toString())
			}
			preferences[HISTORY] = newHistory
		}
	}

	val history: Flow<Set<Song>> = application.dataStore.data.map { preferences ->
		preferences[HISTORY]?.map { it.toSong() }?.toSet() ?: emptySet()
	}

	suspend fun setHapticFeedback(value: HapticFeedback) {
		application.dataStore.edit { preferences ->
			preferences[HAPTIC_FEEDBACK] = value.ordinal
		}
	}

	val hapticFeedback: Flow<HapticFeedback> = application.dataStore.data.map { preferences ->
		HapticFeedback.entries[preferences[HAPTIC_FEEDBACK] ?: HapticFeedback.MEDIUM.ordinal]
	}

	suspend fun setWorkingMode(value: WorkingMode) {
		application.dataStore.edit { preferences ->
			preferences[WORKING_MODE] = value.ordinal
		}
	}

	val workingMode: Flow<WorkingMode> = application.dataStore.data.map { preferences ->
		WorkingMode.entries[preferences[WORKING_MODE] ?: WorkingMode.SCREEN_ON_OFF.ordinal]
	}

	suspend fun setLongPressDuration(value: Long) {
		application.dataStore.edit { preferences ->
			preferences[LONG_PRESS_DURATION] = value.coerceIn(300, 2000)
		}
	}

	val longPressDuration: Flow<Long> = application.dataStore.data.map { preferences ->
		preferences[LONG_PRESS_DURATION] ?: 500
	}

	val doublePressDuration: Flow<Long> = application.dataStore.data.map { preferences ->
		preferences[DOUBLE_PRESS_DURATION] ?: 500
	}

	val unsupportedMediaPlayers: Flow<Set<String>> = application.dataStore.data.map { preferences ->
		preferences[REMOVED_SUPPORTED_MEDIA_PLAYERS] ?: emptySet()
	}

	suspend fun toggleUnsupportedMediaPlayer(value: String) {
		application.dataStore.edit { preferences ->
			val supportedMediaPlayers = preferences[REMOVED_SUPPORTED_MEDIA_PLAYERS] ?: emptySet()

			val newSupportedMediaPlayers = supportedMediaPlayers.toMutableSet().apply {
				if (contains(value)) {
					remove(value)
				} else {
					add(value)
				}
			}
			preferences[REMOVED_SUPPORTED_MEDIA_PLAYERS] = newSupportedMediaPlayers
		}
	}

	suspend fun setOnboardingCompleted() {
		application.dataStore.edit { preferences ->
			preferences[ONBOARDING_COMPLETED] = true
		}
	}

	val onboardingCompleted: Flow<Boolean> = application.dataStore.data.map { preferences ->
		preferences[ONBOARDING_COMPLETED] ?: false
	}

	suspend fun incrementTotalSongsPlayed() {
		application.dataStore.edit { preferences ->
			val totalSongsPlayed = preferences[TOTAL_SONGS_PLAYED] ?: 0
			preferences[TOTAL_SONGS_PLAYED] = totalSongsPlayed + 1
		}
	}

	suspend fun incrementTotalSongsSkipped() {
		application.dataStore.edit { preferences ->
			val totalSongsSkipped = preferences[TOTAL_SONGS_SKIPPED] ?: 0
			preferences[TOTAL_SONGS_SKIPPED] = totalSongsSkipped + 1
		}
	}

	val totalSongsPlayed: Flow<Int> = application.dataStore.data.map { preferences ->
		preferences[TOTAL_SONGS_PLAYED] ?: 0
	}

	val totalSongsSkipped: Flow<Int> = application.dataStore.data.map { preferences ->
		preferences[TOTAL_SONGS_SKIPPED] ?: 0
	}

	suspend fun setPreferredMediaPlayer(value: String?) {
		application.dataStore.edit { preferences ->
			value?.let {
				preferences[PREFERRED_MEDIA_PLAYER] = it
			} ?: run {
				preferences.remove(PREFERRED_MEDIA_PLAYER)
			}
		}
	}

	val preferredMediaPlayer: Flow<String?> = application.dataStore.data.map { preferences ->
		preferences[PREFERRED_MEDIA_PLAYER]
	}
}