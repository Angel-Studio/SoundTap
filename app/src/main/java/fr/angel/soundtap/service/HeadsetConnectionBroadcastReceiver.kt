package fr.angel.soundtap.service

import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class HeadsetConnectionBroadcastReceiver : BroadcastReceiver() {

	companion object {
		const val TAG = "HeadsetConnectionBroadcastReceiver"
	}

	private val scope by lazy { CoroutineScope(Dispatchers.Main) }

	override fun onReceive(context: Context, intent: Intent) {
		Log.i(TAG, "Received broadcast: ${intent.action}")
		if (intent.action != BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) return

		val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)

		/*scope.launch {
			val autoPlay = customizationRepository.autoPlayEnabled.first()
			val autoPlayMode = customizationRepository.autoPlayMode.first()
			val preferredMediaPlayer = dataStore.preferredMediaPlayer.first()

			Log.d(TAG, "Auto play: $autoPlay, preferred media player: $preferredMediaPlayer")

			// If auto play is disabled or the auto play mode is not on headset connected, return
			if (autoPlayMode.isOnHeadsetConnectedActive.not()) return@launch
			if (!autoPlay || preferredMediaPlayer == null) return@launch

			if (state == BluetoothProfile.STATE_CONNECTED) {
				Log.i(TAG, "Headset connected, starting media player")
				GlobalHelper.startMediaPlayer(context, preferredMediaPlayer)
			}
		}*/
	}
}