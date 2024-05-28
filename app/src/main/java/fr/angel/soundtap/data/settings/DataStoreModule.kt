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
package fr.angel.soundtap.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.angel.soundtap.data.settings.customization.CustomizationSettings
import fr.angel.soundtap.data.settings.customization.customizationSettingsDataStore
import fr.angel.soundtap.data.settings.settings.AppSettings
import fr.angel.soundtap.data.settings.settings.settingsDataStore
import fr.angel.soundtap.data.settings.stats.StatsSettings
import fr.angel.soundtap.data.settings.stats.statsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideAppSettingsDataStore(@ApplicationContext context: Context): DataStore<AppSettings> {
        return context.settingsDataStore
    }

    @Provides
    @Singleton
    fun provideCustomizationSettingsDataStore(@ApplicationContext context: Context): DataStore<CustomizationSettings> {
        return context.customizationSettingsDataStore
    }

    @Provides
    @Singleton
    fun provideStatsSettingsDataStore(@ApplicationContext context: Context): DataStore<StatsSettings> {
        return context.statsDataStore
    }
}
