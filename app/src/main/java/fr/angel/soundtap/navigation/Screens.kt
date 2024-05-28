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
