package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun KeyHighlightsBanner(
    modifier: Modifier = Modifier
) {
    val highlights = listOf(
        "☕ Java & Native Kotlin",
        "🎨 Jetpack Compose UI",
        "🏗️ Multi-Module Architecture",
        "🧪 KMP / CMP Hands-on Practice"
    )

    IOSMonochromeCard(
        modifier = modifier.fillMaxWidth()
    ) {
        FlowRowLayout(
            modifier = Modifier.fillMaxWidth(),
            horizontalSpacing = 10.dp,
            verticalSpacing = 10.dp
        ) {
            highlights.forEach { text ->
                HighlightChip(text = text)
            }
        }
    }
}

@Composable
private fun HighlightChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
