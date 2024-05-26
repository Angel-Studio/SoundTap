package fr.angel.soundtap.data.settings.stats

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

val Context.statsDataStore by dataStore(
	fileName = "stats.json",
	serializer = StatsSerializer
)

private object StatsSerializer : Serializer<StatsSettings> {
	override val defaultValue: StatsSettings
		get() = StatsSettings()

	override suspend fun readFrom(input: InputStream): StatsSettings {
		return try {
			Json.decodeFromString(
				deserializer = StatsSettings.serializer(),
				string = input.readBytes().decodeToString()
			)
		} catch (e: Exception) {
			e.printStackTrace()
			defaultValue
		}
	}

	override suspend fun writeTo(t: StatsSettings, output: OutputStream) {
		withContext(Dispatchers.IO) {
			output.write(
				Json.encodeToString(
					serializer = StatsSettings.serializer(),
					value = t
				).encodeToByteArray()
			)
		}
	}
}