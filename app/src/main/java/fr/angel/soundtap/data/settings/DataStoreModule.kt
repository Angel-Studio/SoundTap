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