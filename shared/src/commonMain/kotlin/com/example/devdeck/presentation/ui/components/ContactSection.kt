package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devdeck.domain.model.SocialLink
import com.example.devdeck.presentation.common.WindowWidthSizeClass

@Composable
fun ContactSection(
    socialLinks: List<SocialLink>,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.COMPACT
) {
    val useGrid = windowSizeClass != WindowWidthSizeClass.COMPACT

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Socials & Professional Links",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (useGrid) {
            // 2-column grid for wider screens
            val chunked = socialLinks.chunked(2)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                chunked.forEach { rowLinks ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowLinks.forEach { link ->
                            IOSMonochromeCard(
                                modifier = Modifier.weight(1f)
                            ) {
                                SocialLinkItem(
                                    socialLink = link,
                                    onClick = { onOpenUrl(link.url) }
                                )
                            }
                        }
                        if (rowLinks.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // Single grouped card on mobile
            IOSMonochromeCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    socialLinks.forEach { link ->
                        SocialLinkItem(
                            socialLink = link,
                            onClick = { onOpenUrl(link.url) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialLinkItem(
    socialLink: SocialLink,
    onClick: () -> Unit
) {
    val icon = getSocialIcon(socialLink.platform)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = socialLink.platform,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
