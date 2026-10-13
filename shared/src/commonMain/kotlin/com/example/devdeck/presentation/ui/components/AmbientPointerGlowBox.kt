package com.example.devdeck.presentation.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Organic Water Light Ambient Overlay.
 *
 * Casts an ultra-faint (alpha = 0.025 - 0.035), smooth flowing organic water-reflection light
 * that gracefully follows user cursor or touch movements across platforms.
 */
@Composable
fun AmbientPointerGlowBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var rawPointerOffset by remember { mutableStateOf<Offset?>(null) }

    // Smooth fluid liquid movement easing
    val animatedOffset by animateOffsetAsState(
        targetValue = rawPointerOffset ?: Offset.Zero,
        animationSpec = tween(durationMillis = 700, easing = LinearOutSlowInEasing),
        label = "waterLightAnimation"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val containerColor = MaterialTheme.colorScheme.primaryContainer

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val position = event.changes.firstOrNull()?.position
                        if (position != null) {
                            rawPointerOffset = position
                        }
                    }
                }
            }
            .drawWithContent {
                drawContent()

                if (rawPointerOffset != null) {
                    // Primary organic water sheen layer (ultra-faint 3.5% opacity)
                    withTransform({
                        rotate(degrees = 25f, pivot = animatedOffset)
                    }) {
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.035f),
                                    containerColor.copy(alpha = 0.015f),
                                    Color.Transparent
                                ),
                                center = animatedOffset,
                                radius = 550f
                            ),
                            topLeft = Offset(
                                x = animatedOffset.x - 400f,
                                y = animatedOffset.y - 220f
                            ),
                            size = Size(800f, 440f)
                        )
                    }

                    // Secondary offset liquid ripple layer for organic depth
                    withTransform({
                        rotate(degrees = -15f, pivot = animatedOffset)
                    }) {
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    containerColor.copy(alpha = 0.025f),
                                    Color.Transparent
                                ),
                                center = animatedOffset,
                                radius = 400f
                            ),
                            topLeft = Offset(
                                x = animatedOffset.x - 250f,
                                y = animatedOffset.y - 180f
                            ),
                            size = Size(500f, 360f)
                        )
                    }
                }
            },
        content = content
    )
}
