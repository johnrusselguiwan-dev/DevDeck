package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SkillCategory

/**
 * Modern, Apple/Stripe-inspired Grouped Skills Section using vector icons.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsSection(
    skills: List<Skill>,
    selectedCategory: SkillCategory?,
    onCategorySelected: (SkillCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedSkills = skills.groupBy { it.category }
    val categories = SkillCategory.entries.filter { selectedCategory == null || it == selectedCategory }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Technical Competencies & Skills",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        FlowRowLayout(horizontalSpacing = 8.dp, verticalSpacing = 4.dp) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") }
            )
            SkillCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.displayName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        IOSMonochromeCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                categories.forEachIndexed { index, category ->
                    val categorySkills = groupedSkills[category] ?: emptyList()
                    val categoryIcon: ImageVector = when (category) {
                        SkillCategory.MOBILE_DEV -> Icons.Default.Phone
                        SkillCategory.CROSS_PLATFORM -> Icons.Default.Share
                        SkillCategory.ARCHITECTURE -> Icons.AutoMirrored.Filled.List
                        SkillCategory.DEVOPS -> Icons.Default.Build
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FlowRowLayout(horizontalSpacing = 12.dp, verticalSpacing = 8.dp) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            TechBadge(text = "${categorySkills.size} Competencies")
                        }

                        // Flow Row of Skill Badges
                        FlowRowLayout(
                            horizontalSpacing = 8.dp,
                            verticalSpacing = 8.dp
                        ) {
                            categorySkills.forEach { skill ->
                                TechBadge(text = skill.name)
                            }
                        }
                    }

                    if (index < categories.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}
