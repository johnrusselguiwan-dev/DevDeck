package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devdeck.presentation.common.WindowWidthSizeClass

data class HighlightItem(
    val icon: ImageVector,
    val text: String
)

@Composable
fun KeyHighlightsBanner(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.COMPACT
) {
    val highlights = listOf(
        HighlightItem(Icons.Default.Code, "Java & Native Kotlin"),
        HighlightItem(Icons.Default.Build, "Jetpack Compose UI"),
        HighlightItem(Icons.AutoMirrored.Filled.List, "Multi-Module Architecture"),
        HighlightItem(Icons.Default.Share, "KMP / CMP Hands-on Practice")
    )

    IOSMonochromeCard(
        modifier = modifier.fillMaxWidth()
    ) {
        FlowRowLayout(
            modifier = Modifier.fillMaxWidth(),
            horizontalSpacing = 10.dp,
            verticalSpacing = 10.dp
        ) {
            highlights.forEach { item ->
                HighlightChip(icon = item.icon, text = item.text)
            }
        }
    }
}

@Composable
private fun HighlightChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
