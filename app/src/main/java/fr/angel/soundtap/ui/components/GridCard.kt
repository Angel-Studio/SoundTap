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

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.GridCard(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Card(
        modifier =
        modifier
            .aspectRatio(1f)
            .sharedElement(
                state =
                rememberSharedContentState(
                    key = "$label-card",
                ),
                animatedVisibilityScope = animatedVisibilityScope,
            ),
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick,
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Icon(
                modifier =
                Modifier
                    .size(64.dp)
                    .sharedElement(
                        state =
                        rememberSharedContentState(
                            key = "$label-icon",
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
                imageVector = icon,
                contentDescription = null,
            )

            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .sharedBounds(
                        rememberSharedContentState(
                            key = label,
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
            )
        }
    }
}
