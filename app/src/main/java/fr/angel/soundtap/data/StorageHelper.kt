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
package fr.angel.soundtap.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object StorageHelper {

    private const val TAG = "StorageHelper"

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String): String? {
        try {
            val formattedFileName = filename
                .replace(" ", "_")
                .replace(":", "_")
                .replace("/", "_")
                .replace("\\", "_")

            val file = File(context.filesDir, formattedFileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            return file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bitmap to file", e)
            return null
        }
    }

    fun loadBitmapFromFile(filePath: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(filePath)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from file", e)
            null
        }
    }
}
