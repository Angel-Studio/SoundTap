package fr.angel.soundtap.ui.app

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.angel.soundtap.GlobalHelper
import fr.angel.soundtap.ui.components.SettingsItem

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SupportScreen(
	modifier: Modifier = Modifier,
	animatedVisibilityScope: AnimatedVisibilityScope,
) {
	val uriHandler = LocalUriHandler.current

	Card(
		modifier = modifier
			.padding(8.dp)
			.fillMaxSize()
			.sharedElement(
				state = rememberSharedContentState(
					key = "Support-card"
				),
				animatedVisibilityScope = animatedVisibilityScope
			)
	) {
		Column(modifier = Modifier.fillMaxSize()) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.surfaceContainerHighest)
					.padding(16.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Icon(
					modifier = Modifier
						.size(48.dp)
						.sharedElement(
							state = rememberSharedContentState(
								key = "Support-icon",
							),
							animatedVisibilityScope = animatedVisibilityScope
						),
					imageVector = Icons.Default.Support,
					contentDescription = null,
				)

				Text(
					text = "Support",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					modifier = Modifier
						.sharedBounds(
							rememberSharedContentState(
								key = "Support"
							),
							animatedVisibilityScope = animatedVisibilityScope
						)
				)
			}
			HorizontalDivider()
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 16.dp)
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Spacer(modifier = Modifier.height(8.dp))
				SettingsItem(
					title = "Report a Bug or Suggest a Feature",
					subtitle = "Report a bug or issue with the app or suggest a feature that you would like to see",
					icon = Icons.Default.BugReport,
					onClick = { uriHandler.openUri("https://github.com/Angel-Studio/SoundTap/issues/new/choose") }
				)
				SettingsItem(
					title = "View on GitHub",
					subtitle = "View the source code and contribute to the project",
					icon = Icons.Default.Code,
					onClick = { uriHandler.openUri("https://github.com/Angel-Studio/SoundTap") }
				)
				SettingsItem(
					title = "Discord Server",
					subtitle = "Join the community and get support",
					icon = Icons.Default.Groups,
					onClick = { uriHandler.openUri("https://discord.gg/8NfBrxKs4T") }
				)
				SettingsItem(
					title = "Privacy Policy",
					subtitle = "Read the privacy policy of the app",
					icon = Icons.Default.Policy,
					onClick = { uriHandler.openUri(GlobalHelper.PRIVACY_POLICY_URL) }
				)
				SettingsItem(
					title = "Terms of Service",
					subtitle = "Read the terms of service of the app",
					icon = Icons.Default.Gavel,
					onClick = { uriHandler.openUri(GlobalHelper.TERMS_OF_SERVICE_URL) }
				)
				Spacer(modifier = Modifier.height(8.dp))
			}
		}
	}
}