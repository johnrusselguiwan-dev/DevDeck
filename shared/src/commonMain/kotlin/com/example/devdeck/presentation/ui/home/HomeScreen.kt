package com.example.devdeck.presentation.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devdeck.presentation.common.theme.DevDeckTheme
import com.example.devdeck.presentation.ui.components.ContactSection
import com.example.devdeck.presentation.ui.components.ExperienceTimeline
import com.example.devdeck.presentation.ui.components.HeroHeader
import com.example.devdeck.presentation.ui.components.KeyHighlightsBanner
import com.example.devdeck.presentation.ui.components.ProjectsShowcase
import com.example.devdeck.presentation.ui.components.SkillsSection
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onOpenUrl: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val isDark = when (val state = uiState) {
        is HomeUiState.Success -> state.isDarkMode
        else -> true
    }

    DevDeckTheme(darkTheme = isDark) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "DevDeck",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "• KMP Portfolio",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = if (isDark) "🌙 Dark" else "☀️ Light",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = isDark,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    is HomeUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is HomeUiState.Success -> {
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val isWideScreen = maxWidth > 840.dp

                            if (isWideScreen) {
                                // Wide Screen (Desktop/Web) Dual Column Layout
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(24.dp),
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    // Left Column: Hero & Highlights Banner & Contact
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        HeroHeader(
                                            profile = state.profile,
                                            socialLinks = state.socialLinks,
                                            onOpenUrl = onOpenUrl
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        KeyHighlightsBanner()

                                        Spacer(modifier = Modifier.height(20.dp))

                                        ContactSection(
                                            socialLinks = state.socialLinks,
                                            onOpenUrl = onOpenUrl
                                        )
                                    }

                                    // Right Column: Projects, Skills Matrix, Experience Timeline
                                    Column(
                                        modifier = Modifier.weight(1.2f)
                                    ) {
                                        ProjectsShowcase(
                                            projects = state.projects,
                                            onOpenUrl = onOpenUrl
                                        )

                                        Spacer(modifier = Modifier.height(24.dp))

                                        SkillsSection(
                                            skills = state.skills,
                                            selectedCategory = state.selectedSkillCategory,
                                            onCategorySelected = { cat -> viewModel.filterSkillsByCategory(cat) }
                                        )

                                        Spacer(modifier = Modifier.height(24.dp))

                                        ExperienceTimeline(experiences = state.experiences)
                                    }
                                }
                            } else {
                                // Mobile / Tablet Single Column Sequential Layout
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 16.dp, vertical = 16.dp)
                                ) {
                                    // 1. Hero Section (First Fold)
                                    HeroHeader(
                                        profile = state.profile,
                                        socialLinks = state.socialLinks,
                                        onOpenUrl = onOpenUrl
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // 2. Key Highlights Banner (Quick Scanning Strip)
                                    KeyHighlightsBanner()

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // 3. Featured Projects Showcase (Proof of Work)
                                    ProjectsShowcase(
                                        projects = state.projects,
                                        onOpenUrl = onOpenUrl
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // 4. Categorized Technical Skills Matrix
                                    SkillsSection(
                                        skills = state.skills,
                                        selectedCategory = state.selectedSkillCategory,
                                        onCategorySelected = { cat -> viewModel.filterSkillsByCategory(cat) }
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // 5. Experience Timeline
                                    ExperienceTimeline(experiences = state.experiences)

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // 6. Footer Contact CTA
                                    ContactSection(
                                        socialLinks = state.socialLinks,
                                        onOpenUrl = onOpenUrl
                                    )

                                    Spacer(modifier = Modifier.height(40.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
