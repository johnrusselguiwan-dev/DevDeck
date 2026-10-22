package com.example.devdeck.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
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
    val groupedSkills = skills.groupBy { it.category }
    val categories = SkillCategory.entries.filter { selectedCategory == null || it == selectedCategory }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Skills",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columns = if (maxWidth >= 720.dp && categories.size > 1) 2 else 1
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                categories.chunked(columns).forEach { rowCategories ->
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        rowCategories.forEach { category ->
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                                IOSMonochromeCard {
                                    val categorySkills = groupedSkills[category].orEmpty()
                                    categorySkills.forEachIndexed { index, skill ->
                                        SkillRow(skill = skill)
                                        if (index < categorySkills.lastIndex) {
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }
                        }
                        if (rowCategories.size < columns) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillRow(skill: Skill) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = skill.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = skill.specialty,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = skill.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
