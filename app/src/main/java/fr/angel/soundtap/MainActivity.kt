package fr.angel.soundtap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.angel.soundtap.navigation.Screens
import fr.angel.soundtap.navigation.SoundTapNavGraph
import fr.angel.soundtap.service.SoundTapAccessibilityService
import fr.angel.soundtap.ui.components.BottomControlBar
import fr.angel.soundtap.ui.theme.FontPilowlava
import fr.angel.soundtap.ui.theme.SoundTapTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


	private lateinit var mainViewModel: MainViewModel
	private var shouldKeepSplashScreenOn = mutableStateOf(true)

	@OptIn(ExperimentalMaterial3Api::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		installSplashScreen()
			.setKeepOnScreenCondition { shouldKeepSplashScreenOn.value }

		setContent {
			mainViewModel = hiltViewModel<MainViewModel>()
			val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

			LaunchedEffect(key1 = Unit) {
				mainViewModel.updatePermissionStates(this@MainActivity)
			}

			val navController = rememberNavController()
			val currentBackStack by navController.currentBackStackEntryAsState()
			val currentDestination = currentBackStack?.destination
			val currentScreen: Screens =
				Screens.fromRoute(currentDestination?.route ?: uiState.defaultScreen.route)

			val serviceUiState by SoundTapAccessibilityService.uiState.collectAsState()

			LaunchedEffect(key1 = uiState.finishedInitializations) {
				shouldKeepSplashScreenOn.value = !uiState.finishedInitializations
			}

			LaunchedEffect(key1 = Unit) {
				mainViewModel.updatePermissionStates(this@MainActivity)
			}

			val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

			SoundTapTheme {
				Scaffold(
					modifier = Modifier
						.fillMaxSize()
						.nestedScroll(scrollBehavior.nestedScrollConnection),
					topBar = {
						CenterAlignedTopAppBar(
							navigationIcon = {
								AnimatedVisibility(
									visible = currentScreen.showBackArrow,
									enter = scaleIn(),
									exit = scaleOut()
								) {
									IconButton(onClick = { navController.popBackStack() }) {
										Icon(
											imageVector = Icons.AutoMirrored.Filled.ArrowBack,
											contentDescription = "Back"
										)
									}
								}
							},
							title = {
								Text(
									modifier = Modifier.padding(
										horizontal = 16.dp,
										vertical = 8.dp
									),
									text = "SoundTap",
									style = MaterialTheme.typography.displayMedium,
									fontFamily = FontPilowlava,
									fontWeight = FontWeight.ExtraBold
								)
							}
						)
					},
					bottomBar = {
						AnimatedVisibility(
							visible = currentScreen == Screens.App.Home,
							enter = slideInVertically { it } + expandVertically() + fadeIn(),
							exit = slideOutVertically { it } + shrinkVertically() + fadeOut()
						) {
							BottomControlBar(
								serviceUiState = serviceUiState
							)
						}
					}
				) { innerPadding ->
					SoundTapNavGraph(
						modifier = Modifier.padding(innerPadding),
						navController = navController,
					)
				}
			}
		}
	}

	override fun onResume() {
		if (::mainViewModel.isInitialized) {
			mainViewModel.updatePermissionStates(this)
		}
		super.onResume()
	}
}