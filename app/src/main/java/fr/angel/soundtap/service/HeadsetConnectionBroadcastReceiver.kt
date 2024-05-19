package fr.angel.soundtap.service

import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class HeadsetConnectionBroadcastReceiver : BroadcastReceiver() {

	companion object {
		const val TAG = "HeadsetConnectionBroadcastReceiver"
	}

	override fun onReceive(context: Context, intent: Intent) {
		if (intent.action != BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) return

		val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)

		if (state == BluetoothProfile.STATE_CONNECTED) {
			// Play music
			Log.i(TAG, "Headset connected")
		} else if (state == BluetoothProfile.STATE_DISCONNECTED) {
			// Pause music
			Log.i(TAG, "Headset disconnected")
		}
	}
}