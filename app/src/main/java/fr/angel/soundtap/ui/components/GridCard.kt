package fr.angel.soundtap.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.GridCard(
	modifier: Modifier = Modifier,
	animatedVisibilityScope: AnimatedVisibilityScope,
	icon: ImageVector,
	label: String,
	onClick: () -> Unit,
) {
	Card(
		modifier = modifier
			.aspectRatio(1f)
			.sharedElement(
				state = rememberSharedContentState(
					key = "$label-card",
				),
				animatedVisibilityScope = animatedVisibilityScope
			),
		shape = MaterialTheme.shapes.extraLarge,
		onClick = onClick
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
		) {
			Icon(
				modifier = Modifier
					.size(64.dp)
					.sharedElement(
						state = rememberSharedContentState(
							key = "$label-icon",
						),
						animatedVisibilityScope = animatedVisibilityScope
					),
				imageVector = icon,
				contentDescription = null,
			)

			Text(
				text = label,
				style = MaterialTheme.typography.titleSmall,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.sharedBounds(
						rememberSharedContentState(
							key = label
						),
						animatedVisibilityScope = animatedVisibilityScope
					)
			)
		}
	}
}