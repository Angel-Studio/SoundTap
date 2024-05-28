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
package fr.angel.soundtap.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class InfoCardType(
	val backgroundColor: Color,
) {
	Default(Color.Unspecified),
	Accessibility(Color(0xFFFFE0B2)),
	Notification(Color(0xFFFFCDD2)),
}

@Composable
fun InfoCard(
	modifier: Modifier = Modifier,
	cardType: InfoCardType = InfoCardType.Default,
	icon: ImageVector,
	title: String,
	body: String,
	onCardClick: () -> Unit = {},
	bottomContent: @Composable ColumnScope.() -> Unit = {},
) {
	val isBackgroundColorLight = cardType.backgroundColor.luminance() > 0.5f

	Card(
		modifier = modifier
			.height(IntrinsicSize.Min)
			.fillMaxWidth()
			.clip(MaterialTheme.shapes.extraLarge)
			.clickable(onClick = onCardClick),
		shape = MaterialTheme.shapes.extraLarge,
		colors = CardDefaults.cardColors(
			containerColor = if (cardType == InfoCardType.Default) MaterialTheme.colorScheme.surfaceContainer else cardType.backgroundColor,
			contentColor = if (isBackgroundColorLight || cardType == InfoCardType.Default) Color.Black else Color.White,
		)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Icon(
					modifier = Modifier.size(24.dp),
					imageVector = icon,
					contentDescription = null,
				)
				Text(
					text = title,
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.weight(1f),
					fontWeight = FontWeight.Bold
				)
			}
			Text(
				modifier = Modifier.fillMaxWidth(),
				text = body,
				style = MaterialTheme.typography.bodyMedium,
				textAlign = TextAlign.Justify,
			)
			bottomContent()
		}
	}
}
