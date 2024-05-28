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
package fr.angel.soundtap.tiles

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.angel.soundtap.R
import fr.angel.soundtap.service.SoundTapAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class StateModel(
	val state: Int,
	val label: String,
	val icon: Icon,
)

class ServiceTile : TileService() {

	private val scope by lazy { CoroutineScope(Dispatchers.IO) }

	private lateinit var state: StateModel

	companion object {
		const val TAG = "ServiceTile"

		var isAdded by mutableStateOf(false)
	}

	override fun onTileAdded() {
		super.onTileAdded()
		isAdded = true

		updateTile()
	}

	override fun onTileRemoved() {
		super.onTileRemoved()
		isAdded = false

		updateTile()
	}

	override fun onClick() {
		super.onClick()

		SoundTapAccessibilityService.toggleService()
	}

	override fun onCreate() {
		super.onCreate()
		isAdded = true

		scope.launch {
			SoundTapAccessibilityService.uiState.collect {
				state = StateModel(
					state = if (it.isRunning) {
						if (it.isActivated) {
							Tile.STATE_ACTIVE
						} else {
							Tile.STATE_INACTIVE
						}
					} else {
						Tile.STATE_UNAVAILABLE
					},
					label = getString(R.string.app_name),
					icon = if (it.isRunning) Icon.createWithResource(
						this@ServiceTile,
						R.drawable.round_power_settings_new_24
					) else Icon.createWithResource(
						this@ServiceTile,
						R.drawable.round_power_settings_new_24
					)
				)

				updateTile()
			}
		}
	}

	override fun onStartListening() {
		super.onStartListening()
		isAdded = true
	}

	private fun updateTile() {
		try {
			if (qsTile == null) return
			qsTile.label = state.label
			qsTile.contentDescription = state.label
			qsTile.state = state.state
			qsTile.icon = state.icon
			qsTile.updateTile()
		} catch (e: Exception) {
			Log.w(TAG, "Failed to update tile", e)
		}
	}
}
