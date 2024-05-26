package fr.angel.soundtap.data.enums

import androidx.annotation.DrawableRes
import fr.angel.soundtap.R

enum class HapticFeedbackLevel(
	val title: String,
	@DrawableRes val icon: Int,
) {
	NONE("None", R.drawable.haptic_feedback_none),
	LIGHT("Light", R.drawable.haptic_feedback_light),
	MEDIUM("Medium", R.drawable.haptic_feedback_medium),
	STRONG("Strong", R.drawable.haptic_feedback_strong),
}