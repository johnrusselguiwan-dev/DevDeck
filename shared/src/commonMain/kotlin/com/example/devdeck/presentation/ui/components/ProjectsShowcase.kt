package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devdeck.domain.model.Project

@Composable
fun ProjectsShowcase(
    projects: List<Project>,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val useGrid = maxWidth >= 720.dp
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Featured Projects Showcase",
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (useGrid) {
                val hasSingleTop = projects.size % 2 != 0
                val topProject = if (hasSingleTop) projects.firstOrNull() else null
                val remainingProjects = if (hasSingleTop) projects.drop(1) else projects

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Unique single long card occupies full 2-column width across the TOP
                    topProject?.let { project ->
                        ProjectCard(
                            project = project,
                            onOpenUrl = onOpenUrl,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Pairs below rendered in equal-weight 2-column rows with identical card height
                    remainingProjects.chunked(2).forEach { rowProjects ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)
                        ) {
                            rowProjects.forEach { project ->
                                ProjectCard(
                                    project = project,
                                    onOpenUrl = onOpenUrl,
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            } else {
                // Single Column Mode: For Mobile, Tablet, or resized Web browser windows
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    projects.forEach { project ->
                        ProjectCard(
                            project = project,
                            onOpenUrl = onOpenUrl,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProjectCard(
    project: Project,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    IOSMonochromeCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Section: Title, Subtitle & Description
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = project.subtitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (project.isFeatured) {
                        TechBadge(text = "FEATURED")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Section: Anchored Tech Stack & Play Store / GitHub CTA Buttons at the VERY BOTTOM!
            Column {
                FlowRowLayout(
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp
                ) {
                    project.techBadges.forEach { badge ->
                        TechBadge(text = badge)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    project.playStoreUrl?.let { playUrl ->
                        Button(
                            onClick = { onOpenUrl(playUrl) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSurface,
                                contentColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Play Store",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    project.githubUrl?.let { gitUrl ->
                        OutlinedButton(
                            onClick = { onOpenUrl(gitUrl) },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "GitHub Repo",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
