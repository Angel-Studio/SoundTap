package fr.angel.soundtap.data.settings.stats

import fr.angel.soundtap.data.models.Song
import kotlinx.serialization.Serializable

@Serializable
data class StatsSettings(
	val history: Set<Song> = emptySet(),
	val totalSongsPlayed: Int = 0,
	val totalSongsSkipped: Int = 0,
) {
	fun incrementTotalSongsPlayed() = copy(totalSongsPlayed = totalSongsPlayed + 1)
	fun incrementTotalSongsSkipped() = copy(totalSongsSkipped = totalSongsSkipped + 1)
	fun addSongToHistory(song: Song) = copy(history = history + song)
}