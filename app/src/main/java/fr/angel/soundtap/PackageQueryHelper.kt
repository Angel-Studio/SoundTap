package fr.angel.soundtap

import android.app.Application
import android.content.Intent
import android.content.pm.ResolveInfo
import javax.inject.Inject

class PackageQueryHelper @Inject constructor(private val application: Application) {
	fun getMediaPlayersInstalled(): Set<ResolveInfo> {
		val packageManager = application.packageManager

		val mainIntent = Intent(Intent.ACTION_MAIN, null)
		mainIntent.addCategory(Intent.CATEGORY_APP_MUSIC)

		val playerListPackages: List<ResolveInfo> =
			packageManager.queryIntentActivities(mainIntent, 0)

		// Remove duplicates
		val playerListPackagesNoDuplicates =
			playerListPackages.distinctBy { it.activityInfo.packageName }

		return playerListPackagesNoDuplicates.toSet()
	}
}