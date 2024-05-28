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
