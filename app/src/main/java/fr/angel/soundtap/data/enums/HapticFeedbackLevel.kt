/*
 *
 *  * Copyright (c) 2024 Angel Studio
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package fr.angel.soundtap.data.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import fr.angel.soundtap.R

enum class HapticFeedbackLevel(
	@StringRes val title: Int,
	@DrawableRes val icon: Int,
) {
	NONE(
		R.string.haptic_feedback_none,
		R.drawable.haptic_feedback_none,
	),
	LIGHT(
		R.string.haptic_feedback_light,
		R.drawable.haptic_feedback_light,
	),
	MEDIUM(
		R.string.haptic_feedback_medium,
		R.drawable.haptic_feedback_medium,
	),
	STRONG(
		R.string.haptic_feedback_strong,
		R.drawable.haptic_feedback_strong,
	),
}
