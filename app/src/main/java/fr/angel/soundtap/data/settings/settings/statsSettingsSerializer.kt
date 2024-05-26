package fr.angel.soundtap.data.settings.settings

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

val Context.settingsDataStore by dataStore(
	fileName = "settings.json",
	serializer = SettingsSerializer
)

private object SettingsSerializer : Serializer<AppSettings> {
	override val defaultValue: AppSettings
		get() = AppSettings()

	override suspend fun readFrom(input: InputStream): AppSettings {
		return try {
			Json.decodeFromString(
				deserializer = AppSettings.serializer(),
				string = input.readBytes().decodeToString()
			)
		} catch (e: Exception) {
			e.printStackTrace()
			defaultValue
		}
	}

	override suspend fun writeTo(t: AppSettings, output: OutputStream) {
		withContext(Dispatchers.IO) {
			output.write(
				Json.encodeToString(
					serializer = AppSettings.serializer(),
					value = t
				).encodeToByteArray()
			)
		}
	}
}