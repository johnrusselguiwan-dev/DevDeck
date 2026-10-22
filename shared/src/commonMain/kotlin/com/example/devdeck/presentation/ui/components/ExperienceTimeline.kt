package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devdeck.domain.model.Experience
import com.example.devdeck.presentation.common.WindowWidthSizeClass

/**
 * Vertical Connected Timeline Component for Experience & Career Progression.
 *
 * Displays a continuous connected timeline (dots + line connector) across all screen sizes (Web, Mobile, Desktop),
 * clearly illustrating progression from Student/Academic Foundation to Software Developer Career.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExperienceTimeline(
    experiences: List<Experience>,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.COMPACT
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Experience & Education",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            experiences.forEachIndexed { index, experience ->
                ExperienceTimelineItem(experience = experience, isLast = index == experiences.lastIndex)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExperienceTimelineItem(
    experience: Experience,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Vertical Timeline Connector Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 20.dp, top = 4.dp)
        ) {
            // Timeline Node Dot
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (experience.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )

            // Timeline Connecting Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                )
            }
        }

        // Timeline Card Content
        IOSMonochromeCard(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 24.dp)
        ) {
            ExperienceCardContent(experience = experience)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExperienceCardContent(
    experience: Experience,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section: Role, Company, Period, Summary & Highlights
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = experience.role,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = experience.company,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = experience.period,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = experience.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bullet highlights
            experience.highlights.forEach { highlight ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = highlight,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Column {
            Spacer(modifier = Modifier.height(16.dp))

            FlowRowLayout(
                horizontalSpacing = 6.dp,
                verticalSpacing = 6.dp
            ) {
                experience.techStack.forEach { tech ->
                    TechBadge(text = tech)
                }
            }
        }
    }
}
