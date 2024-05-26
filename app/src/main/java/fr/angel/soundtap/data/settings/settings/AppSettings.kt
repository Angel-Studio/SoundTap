package fr.angel.soundtap.data.settings.settings

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
	val onboardingPageCompleted: Boolean = true,
	val unsupportedMediaPlayers: Set<String> = emptySet(),
)