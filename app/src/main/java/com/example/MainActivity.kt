package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PhonedexViewModel
import com.example.ui.screens.*
import com.example.ui.theme.PhonedexTheme
import com.example.ui.theme.PhonedexThemeMode

class MainActivity : ComponentActivity() {
    private val viewModel: PhonedexViewModel by viewModels()

    override fun getAttributionTag(): String? {
        return "attributionTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.currentTheme.collectAsState()
            val isDark by viewModel.isDarkTheme.collectAsState()

            PhonedexTheme(themeMode = themeMode, darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhonedexApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PhonedexApp(viewModel: PhonedexViewModel) {
    val currentRoute by viewModel.currentRoute.collectAsState()

    // If we are on the splash screen, we don't display the bottom navigation bar
    if (currentRoute == "splash") {
        SplashScreen(viewModel = viewModel)
        return
    }

    Scaffold(
        bottomBar = {
            val showBottomBar = listOf("home", "search_tab", "scan", "favorites", "settings").contains(currentRoute)
            if (showBottomBar) {
                // Pulse animation for central Pokéball scan indicator
                val pulseTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by pulseTransition.animateFloat(
                    initialValue = 0.85f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse_scale"
                )

                Surface(
                    color = Color(0xFFB71C1C),
                    contentColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .testTag("bottom_nav")
                ) {
                    Column {
                        // Border line at top of navigation bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.15f))
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Home Tab
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.navigateTo("home") }
                                    .padding(vertical = 8.dp)
                                    .testTag("nav_tab_home")
                            ) {
                                Icon(
                                    imageVector = if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home",
                                    tint = if (currentRoute == "home") Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "HOME",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRoute == "home") Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }

                            // Search Tab (Dex)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.navigateTo("search_tab") }
                                    .padding(vertical = 8.dp)
                                    .testTag("nav_tab_search_tab")
                            ) {
                                Icon(
                                    imageVector = if (currentRoute == "search_tab") Icons.Filled.Search else Icons.Outlined.Search,
                                    contentDescription = "Dex",
                                    tint = if (currentRoute == "search_tab") Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "DEX",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRoute == "search_tab") Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }

                            // Pulsing central Pokéball button (Scan)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .offset(y = (-14).dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color.White, CircleShape)
                                        .border(5.dp, Color(0xFFC62828), CircleShape)
                                        .clickable { viewModel.navigateTo("scan") }
                                        .testTag("nav_tab_scan")
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .border(2.dp, Color(0xFF1A1A1A), CircleShape)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .graphicsLayer {
                                                    scaleX = pulseScale
                                                    scaleY = pulseScale
                                                }
                                                .background(Color(0xFFE53935), CircleShape)
                                        )
                                    }
                                }
                            }

                            // Favorites Tab (Team)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.navigateTo("favorites") }
                                    .padding(vertical = 8.dp)
                                    .testTag("nav_tab_favorites")
                            ) {
                                Icon(
                                    imageVector = if (currentRoute == "favorites") Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Team",
                                    tint = if (currentRoute == "favorites") Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "TEAM",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRoute == "favorites") Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }

                            // Settings Tab (Set)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.navigateTo("settings") }
                                    .padding(vertical = 8.dp)
                                    .testTag("nav_tab_settings")
                            ) {
                                Icon(
                                    imageVector = if (currentRoute == "settings") Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings",
                                    tint = if (currentRoute == "settings") Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "SET",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRoute == "settings") Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Smooth slide & fade transition between screens
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_navigation"
            ) { targetRoute ->
                when (targetRoute) {
                    "home" -> HomeScreen(viewModel = viewModel)
                    "search_tab" -> HomeScreen(viewModel = viewModel) // Search is integrated elegantly inside home view
                    "scan" -> CameraScanScreen(viewModel = viewModel)
                    "favorites" -> FavoritesScreen(viewModel = viewModel)
                    "settings" -> SettingsScreen(viewModel = viewModel)
                    
                    // Detail and sub-screens
                    "pokemon_detail" -> PokemonDetailScreen(viewModel = viewModel)
                    "team_builder" -> TeamBuilderScreen(viewModel = viewModel)
                    "battle_helper" -> BattleHelperScreen(viewModel = viewModel)
                    "voice_assistant" -> VoiceAssistantScreen(viewModel = viewModel)
                    
                    // List auxiliary profiles
                    "moves" -> MovesListScreen(viewModel = viewModel)
                    "abilities" -> AbilitiesListScreen(viewModel = viewModel)
                    "items" -> ItemsListScreen(viewModel = viewModel)
                }
            }
        }
    }
}
