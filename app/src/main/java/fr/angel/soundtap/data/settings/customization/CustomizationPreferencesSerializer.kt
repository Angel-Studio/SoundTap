package fr.angel.soundtap.data.settings.customization

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

val Context.customizationSettingsDataStore by dataStore(
	fileName = "customization_settings.json",
	serializer = CustomizationSettingsSerializer
)

private object CustomizationSettingsSerializer : Serializer<CustomizationSettings> {
	override val defaultValue: CustomizationSettings
		get() = CustomizationSettings()

	override suspend fun readFrom(input: InputStream): CustomizationSettings {
		return try {
			Json.decodeFromString(
				deserializer = CustomizationSettings.serializer(),
				string = input.readBytes().decodeToString()
			)
		} catch (e: Exception) {
			e.printStackTrace()
			defaultValue
		}
	}

	override suspend fun writeTo(t: CustomizationSettings, output: OutputStream) {
		withContext(Dispatchers.IO) {
			output.write(
				Json.encodeToString(
					serializer = CustomizationSettings.serializer(),
					value = t
				).encodeToByteArray()
			)
		}
	}
}