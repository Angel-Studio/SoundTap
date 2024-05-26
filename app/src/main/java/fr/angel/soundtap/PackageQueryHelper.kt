package fr.angel.soundtap

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

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

@Module
@InstallIn(SingletonComponent::class)
object PackageQueryModule {

	@Provides
	@Singleton
	fun providePackageQueryHelper(@ApplicationContext application: Context): PackageQueryHelper {
		return PackageQueryHelper(application as Application)
	}
}