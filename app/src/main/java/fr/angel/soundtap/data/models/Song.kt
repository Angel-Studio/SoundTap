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
package fr.angel.soundtap.data.models

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val coverFilePath: String,
    val addedTime: Long = System.currentTimeMillis(),
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Song) return false

        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (duration != other.duration) return false
        if (coverFilePath != other.coverFilePath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + coverFilePath.hashCode()
        return result
    }

    fun isPartial(): Boolean {
        return title.isBlank() || artist.isBlank() || album.isBlank() || duration == 0L || coverFilePath.isBlank()
    }
}
