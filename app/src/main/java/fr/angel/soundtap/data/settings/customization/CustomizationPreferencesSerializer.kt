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
package fr.angel.soundtap.data.settings.customization

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

val Context.customizationSettingsDataStore by dataStore(
	fileName = "customization_settings.json",
	serializer = CustomizationSettingsSerializer,
)

private object CustomizationSettingsSerializer : Serializer<CustomizationSettings> {
	override val defaultValue: CustomizationSettings
		get() = CustomizationSettings()

	override suspend fun readFrom(input: InputStream): CustomizationSettings {
		return try {
			Json.decodeFromString(
				deserializer = CustomizationSettings.serializer(),
				string = input.readBytes().decodeToString(),
			)
		} catch (e: Exception) {
			e.printStackTrace()
			defaultValue
		}
	}

	override suspend fun writeTo(
		t: CustomizationSettings,
		output: OutputStream,
	) {
		withContext(Dispatchers.IO) {
			output.write(
				Json.encodeToString(
					serializer = CustomizationSettings.serializer(),
					value = t,
				).encodeToByteArray(),
			)
		}
	}
}
