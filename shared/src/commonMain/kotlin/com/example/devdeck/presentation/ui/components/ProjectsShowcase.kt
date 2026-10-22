package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
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
                text = "Projects",
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            val featured = projects.filter { it.isFeatured }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                featured.firstOrNull()?.let { project ->
                    ProjectCard(project, onOpenUrl, Modifier.fillMaxWidth())
                }
                featured.drop(1).chunked(if (useGrid) 2 else 1).forEach { rowProjects ->
                    if (rowProjects.size == 1) {
                        ProjectCard(rowProjects.single(), onOpenUrl, Modifier.fillMaxWidth())
                    } else {
                        ProjectRow(projects = rowProjects, onOpenUrl = onOpenUrl)
                    }
                }
            }
            val otherProjects = projects.filterNot { it.isFeatured }
            if (otherProjects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                OtherProjects(projects = otherProjects, onOpenUrl = onOpenUrl)
            }
        }
    }
}

@Composable
private fun ProjectRow(projects: List<Project>, onOpenUrl: (String) -> Unit) {
    SubcomposeLayout(modifier = Modifier.fillMaxWidth()) { constraints ->
        val gap = 16.dp.roundToPx()
        val cardWidth = (constraints.maxWidth - gap * (projects.size - 1)) / projects.size
        // Wrapped pill rows need real measurements; intrinsic heights can clip the action buttons.
        val naturalHeights = projects.map { project ->
            subcompose("measure-${project.id}") {
                ProjectCard(project = project, onOpenUrl = onOpenUrl, modifier = Modifier.clearAndSetSemantics {})
            }.single().measure(Constraints.fixedWidth(cardWidth)).height
        }
        val rowHeight = naturalHeights.max()
        val cards = projects.map { project ->
            subcompose(project.id) {
                ProjectCard(project = project, onOpenUrl = onOpenUrl)
            }.single().measure(Constraints.fixed(cardWidth, rowHeight))
        }
        layout(constraints.maxWidth, rowHeight) {
            cards.forEachIndexed { index, card -> card.placeRelative(index * (cardWidth + gap), 0) }
        }
    }
}

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
                            fontWeight = FontWeight.SemiBold,
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
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (project.contributions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "My contribution",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        project.contributions.forEach { contribution ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = contribution,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))

            Column {
                FlowRowLayout(
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp
                ) {
                    project.techBadges.forEach { badge ->
                        TechBadge(text = badge)
                    }
                }

                if (project.playStoreUrl != null || project.githubUrl != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                ProjectActions(project = project, onOpenUrl = onOpenUrl)
            }
        }
    }
}

@Composable
private fun OtherProjects(projects: List<Project>, onOpenUrl: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Other languages & tools",
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Additional experience through scripts, web and desktop projects.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columns = when {
                maxWidth >= 900.dp -> 3
                maxWidth >= 560.dp -> 2
                else -> 1
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                projects.chunked(columns).forEach { rowProjects ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProjects.forEach { project ->
                            OtherProjectCard(project, onOpenUrl, Modifier.weight(1f).fillMaxHeight())
                        }
                        repeat(columns - rowProjects.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OtherProjectCard(project: Project, onOpenUrl: (String) -> Unit, modifier: Modifier = Modifier) {
    IOSMonochromeCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                project.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                project.techBadges.joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            ProjectActions(project = project, onOpenUrl = onOpenUrl)
        }
    }
}

@Composable
private fun ProjectActions(project: Project, onOpenUrl: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        project.playStoreUrl?.let { playUrl ->
            Button(
                onClick = { onOpenUrl(playUrl) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text(
                    text = "Play Store",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }

        project.githubUrl?.let { gitUrl ->
            OutlinedButton(
                onClick = { onOpenUrl(gitUrl) },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text(
                    text = "View source",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }
    }
}
