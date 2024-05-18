package fr.angel.soundtap.data.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

fun String.toSong(): Song {
	return Gson().fromJson(this, Song::class.java)
}

data class Song(
	val title: String,
	val artist: String,
	val album: String,
	val duration: Long,
	val cover: String,
) {
	companion object {
		fun bitmapToBase64(bitmap: Bitmap): String {
			val byteArrayOutputStream = ByteArrayOutputStream()
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
			val byteArray = byteArrayOutputStream.toByteArray()
			return Base64.encodeToString(byteArray, Base64.DEFAULT)
		}

		// Convert the base64 string back to a bitmap
		fun base64ToBitmap(base64: String): Bitmap {
			val decodedBytes = Base64.decode(base64, 0)
			return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
		}
	}

	override fun toString(): String {
		return Gson().toJson(this)
	}

	override fun equals(other: Any?): Boolean {
		if (other !is Song) return false

		if (title != other.title) return false
		if (artist != other.artist) return false
		if (album != other.album) return false
		if (duration != other.duration) return false
		if (cover != other.cover) return false

		return false
	}

	override fun hashCode(): Int {
		var result = title.hashCode()
		result = 31 * result + artist.hashCode()
		result = 31 * result + album.hashCode()
		result = 31 * result + duration.hashCode()
		result = 31 * result + cover.hashCode()
		return result
	}

	fun isPartial(): Boolean {
		return title.isEmpty() || artist.isEmpty() || album.isEmpty() || duration == 0L || cover.isEmpty()
	}
}