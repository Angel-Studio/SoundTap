package fr.angel.soundtap

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import fr.angel.soundtap.data.enums.HapticFeedbackLevel

class VibratorHelper(context: Context) {
	private val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)!!

	fun createHapticFeedback(hapticLevel: HapticFeedbackLevel) = when (hapticLevel) {
		HapticFeedbackLevel.NONE -> {}
		HapticFeedbackLevel.LIGHT -> vibrator.vibrate(VibrationEffect.createOneShot(50, 20))
		HapticFeedbackLevel.MEDIUM -> vibrator.vibrate(VibrationEffect.createOneShot(75, 100))
		HapticFeedbackLevel.STRONG -> vibrator.vibrate(VibrationEffect.createOneShot(100, 250))
	}

	fun tick() = vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
	fun click() = vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
	fun doubleClick() =
		vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))

	fun heavyClick() =
		vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))

	fun short() =
		vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))


	fun medium() =
		vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))


	fun long() =
		vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
}