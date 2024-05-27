package fr.angel.soundtap.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object StorageHelper {

	private const val TAG = "StorageHelper"

	fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String): String {
		val file = File(context.filesDir, filename)
		val outputStream = FileOutputStream(file)
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
		outputStream.close()
		Log.i(TAG, "Saved bitmap to file: $file")
		return file.absolutePath
	}

	fun loadBitmapFromFile(filePath: String): Bitmap {
		Log.i(TAG, "Loading bitmap from file: $filePath")
		return BitmapFactory.decodeFile(filePath)
	}
}