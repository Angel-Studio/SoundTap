package fr.angel.soundtap.service.media

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
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

		Song(
			title = mediaController.metadata!!.getString(MediaMetadata.METADATA_KEY_TITLE)
				?: return,
			artist = mediaController.metadata!!.getString(MediaMetadata.METADATA_KEY_ARTIST)
				?: return,
			album = mediaController.metadata!!.getString(MediaMetadata.METADATA_KEY_ALBUM)
				?: return,
			duration = mediaController.metadata!!.getLong(MediaMetadata.METADATA_KEY_DURATION),
			cover = Song.bitmapToBase64(mediaController.metadata!!.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART))
		).run {

			val isSameAsPrevious = playingSong?.title != title
					&& playingSong?.artist != artist
					&& playingSong?.album != album
					&& playingSong?.duration != duration
					&& playingSong?.cover == cover

			val isDataPartial = this.isPartial()

			if (
				this == playingSong || isSameAsPrevious && debounceCount < 1 || isDataPartial
			) {
				debounceCount++
				return
			}

			debounceCount = 0

			scope.launch { statsDataStore.updateData { it.addSongToHistory(this@run) } }
			scope.launch { statsDataStore.updateData { it.incrementTotalSongsPlayed() } }

			playingSong = this
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
