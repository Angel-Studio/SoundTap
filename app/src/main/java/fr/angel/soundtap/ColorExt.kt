package fr.angel.soundtap

import androidx.compose.ui.graphics.Color

fun Color.hue(): Float {
	val r = red / 255.0
	val g = green / 255.0
	val b = blue / 255.0
	val max = maxOf(r, g, b)
	val min = minOf(r, g, b)
	val c = max - min
	var hue: Double = 0.0
	if (c == 0.0) {
		hue = 0.0
	} else {
		when (max) {
			r -> {
				val segment = (g - b) / c
				var shift = 0.0 / 60.0 // R° / (360° / hex sides)
				if (segment < 0.0) { // hue > 180, full rotation
					shift = 360.0 / 60.0 // R° / (360° / hex sides)
				}
				hue = segment + shift
			}

			g -> {
				val segment = (b - r) / c
				val shift = 120.0 / 60.0 // G° / (360° / hex sides)
				hue = segment + shift
			}

			b -> {
				val segment = (r - g) / c
				val shift = 240.0 / 60.0 // B° / (360° / hex sides)
				hue = segment + shift
			}
		}
	}
	return (hue * 60.0).toFloat() // hue is in [0,6], scale it up
}

