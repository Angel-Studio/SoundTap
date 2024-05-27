package fr.angel.soundtap.service.media

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import androidx.compose.runtime.mutableStateMapOf
import fr.angel.soundtap.data.settings.stats.statsDataStore
import fr.angel.soundtap.service.NotificationService

object MediaReceiver {

	// Map of package name to callback
	val callbackMap = mutableStateMapOf<String, MediaCallback>()
	private val unsupportedCallbackMap = mutableStateMapOf<String, MediaCallback>()
	private lateinit var mediaSessionManager: MediaSessionManager

	private var unsupportedPlayers = emptySet<String>()

	val firstCallback: MediaCallback?
		get() = callbackMap.values.firstOrNull()

	private fun provideListener(context: Context) =
		MediaSessionManager.OnActiveSessionsChangedListener { mediaControllers ->
			if (mediaControllers != null) {
				for (mediaController in mediaControllers) {
					// Cancel if already exists
					if (callbackMap[mediaController.packageName] != null) continue

					// Create callback for this media controller and add it to the map of callbacks
					val callback =
						MediaCallback(
							mediaController = mediaController,
							onDestroyed = { removeMedia(mediaController) },
							onToggleSupportedPlayer = { supported ->
								toggleSupportedPlayer(
									supported,
									mediaController.packageName
								)
							},
							statsDataStore = context.statsDataStore,
							context = context
						)
					callbackMap[mediaController.packageName] = callback
					mediaController.registerCallback(callback)
				}
			}
		}

	private fun removeMedia(mediaController: MediaController) {
		callbackMap.remove(mediaController.packageName)
	}

	fun register(context: Context) {
		//val dataStore = DataStore(context)

		/*scope.launch {
			dataStore.unsupportedMediaPlayers.collect { unsupportedPlayers ->
				callbackMap.values.forEach { it.onUnsupportedPlayersChanged(unsupportedPlayers) }

				// Update the list of unsupported players
				this@MediaReceiver.unsupportedPlayers = unsupportedPlayers
			}
		}*/

		// Get the media session manager
		if (!MediaReceiver::mediaSessionManager.isInitialized) mediaSessionManager =
			context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

		// Register the listener for active sessions (new sessions)
		mediaSessionManager.addOnActiveSessionsChangedListener(
			provideListener(context),
			ComponentName(context, NotificationService::class.java)
		)

		// Register callbacks for already active sessions (if any)
		mediaSessionManager.getActiveSessions(
			ComponentName(
				context,
				NotificationService::class.java
			)
		).forEach { mediaController ->

			// Skip unsupported players
			if (unsupportedPlayers.contains(mediaController.packageName)) return@forEach

			// Cancel if already exists
			if (callbackMap[mediaController.packageName] != null) return@forEach

			// Create callback for this media controller and add it to the map of callbacks
			val mediaCallback = MediaCallback(
				mediaController = mediaController,
				onDestroyed = { removeMedia(mediaController) },
				onToggleSupportedPlayer = { supported ->
					toggleSupportedPlayer(
						supported,
						mediaController.packageName
					)
				},
				statsDataStore = context.statsDataStore,
				context = context
			)
			callbackMap[mediaController.packageName] = mediaCallback

			// Register callback
			mediaController.registerCallback(mediaCallback)
		}
	}

	fun unregister(context: Context) {
		mediaSessionManager.removeOnActiveSessionsChangedListener(provideListener(context))
		callbackMap.values.forEach { it.onDestroyed() }
		callbackMap.clear()
	}

	private fun toggleSupportedPlayer(supported: Boolean, packageName: String) {
		if (supported) {
			unsupportedCallbackMap.remove(packageName)?.let { callback ->
				callbackMap[packageName] = callback
			}
		} else {
			callbackMap.remove(packageName)?.let { callback ->
				unsupportedCallbackMap[packageName] = callback
			}
		}
	}
}