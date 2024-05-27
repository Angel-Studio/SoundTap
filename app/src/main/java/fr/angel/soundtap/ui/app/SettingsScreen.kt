package fr.angel.soundtap.ui.app

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.pm.ResolveInfo
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.imageLoader
import fr.angel.soundtap.MainViewModel
import fr.angel.soundtap.R
import fr.angel.soundtap.supportedStartMediaPlayerPackages
import fr.angel.soundtap.tiles.ServiceTile
import fr.angel.soundtap.ui.components.settings.SettingsItem
import fr.angel.soundtap.ui.components.settings.SettingsItemCustomBottom
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SettingsScreen(
	modifier: Modifier = Modifier,
	animatedVisibilityScope: AnimatedVisibilityScope,
	mainViewModel: MainViewModel,
) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	val statusBarManager = context.getSystemService(StatusBarManager::class.java)

	val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

	Card(
		modifier = modifier
			.padding(8.dp)
			.fillMaxSize()
			.sharedElement(
				state = rememberSharedContentState(
					key = "Settings-card"
				),
				animatedVisibilityScope = animatedVisibilityScope
			)
	) {
		Column(
			modifier = Modifier.fillMaxSize()
		) {
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
								key = "Settings-icon",
							),
							animatedVisibilityScope = animatedVisibilityScope
						),
					imageVector = Icons.Default.Settings,
					contentDescription = null,
				)

				Text(
					text = "Settings",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					modifier = Modifier
						.sharedBounds(
							rememberSharedContentState(
								key = "Settings"
							),
							animatedVisibilityScope = animatedVisibilityScope
						)
				)
			}
			HorizontalDivider()
			LazyColumn(
				modifier = Modifier.fillMaxWidth(),
				contentPadding = PaddingValues(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				item {
					AnimatedVisibility(
						visible = ServiceTile.isAdded.not() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
						enter = fadeIn() + expandVertically(),
						exit = fadeOut() + shrinkVertically()
					) {
						SettingsItem(
							title = "Add quick tile",
							subtitle = "Add a tile to the quick settings panel to quickly access SoundTap",
							icon = Icons.Rounded.GridView,
							onClick = {
								if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@SettingsItem
								statusBarManager.requestAddTileService(
									/* tileServiceComponentName = */ ComponentName(
										context,
										ServiceTile::class.java
									),
									/* tileLabel = */
									context.getString(R.string.app_name),
									/* icon = */
									android.graphics.drawable.Icon.createWithResource(
										context,
										R.drawable.app_icon
									),
									/* resultExecutor = */
									{ it.run() },
									/* resultCallback = */
									{ Log.i("Settings", "Tile added") }
								)
							}
						)
					}
				}

				item {
					var supportedMediaPlayersExpanded by rememberSaveable {
						mutableStateOf(
							uiState.playersPackages.size < 5
						)
					}
					SettingsItemCustomBottom(
						title = "Supported players",
						subtitle = "Select the players you want to control with SoundTap",
						icon = Icons.Rounded.Radio,
						expanded = supportedMediaPlayersExpanded,
						onClick = {
							supportedMediaPlayersExpanded = supportedMediaPlayersExpanded.not()
						},
						trailing = {
							IconButton(onClick = {
								supportedMediaPlayersExpanded = supportedMediaPlayersExpanded.not()
							}) {
								Icon(
									modifier = Modifier.rotate(
										animateFloatAsState(
											targetValue = if (supportedMediaPlayersExpanded) 180f else 0f,
											label = "SupportedMediaPlayerIconRotation"
										).value
									),
									imageVector = Icons.Default.KeyboardArrowDown,
									contentDescription = null
								)
							}
						},
						content = {
							Column(
								modifier = Modifier.fillMaxWidth(),
								verticalArrangement = Arrangement.spacedBy(8.dp)
							) {
								uiState.playersPackages.forEach { mediaPackage ->
									MediaPlayerSwitchRow(
										modifier = Modifier.fillMaxWidth(),
										resolveInfo = mediaPackage,
										onClick = {
											scope.launch {
												mainViewModel.toggleUnsupportedMediaPlayer(
													mediaPackage.activityInfo.packageName
												)
											}
										},
										selected = uiState.appSettings.unsupportedMediaPlayers.contains(
											mediaPackage.activityInfo.packageName
										).not()
									)
								}
							}
						}
					)
				}

				item {
					var preferredMediaPlayerExpanded by rememberSaveable {
						mutableStateOf(
							uiState.playersPackages.size < 5
						)
					}
					SettingsItemCustomBottom(
						title = "Preferred player",
						subtitle = "Select the preferred player that SoundTap should control by default",
						icon = Icons.Rounded.StarBorder,
						expanded = preferredMediaPlayerExpanded,
						onClick = {
							preferredMediaPlayerExpanded = preferredMediaPlayerExpanded.not()
						},
						trailing = {
							IconButton(onClick = {
								preferredMediaPlayerExpanded = preferredMediaPlayerExpanded.not()
							}) {
								Icon(
									modifier = Modifier.rotate(
										animateFloatAsState(
											targetValue = if (preferredMediaPlayerExpanded) 180f else 0f,
											label = "SupportedMediaPlayerIconRotation"
										).value
									),
									imageVector = Icons.Default.KeyboardArrowDown,
									contentDescription = null
								)
							}
						},
						content = {
							Column(
								modifier = Modifier.fillMaxWidth(),
								verticalArrangement = Arrangement.spacedBy(8.dp)
							) {
								uiState.playersPackages.forEach { mediaPackage ->
									if (mediaPackage.activityInfo.packageName !in supportedStartMediaPlayerPackages) return@forEach
									MediaPlayerRadioRow(
										modifier = Modifier.fillMaxWidth(),
										resolveInfo = mediaPackage,
										onClick = {
											scope.launch {
												mainViewModel.setPreferredMediaPlayer(mediaPackage.activityInfo.packageName)
											}
										},
										selected = uiState.customizationSettings.preferredMediaPlayer == mediaPackage.activityInfo.packageName
									)
								}
							}
						}
					)
				}
			}
		}
	}
}

