package fr.angel.soundtap.navigation

sealed class Screens(
	val route: String,
	val showBackArrow: Boolean = false,
) {
	data object App : Screens(route = "app") {
		data object Home : Screens(route = "home")
		data object Customization : Screens(route = "customization", showBackArrow = true)
		data object History : Screens(route = "history", showBackArrow = true)
		data object Settings : Screens(route = "settings", showBackArrow = true)
		data object Support : Screens(route = "support", showBackArrow = true)
	}

	data object Onboarding : Screens(route = "onboarding")

	companion object {
		fun fromRoute(s: String): Screens {
			return when (s) {

				// App
				App.route -> App
				App.Home.route -> App.Home
				App.Customization.route -> App.Customization
				App.History.route -> App.History
				App.Settings.route -> App.Settings
				App.Support.route -> App.Support

				// Onboarding
				Onboarding.route -> Onboarding

				else -> throw IllegalArgumentException("Route $s is not recognized.")
			}
		}
	}
}