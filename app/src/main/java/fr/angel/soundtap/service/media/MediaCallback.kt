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
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaCallback
	@Inject
	constructor(
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

		fun togglePlayPause() =
			if (isPlaying) {
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
				val bitmap =
					metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART) ?: run {
						Log.w("MediaCallback", "No album art found")
						return
					}
				val filename =
					metadata.getString(MediaMetadata.METADATA_KEY_TITLE) +
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
					val duplicate =
						playingSong != null &&
							(addedTime - (playingSong?.addedTime ?: 0)) > 250

					if (
						this == playingSong ||
						this.isPartial() ||
						duplicate &&
						debounceCount == 0
					) {
						debounceCount = 1
						return
					}

					debounceCount = 0

					scope.launch {
						statsDataStore.updateData { settings ->
							settings.addSongToHistory(
								this@run,
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
