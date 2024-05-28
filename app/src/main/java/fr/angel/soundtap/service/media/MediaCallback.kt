package fr.angel.soundtap.service.media

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import fr.angel.soundtap.data.StorageHelper.saveBitmapToFile
import fr.angel.soundtap.data.models.Song
import fr.angel.soundtap.data.settings.stats.StatsSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaCallback @Inject constructor(
	private val mediaController: MediaController,
	val onDestroyed: () -> Unit,
	val onToggleSupportedPlayer: (Boolean) -> Unit,
	private val statsDataStore: DataStore<StatsSettings>,
	private val context: Context,
) : MediaController.Callback() {

	private var playbackState: MutableState<PlaybackState?> = mutableStateOf(null)
	var playingSong: Song? by mutableStateOf(null)

	private val scope by lazy { CoroutineScope(Dispatchers.IO) }

	private var debounceCount = 0

	var isPlaying: Boolean
		get() = playbackState.value?.state == PlaybackState.STATE_PLAYING
		set(value) {
			if (value) {
				mediaController.transportControls.play()
			} else {
				mediaController.transportControls.pause()
			}
		}

	init {
		if (mediaController.metadata != null && mediaController.playbackState != null) {

			// Set the initial values for the media controller
			playbackState.value = mediaController.playbackState!!

			updatePlayingSong()
		}
	}

	fun skipToNext() {
		mediaController.transportControls.skipToNext()
		scope.launch { statsDataStore.updateData { it.incrementTotalSongsSkipped() } }
	}

	fun skipToPrevious() {
		mediaController.transportControls.skipToPrevious()
		scope.launch { statsDataStore.updateData { it.incrementTotalSongsSkipped() } }
	}

	fun togglePlayPause() = if (isPlaying) {
		mediaController.transportControls.pause()
	} else {
		mediaController.transportControls.play()
	}

	override fun onPlaybackStateChanged(state: PlaybackState?) {
		super.onPlaybackStateChanged(state)
		playbackState.value = state ?: return
	}

	override fun onMetadataChanged(metadata: MediaMetadata?) {
		super.onMetadataChanged(metadata)

		updatePlayingSong()
	}

	override fun onSessionDestroyed() {
		super.onSessionDestroyed()
		onDestroyed()
	}

	private fun updatePlayingSong() {

		mediaController.metadata?.let { metadata ->
			val bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART) ?: run {
				Log.w("MediaCallback", "No album art found")
				return
			}
			val filename = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) +
					"_${metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)}" +
					"_${metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)}" +
					"_${bitmap.hashCode()}" +
					"_cover.png"
			val coverFilePath = saveBitmapToFile(context, bitmap, filename)

			Song(
				id = bitmap.hashCode(),
				title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: return,
				artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: return,
				album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: return,
				duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION),
				coverFilePath = coverFilePath ?: return,
			).run {
				val duplicate = playingSong != null &&
						(addedTime - (playingSong?.addedTime ?: 0)) > 250

				if (
					this == playingSong
					|| this.isPartial()
					|| duplicate
					&& debounceCount == 0
				) {
					debounceCount = 1
					return
				}

				debounceCount = 0

				scope.launch {
					statsDataStore.updateData { settings ->
						settings.addSongToHistory(
							this@run
						)
					}
				}
				scope.launch { statsDataStore.updateData { settings -> settings.incrementTotalSongsPlayed() } }

				playingSong = this
			}
		}
	}

	fun onUnsupportedPlayersChanged(unsupportedPlayers: Set<String>) {
		if (mediaController.packageName in unsupportedPlayers) {
			onToggleSupportedPlayer(false)
		} else {
			onToggleSupportedPlayer(true)
		}
	}
}
