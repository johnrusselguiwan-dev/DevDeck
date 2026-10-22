package com.example.devdeck.presentation.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devdeck.domain.model.PortfolioResource
import com.example.devdeck.presentation.common.WindowWidthSizeClass
import com.example.devdeck.presentation.common.calculateWindowWidthSizeClass
import com.example.devdeck.presentation.common.theme.DevDeckTheme
import com.example.devdeck.presentation.common.theme.ThemeMode
import com.example.devdeck.presentation.ui.components.ContactSection
import com.example.devdeck.presentation.ui.components.ExperienceTimeline
import com.example.devdeck.presentation.ui.components.HeroHeader
import com.example.devdeck.presentation.ui.components.ProjectsShowcase
import com.example.devdeck.presentation.ui.components.ResumeButton
import com.example.devdeck.presentation.ui.components.SkillsSection
import com.example.devdeck.presentation.ui.components.SocialLinksRow
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * iOS/macOS-Inspired Single-Page Portfolio Layout for DevDeck.
 *
 * Employs Cupertino/Apple design aesthetics:
 * - Inset grouped surface containers with soft rounded corners (20dp-24dp)
 * - Translucent frosted top/bottom bars with blur/alpha
 * - Smooth top & bottom fading edges for scrollable feeds
 * - macOS/iPadOS-style sidebar navigation menu with animated selection pills
 * - Adaptive layout switching between Desktop Split-View (macOS style) and Mobile Feed (iOS style)
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onOpenUrl: (String) -> Unit = {},
    onResourceClick: (PortfolioResource) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val themeMode = uiState.themeMode
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val activeSection by remember(listState) {
        derivedStateOf {
            val layout = listState.layoutInfo
            activePortfolioSection(
                items = layout.visibleItemsInfo.map { SectionBounds(it.index, it.offset, it.size) },
                viewportStart = layout.viewportStartOffset,
                viewportEnd = layout.viewportEndOffset,
                atStart = !listState.canScrollBackward,
                atEnd = !listState.canScrollForward
            )
        }
    }
    val scrollToSection: (PortfolioSection) -> Unit = { section ->
        coroutineScope.launch { listState.animateScrollToItem(section.index) }
    }

    DevDeckTheme(themeMode = themeMode) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val windowSizeClass = calculateWindowWidthSizeClass(maxWidth)
            val isCompact = windowSizeClass == WindowWidthSizeClass.COMPACT

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = viewModel::loadPortfolioData) {
                            Text("Retry")
                        }
                    }
                }

                is HomeUiState.Success -> {
                    if (isCompact) {
                        IosStyleMobileLayout(
                            state = state,
                            viewModel = viewModel,
                            onOpenUrl = onOpenUrl,
                            onResourceClick = onResourceClick,
                            themeMode = themeMode,
                            listState = listState,
                            activeSection = activeSection,
                            onSectionSelected = scrollToSection
                        )
                    } else {
                        MacOsStyleSplitLayout(
                            state = state,
                            viewModel = viewModel,
                            onOpenUrl = onOpenUrl,
                            onResourceClick = onResourceClick,
                            themeMode = themeMode,
                            listState = listState,
                            activeSection = activeSection,
                            onSectionSelected = scrollToSection
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
//  macOS / iPadOS Style Split Layout (Desktop & Web)
// ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacOsStyleSplitLayout(
    state: HomeUiState.Success,
    viewModel: HomeViewModel,
    onOpenUrl: (String) -> Unit,
    onResourceClick: (PortfolioResource) -> Unit,
    themeMode: ThemeMode,
    listState: LazyListState,
    activeSection: PortfolioSection,
    onSectionSelected: (PortfolioSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val contactIsVisible by remember(listState) {
        derivedStateOf {
            val layout = listState.layoutInfo
            layout.visibleItemsInfo.any { item ->
                item.index == PortfolioSection.CONTACT.index &&
                    item.offset < layout.viewportEndOffset &&
                    item.offset + item.size > layout.viewportStartOffset
            }
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            val sidebarWidth = when {
                maxWidth >= 1200.dp -> 360.dp
                maxWidth >= 900.dp -> 320.dp
                else -> 240.dp
            }
            Row(
                modifier = Modifier
                    .widthIn(max = 1440.dp)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .width(sidebarWidth)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Top Bar Header & Theme Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DevDeck",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                ThemeDropdownButton(
                                    currentTheme = themeMode,
                                    onThemeSelected = { viewModel.setThemeMode(it) }
                                )
                            }

                            // Hero Profile Summary
                            Column {
                                Text(
                                    text = state.profile.name,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = state.profile.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "${state.profile.location}\n${state.profile.yearsExperience}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                ResumeButton(
                                    profile = state.profile,
                                    onOpenUrl = onOpenUrl,
                                    onResourceClick = onResourceClick
                                )
                            }

                            // macOS / iOS Style Sidebar Navigation Menu
                            IOSSidebarNavMenu(
                                activeSection = activeSection,
                                onSectionSelected = onSectionSelected,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        SocialLinksRow(
                            socialLinks = state.socialLinks,
                            onOpenUrl = onOpenUrl,
                            visible = !contactIsVisible
                        )
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    val windowSizeClass = calculateWindowWidthSizeClass(maxWidth)
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(48.dp)
                    ) {
                        // Section 0: About & Key Highlights
                        item(key = PortfolioSection.ABOUT.name) {
                            AboutSummaryCard(bio = state.profile.bio)
                        }

                        // Section 1: Experience & Career Timeline
                        item(key = PortfolioSection.EXPERIENCE.name) {
                            ExperienceTimeline(
                                experiences = state.experiences,
                                windowSizeClass = windowSizeClass
                            )
                        }

                        // Section 2: Projects Showcase
                        item(key = PortfolioSection.PROJECTS.name) {
                            ProjectsShowcase(
                                projects = state.projects,
                                onOpenUrl = onOpenUrl
                            )
                        }

                        // Section 3: Technical Skills Matrix
                        item(key = PortfolioSection.SKILLS.name) {
                            SkillsSection(
                                skills = state.skills,
                                selectedCategory = state.selectedSkillCategory,
                                onCategorySelected = { cat -> viewModel.filterSkillsByCategory(cat) }
                            )
                        }

                        // Section 4: Contact & Professional Links
                        item(key = PortfolioSection.CONTACT.name) {
                            ContactSection(
                                profile = state.profile,
                                socialLinks = state.socialLinks,
                                onOpenUrl = onOpenUrl,
                                onResourceClick = onResourceClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutSummaryCard(
    bio: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Kotlin & Jetpack Compose",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Building for Android.",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = bio,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─────────────────────────────────────────────────────────
//  iOS Style Mobile Layout (Single Column Feed)
// ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IosStyleMobileLayout(
    state: HomeUiState.Success,
    viewModel: HomeViewModel,
    onOpenUrl: (String) -> Unit,
    onResourceClick: (PortfolioResource) -> Unit,
    themeMode: ThemeMode,
    listState: LazyListState,
    activeSection: PortfolioSection,
    onSectionSelected: (PortfolioSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "DevDeck",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    ThemeDropdownButton(
                        currentTheme = themeMode,
                        onThemeSelected = { viewModel.setThemeMode(it) }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.70f)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                tonalElevation = 8.dp,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                PortfolioSection.entries.forEach { section ->
                    val selected = activeSection == section
                    val icon: ImageVector = when (section) {
                        PortfolioSection.ABOUT -> Icons.Default.Person
                        PortfolioSection.EXPERIENCE -> Icons.Default.DateRange
                        PortfolioSection.PROJECTS -> Icons.AutoMirrored.Filled.List
                        PortfolioSection.SKILLS -> Icons.Default.Build
                        PortfolioSection.CONTACT -> Icons.Default.Email
                    }

                    NavigationBarItem(
                        selected = selected,
                        onClick = { onSectionSelected(section) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = section.title,
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item(key = PortfolioSection.ABOUT.name) {
                    HeroHeader(
                        profile = state.profile,
                        socialLinks = state.socialLinks,
                        onOpenUrl = onOpenUrl,
                        onResourceClick = onResourceClick
                    )
                }

                item(key = PortfolioSection.EXPERIENCE.name) {
                    ExperienceTimeline(
                        experiences = state.experiences,
                        windowSizeClass = WindowWidthSizeClass.COMPACT
                    )
                }

                item(key = PortfolioSection.PROJECTS.name) {
                    ProjectsShowcase(
                        projects = state.projects,
                        onOpenUrl = onOpenUrl
                    )
                }

                item(key = PortfolioSection.SKILLS.name) {
                    SkillsSection(
                        skills = state.skills,
                        selectedCategory = state.selectedSkillCategory,
                        onCategorySelected = { cat -> viewModel.filterSkillsByCategory(cat) }
                    )
                }

                item(key = PortfolioSection.CONTACT.name) {
                    ContactSection(
                        profile = state.profile,
                        socialLinks = state.socialLinks,
                        onOpenUrl = onOpenUrl,
                        onResourceClick = onResourceClick
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
//  Fading Edges Modifier for Smooth Top & Bottom Scroll Transitions
// ─────────────────────────────────────────────────────────

// ─────────────────────────────────────────────────────────
//  iOS/macOS-Style Sidebar Navigation Menu
// ─────────────────────────────────────────────────────────

@Composable
fun IOSSidebarNavMenu(
    activeSection: PortfolioSection,
    onSectionSelected: (PortfolioSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PortfolioSection.entries.forEach { section ->
            val isSelected = activeSection == section

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                animationSpec = tween(durationMillis = 250)
            )
            val fgColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 250)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .selectable(selected = isSelected, role = Role.Tab) { onSectionSelected(section) },
                shape = RoundedCornerShape(12.dp),
                color = bgColor
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = fgColor
                    )
                }
            }
        }
    }
}

// ─── Sub-components ───

@Composable
private fun ThemeDropdownButton(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = themeIcon(currentTheme),
                contentDescription = "Appearance: ${currentTheme.displayName}",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.displayName) },
                    leadingIcon = { Icon(themeIcon(mode), contentDescription = null) },
                    trailingIcon = {
                        if (mode == currentTheme) {
                            Icon(Icons.Default.Check, contentDescription = "Selected")
                        }
                    },
                    onClick = {
                        onThemeSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun themeIcon(mode: ThemeMode): ImageVector = when (mode) {
    ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
    ThemeMode.LIGHT -> Icons.Default.LightMode
    ThemeMode.DARK -> Icons.Default.DarkMode
}