@Composable
private fun MediaPlayerSwitchRow(
	modifier: Modifier = Modifier,
	resolveInfo: ResolveInfo,
	onClick: () -> Unit,
	selected: Boolean = false,
) {
	val context = LocalContext.current
	val packageManager = context.packageManager
	val applicationInfo = try {
		packageManager.getApplicationInfo(resolveInfo.activityInfo.packageName, 0)
	} catch (e: Exception) {
		return
	}

	val appName = rememberSaveable(applicationInfo) {
		packageManager.getApplicationLabel(applicationInfo).toString()
	}
	val appIcon = remember(applicationInfo) { packageManager.getApplicationIcon(applicationInfo) }

	val backgroundColor by animateColorAsState(
		targetValue = if (selected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
		label = "MediaPlayerSwitchRowBackgroundColor"
	)

	Row(
		modifier = modifier
			.clip(MaterialTheme.shapes.medium)
			.background(backgroundColor)
			.clickable(onClick = onClick)
			.padding(8.dp)
			.height(IntrinsicSize.Min),
		verticalAlignment = Alignment.CenterVertically
	) {
		AsyncImage(
			model = appIcon,
			imageLoader = context.imageLoader,
			contentDescription = null,
			modifier = Modifier.size(48.dp)
		)
		Spacer(Modifier.width(8.dp))
		Column(
			modifier = Modifier
				.fillMaxHeight()
				.weight(1f)
				.padding(4.dp),
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(
				text = appName,
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.Bold,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
			Text(
				text = resolveInfo.activityInfo.packageName,
				style = MaterialTheme.typography.bodySmall,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		}
		Spacer(modifier = Modifier.width(8.dp))
		Switch(
			checked = selected,
			onCheckedChange = null
		)
		Spacer(modifier = Modifier.width(8.dp))
	}
}

@Composable
private fun MediaPlayerRadioRow(
	modifier: Modifier = Modifier,
	resolveInfo: ResolveInfo,
	onClick: () -> Unit,
	selected: Boolean = false,
) {
	val context = LocalContext.current
	val packageManager = context.packageManager
	val applicationInfo = try {
		packageManager.getApplicationInfo(resolveInfo.activityInfo.packageName, 0)
	} catch (e: Exception) {
		return
	}

	val appName = rememberSaveable(applicationInfo) {
		packageManager.getApplicationLabel(applicationInfo).toString()
	}
	val appIcon = remember(applicationInfo) { packageManager.getApplicationIcon(applicationInfo) }

	val backgroundColor by animateColorAsState(
		targetValue = if (selected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
		label = "MediaPlayerSwitchRowBackgroundColor"
	)

	Row(
		modifier = modifier
			.clip(MaterialTheme.shapes.medium)
			.background(backgroundColor)
			.clickable(onClick = onClick)
			.padding(8.dp)
			.height(IntrinsicSize.Min),
		verticalAlignment = Alignment.CenterVertically
	) {
		AsyncImage(
			model = appIcon,
			imageLoader = context.imageLoader,
			contentDescription = null,
			modifier = Modifier.size(48.dp)
		)
		Spacer(Modifier.width(8.dp))
		Column(
			modifier = Modifier
				.fillMaxHeight()
				.weight(1f)
				.padding(4.dp),
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(
				text = appName,
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.Bold,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
			Text(
				text = resolveInfo.activityInfo.packageName,
				style = MaterialTheme.typography.bodySmall,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		}
		Spacer(modifier = Modifier.width(8.dp))
		RadioButton(
			selected = selected,
			onClick = null
		)
		Spacer(modifier = Modifier.width(8.dp))
	}
}