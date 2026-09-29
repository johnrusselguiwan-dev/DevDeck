package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SkillCategory

@Composable
fun SkillsSection(
    skills: List<Skill>,
    selectedCategory: SkillCategory?,
    onCategorySelected: (SkillCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredSkills = if (selectedCategory != null) {
        skills.filter { it.category == selectedCategory }
    } else {
        skills
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Categorized Technical Skills Matrix",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        // iOS Segmented Control Pill Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(6.dp)
        ) {
            FlowRowLayout(
                horizontalSpacing = 6.dp,
                verticalSpacing = 6.dp
            ) {
                IOSSegmentedPill(
                    label = "All Skills",
                    isSelected = selectedCategory == null,
                    onClick = { onCategorySelected(null) }
                )

                SkillCategory.entries.forEach { cat ->
                    IOSSegmentedPill(
                        label = cat.displayName,
                        isSelected = selectedCategory == cat,
                        onClick = { onCategorySelected(cat) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skills Grid Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            filteredSkills.forEach { skill ->
                IOSSkillCard(skill = skill)
            }
        }
    }
}

@Composable
private fun IOSSegmentedPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primaryContainer
    val fg = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = fg,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun IOSSkillCard(
    skill: Skill,
    modifier: Modifier = Modifier
) {
    IOSMonochromeCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = skill.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            TechBadge(text = skill.category.displayName)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = skill.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LinearProgressIndicator(
                progress = { skill.level / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.onSurface,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )

            Text(
                text = "${skill.level}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
