package fr.angel.soundtap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.angel.soundtap.data.models.Song

@Composable
fun SongRow(
	modifier: Modifier = Modifier,
	song: Song,
) {
	val generatedBitmap = remember { Song.base64ToBitmap(song.cover) }

	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceContainer
		),
		onClick = { }
	) {
		Row(
			modifier = Modifier.padding(4.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			AsyncImage(
				model = generatedBitmap,
				contentDescription = null,
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.size(64.dp)
					.clip(MaterialTheme.shapes.medium)
			)

			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				Text(
					text = song.title,
					style = MaterialTheme.typography.labelLarge,
					fontWeight = FontWeight.Bold
				)

				Text(
					text = song.artist,
					style = MaterialTheme.typography.labelSmall,
					fontWeight = FontWeight.SemiBold
				)
			}
		}
	}
}