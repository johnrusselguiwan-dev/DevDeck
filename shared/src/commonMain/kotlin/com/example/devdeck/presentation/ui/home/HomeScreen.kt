package com.example.devdeck.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devdeck.presentation.common.theme.DevDeckTheme
import com.example.devdeck.presentation.common.theme.ThemeMode
import com.example.devdeck.presentation.ui.components.ContactSection
import com.example.devdeck.presentation.ui.components.ExperienceTimeline
import com.example.devdeck.presentation.ui.components.HeroHeader
import com.example.devdeck.presentation.ui.components.KeyHighlightsBanner
import com.example.devdeck.presentation.ui.components.ProjectsShowcase
import com.example.devdeck.presentation.ui.components.SkillsSection
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main entry composable for the DevDeck portfolio screen.
 *
 * Organizes content into distinct screens (Overview, Projects, Skills, Experience)
 * navigable via a Bottom Navigation Bar and provides Appearance settings (Theme) via an overflow menu.
 *
 * @param modifier Optional [Modifier] for layout customization.
 * @param viewModel [HomeViewModel] instance managing screen UI state and actions.
 * @param onOpenUrl Callback invoked when an external link or URL is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onOpenUrl: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val themeMode = (uiState as? HomeUiState.Success)?.themeMode ?: ThemeMode.SYSTEM

    DevDeckTheme(themeMode = themeMode) {
        var showMenu by remember { mutableStateOf(false) }

        Scaffold(
            modifier = modifier,
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
                                text = "• Portfolio",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options Menu",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Appearance",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {},
                                    enabled = false
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                                ThemeMode.entries.forEach { mode ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = mode.displayName,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = if (mode == themeMode) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    },
                                                    fontWeight = if (mode == themeMode) FontWeight.Bold else FontWeight.Normal
                                                )
                                                if (mode == themeMode) {
                                                    Text(
                                                        text = "✓",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            viewModel.setThemeMode(mode)
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                val state = uiState
                if (state is HomeUiState.Success) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationTab.entries.forEach { tab ->
                            val selected = state.selectedTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(tab) },
                                icon = {
                                    val icon = when (tab) {
                                        NavigationTab.OVERVIEW -> Icons.Default.Person
                                        NavigationTab.PROJECTS -> Icons.AutoMirrored.Filled.List
                                        NavigationTab.SKILLS -> Icons.Default.Build
                                        NavigationTab.EXPERIENCE -> Icons.Default.DateRange
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = tab.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
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
                        key(state.selectedTab) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                            ) {
                                when (state.selectedTab) {
                                    NavigationTab.OVERVIEW -> {
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

                                    NavigationTab.PROJECTS -> {
                                        ProjectsShowcase(
                                            projects = state.projects,
                                            onOpenUrl = onOpenUrl
                                        )
                                    }

                                    NavigationTab.SKILLS -> {
                                        SkillsSection(
                                            skills = state.skills,
                                            selectedCategory = state.selectedSkillCategory,
                                            onCategorySelected = { cat -> viewModel.filterSkillsByCategory(cat) }
                                        )
                                    }

                                    NavigationTab.EXPERIENCE -> {
                                        ExperienceTimeline(
                                            experiences = state.experiences
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
