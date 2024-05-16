package fr.angel.soundtap.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import fr.angel.soundtap.data.models.Song
import fr.angel.soundtap.service.media.MediaCallback

@Composable
fun MediaCard(
	modifier: Modifier = Modifier,
	media: MediaCallback,
) {
	val song = media.playingSong ?: return

	val generatedBitmap: Bitmap = remember(song.cover) { Song.base64ToBitmap(song.cover) }

	val coverPalette = Palette.from(generatedBitmap).generate()
	val dominantColor = Color(coverPalette.getVibrantColor(Color.White.toArgb()))

	val playbackCornerRadius by animateDpAsState(
		targetValue = if (media.isPlaying) 16.dp else 96.dp,
		label = "Playback corner radius",
	)

	Card(
		modifier = modifier
			.height(IntrinsicSize.Min)
			.fillMaxWidth()
			.clip(MaterialTheme.shapes.extraLarge),
		shape = MaterialTheme.shapes.extraLarge,
	) {
		Box(
			modifier = Modifier
				.fillMaxSize(),
		) {
			Crossfade(
				modifier = Modifier.fillMaxSize(),
				targetState = generatedBitmap,
				label = "Cover image",
			) { cover ->
				AsyncImage(
					model = cover,
					contentDescription = null,
					contentScale = ContentScale.Crop,
					colorFilter = ColorFilter.tint(
						color = dominantColor.copy(alpha = 0.5f),
						blendMode = BlendMode.Color
					),
					modifier = Modifier
						.fillMaxSize()
						.drawWithContent {
							drawContent()
							drawRect(
								brush = Brush.radialGradient(
									listOf(
										Color.Transparent,
										Color.Black.copy(alpha = 0.35f)
									),
									center = size.center,
									radius = size.width / 2f
								),
								size = size,
								blendMode = BlendMode.Darken
							)
						}
				)
			}

			Column(
				modifier = Modifier.fillMaxSize()
			) {
				Spacer(modifier = Modifier.weight(1f))

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.Bottom,
				) {
					Column(
						modifier = Modifier
							.weight(1f)
							.padding(
								start = 24.dp,
								end = 24.dp,
								bottom = 24.dp,
							),
						verticalArrangement = Arrangement.spacedBy(2.dp),
					) {
						AnimatedContent(
							targetState = song.title,
							label = "Song title",
							transitionSpec = {
								slideInHorizontally { height -> height } + fadeIn() togetherWith
										slideOutHorizontally { height -> -height } + fadeOut()
							},
						) { title ->
							Text(
								text = title,
								style = MaterialTheme.typography.titleLarge,
								fontWeight = FontWeight.Bold,
								color = Color.White,
								maxLines = 2,
								overflow = TextOverflow.Ellipsis,
							)
						}
						AnimatedContent(
							targetState = song.artist,
							label = "Song artist",
							transitionSpec = {
								slideInHorizontally { height -> height } + fadeIn() togetherWith
										slideOutHorizontally { height -> -height } + fadeOut()
							},
						) { artist ->
							Text(
								text = artist,
								style = MaterialTheme.typography.labelMedium,
								fontWeight = FontWeight.Bold,
								color = Color.White,
								maxLines = 1,
								overflow = TextOverflow.Ellipsis,
							)
						}
					}

					Row(
						modifier = Modifier
							.padding(end = 24.dp, bottom = 24.dp),
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(
							modifier = Modifier
								.padding(4.dp)
								.size(24.dp)
								.clickable(
									interactionSource = remember { MutableInteractionSource() },
									indication = rememberRipple(
										bounded = false,
										color = Color.White,
										radius = 400.dp
									),
									onClick = { media.skipToPrevious() }
								),
							imageVector = Icons.Outlined.SkipPrevious,
							contentDescription = "Previous track",
							tint = Color.White
						)
						FilledTonalIconButton(
							modifier = Modifier.size(48.dp),
							shape = RoundedCornerShape(playbackCornerRadius),
							onClick = { media.togglePlayPause() },
							colors = IconButtonDefaults.filledTonalIconButtonColors(
								containerColor = Color(
									ColorUtils.blendARGB(
										dominantColor.toArgb(),
										Color.White.toArgb(),
										0.6f
									)
								),
								contentColor = Color.Black
							),
						) {
							Icon(
								imageVector = if (media.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
								contentDescription = "Play/Pause"
							)
						}

						Icon(
							modifier = Modifier
								.padding(4.dp)
								.size(24.dp)
								.clickable(
									interactionSource = remember { MutableInteractionSource() },
									indication = rememberRipple(
										bounded = false,
										color = Color.White,
										radius = 400.dp
									),
									onClick = { media.skipToNext() }
								),
							imageVector = Icons.Outlined.SkipNext,
							contentDescription = "Next track",
							tint = Color.White
						)
					}
				}
			}
		}

	}
}