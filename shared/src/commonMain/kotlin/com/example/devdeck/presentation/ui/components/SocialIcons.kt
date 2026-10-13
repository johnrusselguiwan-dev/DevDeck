package com.example.devdeck.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GitHubIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "GitHub",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(12f, 0f)
            curveTo(5.37f, 0f, 0f, 5.37f, 0f, 12f)
            curveTo(0f, 17.31f, 3.435f, 21.795f, 8.205f, 23.385f)
            curveTo(8.805f, 23.49f, 9.03f, 23.13f, 9.03f, 22.815f)
            curveTo(9.03f, 22.53f, 9.015f, 21.585f, 9.015f, 20.58f)
            curveTo(5.7f, 22.135f, 4.92f, 20.845f, 4.68f, 20.17f)
            curveTo(4.545f, 19.825f, 3.96f, 18.76f, 3.45f, 18.475f)
            curveTo(3.03f, 18.25f, 2.43f, 17.695f, 3.435f, 17.68f)
            curveTo(4.38f, 17.665f, 5.055f, 18.555f, 5.28f, 18.915f)
            curveTo(6.36f, 20.73f, 8.085f, 20.22f, 8.775f, 19.905f)
            curveTo(8.88f, 19.125f, 9.195f, 18.6f, 9.54f, 18.3f)
            curveTo(6.87f, 18f, 4.08f, 16.965f, 4.08f, 12.375f)
            curveTo(4.08f, 11.07f, 4.545f, 9.99f, 5.31f, 9.15f)
            curveTo(5.19f, 8.85f, 4.77f, 7.62f, 5.43f, 5.97f)
            curveTo(5.43f, 5.97f, 6.435f, 5.655f, 8.73f, 7.215f)
            curveTo(9.69f, 6.945f, 10.71f, 6.81f, 11.73f, 6.81f)
            curveTo(12.75f, 6.81f, 13.77f, 6.945f, 14.73f, 7.215f)
            curveTo(17.025f, 5.655f, 18.03f, 5.97f, 18.03f, 5.97f)
            curveTo(18.69f, 7.62f, 18.27f, 8.85f, 18.15f, 9.15f)
            curveTo(18.915f, 9.99f, 19.38f, 11.055f, 19.38f, 12.375f)
            curveTo(19.38f, 16.98f, 16.575f, 18f, 13.905f, 18.3f)
            curveTo(14.34f, 18.675f, 14.715f, 19.395f, 14.715f, 20.52f)
            curveTo(14.715f, 22.125f, 14.7f, 23.415f, 14.7f, 23.82f)
            curveTo(14.7f, 24.135f, 14.925f, 24.51f, 15.525f, 24.39f)
            curveTo(20.295f, 22.795f, 23.73f, 18.315f, 23.73f, 13f)
            curveTo(23.73f, 6.37f, 18.36f, 0f, 12f, 0f)
            close()
        }
    }.build()
}

val LinkedInIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "LinkedIn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(19f, 0f)
            lineTo(5f, 0f)
            curveTo(2.239f, 0f, 0f, 2.239f, 0f, 5f)
            lineTo(0f, 19f)
            curveTo(0f, 21.761f, 2.239f, 24f, 5f, 24f)
            lineTo(19f, 24f)
            curveTo(21.762f, 24f, 24f, 21.761f, 24f, 19f)
            lineTo(24f, 5f)
            curveTo(24f, 2.239f, 21.762f, 0f, 19f, 0f)
            close()
            moveTo(8f, 19f)
            lineTo(5f, 19f)
            lineTo(5f, 8f)
            lineTo(8f, 8f)
            lineTo(8f, 19f)
            close()
            moveTo(6.5f, 6.732f)
            curveTo(5.534f, 6.732f, 4.75f, 5.942f, 4.75f, 4.968f)
            curveTo(4.75f, 3.994f, 5.534f, 3.204f, 6.5f, 3.204f)
            curveTo(7.466f, 3.204f, 8.25f, 3.994f, 8.25f, 4.968f)
            curveTo(8.25f, 5.942f, 7.466f, 6.732f, 6.5f, 6.732f)
            close()
            moveTo(20f, 19f)
            lineTo(17f, 19f)
            lineTo(17f, 13.396f)
            curveTo(17f, 10.028f, 13f, 10.283f, 13f, 13.396f)
            lineTo(13f, 19f)
            lineTo(10f, 19f)
            lineTo(10f, 8f)
            lineTo(13f, 8f)
            lineTo(13f, 9.765f)
            curveTo(14.396f, 7.179f, 20f, 6.988f, 20f, 12.223f)
            lineTo(20f, 19f)
            close()
        }
    }.build()
}

fun getSocialIcon(platform: String): ImageVector {
    return when {
        platform.contains("GitHub", ignoreCase = true) -> GitHubIcon
        platform.contains("LinkedIn", ignoreCase = true) -> LinkedInIcon
        platform.contains("Email", ignoreCase = true) -> Icons.Default.Email
        platform.contains("Phone", ignoreCase = true) -> Icons.Default.Phone
        else -> Icons.Default.Share
    }
}
