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
