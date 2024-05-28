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