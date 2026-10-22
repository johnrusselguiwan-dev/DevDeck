package com.example.devdeck.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.PortfolioResource
import com.example.devdeck.domain.model.SocialLink
import com.example.devdeck.getPlatform

@Composable
fun ResumeButton(
    profile: DeveloperProfile,
    onOpenUrl: (String) -> Unit,
    onResourceClick: (PortfolioResource) -> Unit,
    modifier: Modifier = Modifier
) {
    profile.resumeUrl?.let { url ->
        OutlinedButton(
            onClick = {
                onResourceClick(PortfolioResource.RESUME)
                onOpenUrl(url)
            },
            modifier = modifier
        ) {
            Text("View resume (PDF)")
        }
    }
}

@Composable
fun ContactSection(
    profile: DeveloperProfile,
    socialLinks: List<SocialLink>,
    onOpenUrl: (String) -> Unit,
    onResourceClick: (PortfolioResource) -> Unit,
    modifier: Modifier = Modifier
) {
    val isWeb = getPlatform().isWeb
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Contact",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        IOSMonochromeCard {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth >= 680.dp) {
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        ContactSocialLinks(socialLinks, onOpenUrl, Modifier.weight(1f))
                        ContactResources(profile, isWeb, onOpenUrl, onResourceClick, Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ContactSocialLinks(socialLinks, onOpenUrl)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        ContactResources(profile, isWeb, onOpenUrl, onResourceClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactSocialLinks(
    socialLinks: List<SocialLink>,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        socialLinks.forEach { link ->
            ContactRow(
                icon = getSocialIcon(link.platform),
                label = link.platform,
                value = link.url.removePrefix("https://").removePrefix("http://")
                    .removePrefix("mailto:").removePrefix("tel:").trimEnd('/'),
                onClick = { onOpenUrl(link.url) }
            )
        }
    }
}

@Composable
private fun ContactResources(
    profile: DeveloperProfile,
    isWeb: Boolean,
    onOpenUrl: (String) -> Unit,
    onResourceClick: (PortfolioResource) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ContactRow(icon = Icons.Default.LocationOn, label = "Location", value = profile.location)
        PortfolioResourceLink(
            icon = Icons.Default.Description,
            label = "Resume",
            resource = PortfolioResource.RESUME,
            url = profile.resumeUrl,
            onOpenUrl = onOpenUrl,
            onResourceClick = onResourceClick
        )
        val companionUrl = if (isWeb) profile.androidDownloadUrl else profile.websiteUrl
        companionUrl?.let { url ->
            PortfolioResourceLink(
                icon = if (isWeb) Icons.Default.Android else Icons.Default.Language,
                label = if (isWeb) "Android app" else "Website",
                resource = if (isWeb) PortfolioResource.ANDROID_APP else PortfolioResource.WEBSITE,
                url = url,
                onOpenUrl = onOpenUrl,
                onResourceClick = onResourceClick
            )
        }
    }
}

@Composable
fun SocialLinksRow(
    socialLinks: List<SocialLink>,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    FlowRowLayout(modifier = modifier.clipToBounds(), horizontalSpacing = 0.dp, verticalSpacing = 8.dp) {
        socialLinks.forEachIndexed { index, link ->
            val delay = (if (visible) index else socialLinks.lastIndex - index) * 70
            Box(modifier = Modifier.size(48.dp)) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(240, delay)) + slideInHorizontally(tween(240, delay)) { it },
                    exit = fadeOut(tween(240, delay)) + slideOutHorizontally(tween(240, delay)) { it }
                ) {
                    IconButton(
                        onClick = { onOpenUrl(link.url) },
                        enabled = visible,
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = getSocialIcon(link.platform),
                            contentDescription = link.platform,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(icon: ImageVector, label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp).clip(RoundedCornerShape(12.dp))
            .then(if (onClick == null) Modifier else Modifier.clickable(role = Role.Button, onClick = onClick))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (onClick != null) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PortfolioResourceLink(
    icon: ImageVector,
    label: String,
    resource: PortfolioResource,
    url: String?,
    onOpenUrl: (String) -> Unit,
    onResourceClick: (PortfolioResource) -> Unit
) {
    ContactRow(
        icon = icon,
        label = label,
        value = when {
            url == null -> "Link coming soon"
            resource == PortfolioResource.RESUME -> "View resume"
            resource == PortfolioResource.WEBSITE -> "Open portfolio"
            else -> "Download APK"
        },
        onClick = url?.let {
            {
                onResourceClick(resource)
                onOpenUrl(url)
            }
        }
    )
}
