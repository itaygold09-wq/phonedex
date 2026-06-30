package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.*
import com.example.ui.PhonedexViewModel
import com.example.ui.theme.PhonedexThemeMode
import com.example.ui.theme.getPokemonTypeColor
import com.example.utils.SoundEffects
import kotlinx.coroutines.delay

// --- Generic Header Component ---
@Composable
fun PhonedexDeviceHeader(
    viewModel: PhonedexViewModel,
    title: String,
    onBackClick: (() -> Unit)? = null
) {
    val themeMode by viewModel.currentTheme.collectAsState()
    
    val deviceColor = when (themeMode) {
        PhonedexThemeMode.RED -> Color(0xFFC62828)
        PhonedexThemeMode.BLUE -> Color(0xFF1976D2)
        PhonedexThemeMode.GREEN -> Color(0xFF388E3C)
        PhonedexThemeMode.BLACK -> Color(0xFF212121)
        PhonedexThemeMode.WHITE -> Color(0xFFEEEEEE)
    }

    val onDeviceColor = if (themeMode == PhonedexThemeMode.WHITE) Color.Black else Color.White

    // Blinking lights state animation
    val infiniteTransition = rememberInfiniteTransition(label = "blinking_lights")
    val isBlinking by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    Surface(
        color = deviceColor,
        contentColor = onDeviceColor,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Physical Pokédex top panel detailing
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Large blue circular camera lens / scanner
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(3.dp, Color.White, CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFE3F2FD), Color(0xFF64B5F6), Color(0xFF1976D2))
                                ),
                                CircleShape
                            )
                            .graphicsLayer {
                                scaleX = 1f + (0.05f * isBlinking)
                                scaleY = 1f + (0.05f * isBlinking)
                            }
                    ) {
                        // Gloss reflection highlight
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .offset(10.dp, 10.dp)
                                .background(Color.White.copy(alpha = 0.6f), CircleShape)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Pokédex Blinking auxiliary LEDs (Red, Yellow, Green)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    Color.Red.copy(alpha = if (isBlinking > 0.5) 1.0f else 0.3f),
                                    CircleShape
                                )
                                .border(1.dp, Color.White, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    Color.Yellow.copy(alpha = if (isBlinking < 0.6) 1.0f else 0.3f),
                                    CircleShape
                                )
                                .border(1.dp, Color.White, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    Color.Green.copy(alpha = if (isBlinking > 0.4) 1.0f else 0.3f),
                                    CircleShape
                                )
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                }

                // Device branding
                Text(
                    text = "PHONEDEX MODEL V3",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = onDeviceColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Row inside Device Panel
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = {
                            SoundEffects.playBeep()
                            onBackClick()
                        },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go Back",
                            tint = onDeviceColor
                        )
                    }
                }
                
                Text(
                    text = title.uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (onBackClick == null) 4.dp else 8.dp)
                )
                
                // Sound status icon
                val soundOn by viewModel.soundEnabled.collectAsState()
                IconButton(onClick = { viewModel.toggleSound() }) {
                    Icon(
                        imageVector = if (soundOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = "Toggle Sound",
                        tint = onDeviceColor
                    )
                }
            }
        }
    }
}

// --- Screen 1: Splash Screen ---
@Composable
fun SplashScreen(viewModel: PhonedexViewModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowing"
    )

    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        SoundEffects.playStartup()
        delay(3500) // Beautiful splash showcase duration
        viewModel.navigateTo("home")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF330505), Color(0xFF100202))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Futuristic Holographic Scanner Outer Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer {
                        scaleX = scaleAnim
                        scaleY = scaleAnim
                    }
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFFFF5252).copy(alpha = alphaAnim * 0.15f),
                            radius = size.minDimension / 1.8f
                        )
                    }
            ) {
                // High Quality Generated Pokédex Device Artwork
                Image(
                    painter = painterResource(id = R.drawable.img_pokedex_hero),
                    contentDescription = "Phonedex Hero",
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color(0xFFFF5252), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "PHONEDEX",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFFF5252),
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = alphaAnim }
            )

            Text(
                text = "THE ULTIMATE POKÉMON COMPANION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Pokédex terminal booting simulator text
            CircularProgressIndicator(
                color = Color(0xFFFF5252),
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = "ZZT-ZZT! ROTOM INITIALIZING...",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

// --- Screen 2: Home Screen ---
@Composable
fun HomeScreen(viewModel: PhonedexViewModel) {
    val pokemons by viewModel.filteredPokemons.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val recentlyViewed by viewModel.recentlyViewed.collectAsState()
    
    // Quick Category list for quick navigation
    val categories = listOf(
        "Moves" to Icons.Filled.FlashOn,
        "Abilities" to Icons.Filled.ElectricBolt,
        "Items" to Icons.Filled.Backpack,
        "Teams" to Icons.Filled.Group,
        "Battle" to Icons.Filled.SportsMartialArts
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(viewModel = viewModel, title = "Phonedex")

        // Main content area viewport (Vibrant Palette Console Viewport Screen)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Search field styled with premium glassmorphism
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by name, type, ability, move...", color = Color.White.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.7f)) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.7f))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.10f),
                    focusedBorderColor = Color.White.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Categories Row
            Text(
                text = "SYSTEM MODULES",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(categories) { (name, icon) ->
                    Card(
                        onClick = {
                            when (name) {
                                "Moves" -> viewModel.navigateTo("moves")
                                "Abilities" -> viewModel.navigateTo("abilities")
                                "Items" -> viewModel.navigateTo("items")
                                "Teams" -> viewModel.navigateTo("team_builder")
                                "Battle" -> viewModel.navigateTo("battle_helper")
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .width(110.dp)
                            .testTag("category_button_${name.lowercase()}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Interactive Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Random Generator Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    onClick = { viewModel.navigateToRandomPokemon("All") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.Filled.Casino, contentDescription = "Random", tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "RANDOM\nPOKÉMON",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Random Legendary Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    onClick = { viewModel.navigateToRandomPokemon("Legendary") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Legendary", tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "RANDOM\nLEGENDARY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // National Pokédex Grid
            Text(
                text = "NATIONAL POKÉDEX",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (pokemons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching Pokémon found zzt-zzt!",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp) // Bound the grid inside scroll view
                ) {
                    items(pokemons) { pokemon ->
                        PokemonGridItem(pokemon = pokemon) {
                            viewModel.showPokemonDetail(pokemon.id)
                        }
                    }
                }
            }

            // Recently Viewed Panel
            if (recentlyViewed.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "RECENTLY VIEWED",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    items(recentlyViewed) { recent ->
                        val p = viewModel.allPokemons.value.firstOrNull { it.id == recent.pokemonId }
                        if (p != null) {
                            Card(
                                onClick = { viewModel.showPokemonDetail(p.id) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    AsyncImage(
                                        model = p.spriteUrl,
                                        contentDescription = p.name,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = p.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonGridItem(pokemon: PokemonDetail, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pokemon_card_${pokemon.name.lowercase()}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Artwork representation
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.artworkUrl,
                    contentDescription = pokemon.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "#${String.format("%03d", pokemon.id)}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = pokemon.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Type badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (type in pokemon.types) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(getPokemonTypeColor(type))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = type.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// --- Screen 3: Pokémon Detail Page ---
@Composable
fun PokemonDetailScreen(viewModel: PhonedexViewModel) {
    val pokemon by viewModel.selectedPokemon.collectAsState()
    
    if (pokemon == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val p = pokemon!!
    val isFav by viewModel.isFavorite("pokemon", p.id.toString()).collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(
            viewModel = viewModel,
            title = "#${String.format("%03d", p.id)} ${p.name}",
            onBackClick = { viewModel.navigateBack() }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Hero card holding artwork and main identifiers
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Legendary / Mythical tags
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (p.isLegendary) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFD54F), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("LEGENDARY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                            if (p.isMythical) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE040FB), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("MYTHICAL", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            if (p.isStarter) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF4DB6AC), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("STARTER", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                        
                        // Favorite button
                        IconButton(
                            onClick = { viewModel.toggleFavorite("pokemon", p.id.toString(), p.name) },
                            modifier = Modifier.testTag("favorite_button")
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) Color.Red else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Main Image with circular radar-scanning background
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(200.dp)
                            .background(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                CircleShape
                            )
                    ) {
                        AsyncImage(
                            model = p.artworkUrl,
                            contentDescription = p.name,
                            modifier = Modifier.size(180.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Speaker sound trigger
                    Button(
                        onClick = { viewModel.speakAloud(p.description) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.RecordVoiceOver, contentDescription = "Speak")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DEX VOICE ASSISTANT", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bio text description
                    Text(
                        text = p.description,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Characteristics list
            Text("DATA PROFILES", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val fields = listOf(
                        "National No." to "#${String.format("%03d", p.id)}",
                        "Species" to p.species,
                        "Generation" to "Gen ${p.generation}",
                        "Height" to p.height,
                        "Weight" to p.weight,
                        "Gender Ratio" to p.genderRatio,
                        "Catch Rate" to p.catchRate.toString(),
                        "Friendship" to p.baseFriendship.toString(),
                        "Growth Rate" to p.growthRate,
                        "Egg Groups" to p.eggGroups.joinToString(", ")
                    )
                    
                    for (index in fields.indices) {
                        val (label, valStr) = fields[index]
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, fontFamily = FontFamily.Monospace)
                            Text(valStr, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                        if (index < fields.size - 1) {
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.15f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Base Stats section
            Text("BASE STATS (BST: ${p.bstTotal})", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val stats = listOf(
                        "HP" to p.hp,
                        "ATTACK" to p.attack,
                        "DEFENSE" to p.defense,
                        "SP. ATK" to p.spAttack,
                        "SP. DEF" to p.spDefense,
                        "SPEED" to p.speed
                    )
                    
                    for ((statName, statVal) in stats) {
                        val ratio = (statVal / 255f).coerceIn(0f, 1f)
                        val barColor = when {
                            statVal < 60 -> Color(0xFFEF5350) // Red
                            statVal < 90 -> Color(0xFFFFB74D) // Orange
                            statVal < 120 -> Color(0xFF81C784) // Green
                            else -> Color(0xFF4FC3F7) // Blue
                        }
                        
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(statName, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(statVal.toString(), fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Bar layout
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.2f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(ratio)
                                        .clip(CircleShape)
                                        .background(barColor)
                                )
                            }
                        }
                    }
                }
            }

            // Type Effectiveness Calculations
            Spacer(modifier = Modifier.height(16.dp))
            Text("TYPE COVERAGE", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Matchups weaknesses
                    val weakList = if (p.types.contains("Fire")) listOf("Water", "Ground", "Rock") else listOf("Fire", "Flying", "Psychic")
                    val resistList = if (p.types.contains("Fire")) listOf("Fire", "Grass", "Ice", "Steel", "Fairy") else listOf("Water", "Grass", "Ice")
                    
                    Text("Weak Against (2x Damage):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Red, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (wType in weakList) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(getPokemonTypeColor(wType))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(wType.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Resistant to (0.5x Damage):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (rType in resistList) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(getPokemonTypeColor(rType))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(rType.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Interactive Evolution Chains
            Spacer(modifier = Modifier.height(16.dp))
            Text("EVOLUTION CHAINS", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            
            val evolution = PokemonStaticData.evolutionTrees[p.id]
            if (evolution != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        RenderEvolutionNode(node = evolution, viewModel = viewModel)
                    }
                }
            } else {
                Text(
                    text = "This Pokémon has no known evolutions or is a standalone species.",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun RenderEvolutionNode(node: EvolutionNode, viewModel: PhonedexViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            onClick = { viewModel.showPokemonDetail(node.id) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp)
            ) {
                AsyncImage(model = node.artworkUrl, contentDescription = node.name, modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(node.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
        
        for (child in node.children) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ArrowDownward, contentDescription = "Evolves")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${child.triggerMethod} (${child.triggerDetails ?: ""})",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            RenderEvolutionNode(node = child, viewModel = viewModel)
        }
    }
}

// --- Screen 4: Team Builder ---
@Composable
fun TeamBuilderScreen(viewModel: PhonedexViewModel) {
    val teams by viewModel.teams.collectAsState()
    val allPokemons = viewModel.allPokemons.collectAsState().value
    
    var showCreateDialog by remember { mutableStateFlowOf(false) }
    var newTeamName by remember { mutableStateFlowOf("") }
    val selectedTeamPokemon = remember { mutableStateListOf<PokemonDetail>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(
            viewModel = viewModel,
            title = "Team Builder",
            onBackClick = { viewModel.navigateTo("home") }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Intro Callout
            Text(
                text = "Assemble up to 6 companion Pokémon and analyze their average offensive & defensive type coverages instantly.",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            Button(
                onClick = {
                    selectedTeamPokemon.clear()
                    newTeamName = ""
                    showCreateDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_team_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Add, contentDescription = "Create")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CREATE NEW SQUAD", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("SAVED SQUADS", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

            if (teams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No squads constructed yet zzt!", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                }
            } else {
                for (team in teams) {
                    val teamIds = try {
                        kotlinx.serialization.json.Json.decodeFromString<List<Int>>(team.pokemonIdsJson)
                    } catch (e: Exception) {
                        emptyList()
                    }
                    val teamPokes = teamIds.mapNotNull { id -> allPokemons.firstOrNull { it.id == id } }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = team.name.uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = { viewModel.deleteTeam(team.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))

                            // Squad list preview
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                for (p in teamPokes) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                MaterialTheme.colorScheme.background,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.showPokemonDetail(p.id) }
                                    ) {
                                        AsyncImage(model = p.spriteUrl, contentDescription = p.name, modifier = Modifier.size(40.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Squad Statistics Average calculations
                            val avgBST = if (teamPokes.isNotEmpty()) teamPokes.map { it.bstTotal }.average().toInt() else 0
                            val coverageTypes = teamPokes.flatMap { it.types }.distinct()

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Avg BST: $avgBST", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("Types covered: ${coverageTypes.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            
                            // Coverage analysis suggestions
                            val missingCoreTypes = listOf("Fire", "Water", "Grass").filter { !coverageTypes.contains(it) }
                            if (missingCoreTypes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "💡 Suggestion: Consider adding a ${missingCoreTypes.joinToString("/")} type to balance your offense!",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog to construct team
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Assemble New Squad", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newTeamName,
                        onValueChange = { newTeamName = it },
                        placeholder = { Text("Enter Squad Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Select up to 6 members:", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Simple select list
                    LazyColumn(modifier = Modifier.height(180.dp)) {
                        items(allPokemons) { poke ->
                            val isSelected = selectedTeamPokemon.contains(poke)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) {
                                            selectedTeamPokemon.remove(poke)
                                        } else if (selectedTeamPokemon.size < 6) {
                                            selectedTeamPokemon.add(poke)
                                        }
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null // Click handles this
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                AsyncImage(model = poke.spriteUrl, contentDescription = poke.name, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(poke.name, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTeamName.isNotBlank() && selectedTeamPokemon.isNotEmpty()) {
                            viewModel.createTeam(newTeamName, selectedTeamPokemon.map { it.id })
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Construct")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Helper delegate to bypass generic casting warnings
private fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)

// --- Screen 5: Battle Helper ---
@Composable
fun BattleHelperScreen(viewModel: PhonedexViewModel) {
    val b1 by viewModel.battlePoke1.collectAsState()
    val b2 by viewModel.battlePoke2.collectAsState()
    val allPokemons = viewModel.allPokemons.collectAsState().value

    var activeSlot by remember { mutableStateFlowOf<Int?>(null) }
    var showSelectDialog by remember { mutableStateFlowOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(
            viewModel = viewModel,
            title = "Battle Helper",
            onBackClick = { viewModel.navigateTo("home") }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Compare two Pokémon combat metrics side-by-side to analyze speed, defenses, and strategic matchups instantly.",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Side-by-side selection
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pokémon 1 Selection
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Card(
                        onClick = {
                            activeSlot = 1
                            showSelectDialog = true
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            if (b1 != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                                    AsyncImage(model = b1!!.artworkUrl, contentDescription = b1!!.name, modifier = Modifier.size(80.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(b1!!.name.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.Gray)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("SELECT #1", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                }
                            }
                        }
                    }
                    if (b1 != null) {
                        TextButton(onClick = { viewModel.selectBattlePokemon(1, null) }) {
                            Text("Clear Selection", color = Color.Red, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // VS center node
                Box(contentAlignment = Alignment.Center, modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text("VS", fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                }

                // Pokémon 2 Selection
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Card(
                        onClick = {
                            activeSlot = 2
                            showSelectDialog = true
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            if (b2 != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                                    AsyncImage(model = b2!!.artworkUrl, contentDescription = b2!!.name, modifier = Modifier.size(80.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(b2!!.name.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.Gray)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("SELECT #2", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                }
                            }
                        }
                    }
                    if (b2 != null) {
                        TextButton(onClick = { viewModel.selectBattlePokemon(2, null) }) {
                            Text("Clear Selection", color = Color.Red, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Comparison Metrics panel
            if (b1 != null && b2 != null) {
                Text("TACTICAL MATRIX", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Stat comparison highlights
                        val metrics = listOf(
                            "HP" to (b1!!.hp to b2!!.hp),
                            "ATTACK" to (b1!!.attack to b2!!.attack),
                            "DEFENSE" to (b1!!.defense to b2!!.defense),
                            "SP. ATTACK" to (b1!!.spAttack to b2!!.spAttack),
                            "SP. DEFENSE" to (b1!!.spDefense to b2!!.spDefense),
                            "SPEED" to (b1!!.speed to b2!!.speed)
                        )

                        for ((statName, values) in metrics) {
                            val (v1, v2) = values
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = v1.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = if (v1 > v2) FontWeight.Black else FontWeight.Bold,
                                    color = if (v1 > v2) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = statName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1.5f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = v2.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = if (v2 > v1) FontWeight.Black else FontWeight.Bold,
                                    color = if (v2 > v1) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        // Matchup outcomes
                        Text("Speed Analysis:", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                        val speedWinner = if (b1!!.speed > b2!!.speed) b1!!.name else if (b2!!.speed > b1!!.speed) b2!!.name else "Draw"
                        Text(
                            text = "⚡ $speedWinner will attack first in battle due to higher Speed value.",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text("Type Advantage:", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                        val p1Advantage = if (b1!!.types.contains("Fire") && b2!!.types.contains("Grass")) "Super Effective!" else "Normal Matchup"
                        Text(
                            text = "⚔️ ${b1!!.name} against ${b2!!.name}: $p1Advantage",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Select two Pokémon to unlock tactical battle matrix zzt!", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                }
            }
        }
    }

    if (showSelectDialog) {
        AlertDialog(
            onDismissRequest = { showSelectDialog = false },
            title = { Text("Select Member", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        items(allPokemons) { poke ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectBattlePokemon(activeSlot ?: 1, poke)
                                        showSelectDialog = false
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                AsyncImage(model = poke.spriteUrl, contentDescription = poke.name, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(poke.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSelectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --- Screen 6: Voice Assistant ---
@Composable
fun VoiceAssistantScreen(viewModel: PhonedexViewModel) {
    val responseText by viewModel.assistantResponse.collectAsState()
    val isSpeaking by viewModel.isAssistantSpeaking.collectAsState()
    val isListening by viewModel.isListening.collectAsState()

    var userInquiry by remember { mutableStateFlowOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(
            viewModel = viewModel,
            title = "Voice Dex",
            onBackClick = { viewModel.navigateTo("home") }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Visual waveform pulse representing voice scan
            val pulseTransition = rememberInfiniteTransition(label = "waveform")
            val wavePulse1 by pulseTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "p1"
            )

            val wavePulse2 by pulseTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1100, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "p2"
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(160.dp)
                        .padding(16.dp)
                ) {
                    // Outer Pulsing Waveform 2
                    if (isSpeaking || isListening) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = wavePulse2
                                    scaleY = wavePulse2
                                    alpha = 0.2f
                                }
                                .background(Color(0xFF00B0FF), CircleShape)
                        )
                        // Outer Pulsing Waveform 1
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = wavePulse1
                                    scaleY = wavePulse1
                                    alpha = 0.35f
                                }
                                .background(Color(0xFF80D8FF), CircleShape)
                        )
                    }

                    // Solid central glowing node
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF5252), Color(0xFFD32F2F))
                                ),
                                CircleShape
                            )
                            .border(3.dp, Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Filled.RecordVoiceOver else Icons.Filled.Mic,
                            contentDescription = "Assistant State",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isSpeaking) "ROTOM DEX SPEAKING..." else if (isListening) "LISTENING..." else "STANDBY FOR SCAN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isSpeaking || isListening) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }

            // Dialogue view card
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = responseText ?: "Greetings, trainer! Ask me anything about Pokémon (e.g., 'Tell me about Charizard' or 'What are Pikachu's abilities?') and I will search my databanks instantly zzt-zzt!",
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }

            // Speech text input and triggers
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = userInquiry,
                    onValueChange = { userInquiry = it },
                    placeholder = { Text("Query Rotom Dex...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (userInquiry.isNotBlank()) {
                                    viewModel.askVoiceAssistant(userInquiry)
                                    userInquiry = ""
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "Ask")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Popular suggestions tags
                Text("POPULAR TOPICS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Gray)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    val topics = listOf("Charizard", "Mewtwo", "Pikachu Bio")
                    for (topic in topics) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable {
                                    val q = if (topic.contains("Bio")) "Describe ${topic.replace(" Bio", "")}" else "Tell me about $topic"
                                    viewModel.askVoiceAssistant(q)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(topic, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

// --- Screen 7: Camera Scanner ---
@Composable
fun CameraScanScreen(viewModel: PhonedexViewModel) {
    val isScanning by viewModel.isScanning.collectAsState()
    val scannedPoke by viewModel.scannedPokemon.collectAsState()

    val scanTypes = listOf(
        "Card" to Icons.Filled.SimCard,
        "Plushie" to Icons.Filled.Toys,
        "Figure" to Icons.Filled.AccessibilityNew
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(
            viewModel = viewModel,
            title = "Camera Scan",
            onBackClick = { viewModel.navigateTo("home") }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Activate the holographic scanner lens to identify physical cards, figures, plushies, and game screenshots instantly.",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Dynamic camera viewfinder simulation with overlay scanning lasers
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(4.dp, Color(0xFF4CAF50), RoundedCornerShape(24.dp))
                    .background(Color.Black)
            ) {
                // Viewfinder lines or target
                Icon(
                    imageVector = Icons.Filled.FilterCenterFocus,
                    contentDescription = "Target focus",
                    tint = Color(0xFF4CAF50).copy(alpha = 0.5f),
                    modifier = Modifier.size(84.dp)
                )

                // Moving laser sweep line representing scan progress
                if (isScanning) {
                    val laserTransition = rememberInfiniteTransition(label = "laser")
                    val laserOffset by laserTransition.animateFloat(
                        initialValue = -120f,
                        targetValue = 120f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "laser_y"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .offset(y = laserOffset.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
                                )
                            )
                    )
                }

                // Match layout
                if (scannedPoke != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(model = scannedPoke!!.artworkUrl, contentDescription = scannedPoke!!.name, modifier = Modifier.size(120.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "MATCH MATCH MATCH!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4CAF50),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = scannedPoke!!.name.uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action triggers to choose scanning category
            Text("CHOOSE TARGET FOR SCAN:", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Gray)
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                for ((category, icon) in scanTypes) {
                    Card(
                        onClick = { viewModel.simulateCardScan(category) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Icon(imageVector = icon, contentDescription = category, tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(category.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // Results Unlock Button
            if (scannedPoke != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.showPokemonDetail(scannedPoke!!.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("UNLOCK FULL DATA SHEET", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

// --- Screen 8: Favorites Tab ---
@Composable
fun FavoritesScreen(viewModel: PhonedexViewModel) {
    val favorites by viewModel.favorites.collectAsState()
    val allPokemons = viewModel.allPokemons.collectAsState().value
    
    var activeTab by remember { mutableStateFlowOf(0) }
    val tabNames = listOf("Pokémon", "Moves", "Abilities", "Items")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(viewModel = viewModel, title = "Favorites")

        // Main content area viewport (Vibrant Palette Console Viewport Screen)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
        ) {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color(0xFF262626),
                contentColor = Color.White
            ) {
                tabNames.forEachIndexed { index, name ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = { Text(name, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
            val listData = favorites.filter {
                when (activeTab) {
                    0 -> it.type == "pokemon"
                    1 -> it.type == "move"
                    2 -> it.type == "ability"
                    3 -> it.type == "item"
                    else -> false
                }
            }

            if (listData.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favorites saved in this category yet zzt!", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listData) { fav ->
                        Card(
                            onClick = {
                                if (fav.type == "pokemon") {
                                    viewModel.showPokemonDetail(fav.idOrName.toInt())
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (fav.type == "pokemon") {
                                        val p = allPokemons.firstOrNull { it.id == fav.idOrName.toInt() }
                                        if (p != null) {
                                            AsyncImage(model = p.spriteUrl, contentDescription = p.name, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.width(10.dp))
                                        }
                                    }
                                    Text(fav.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                
                                IconButton(onClick = { viewModel.toggleFavorite(fav.type, fav.idOrName, fav.name) }) {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Unfavorite", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

// --- Screen 9: Settings Screen ---
@Composable
fun SettingsScreen(viewModel: PhonedexViewModel) {
    val themeMode by viewModel.currentTheme.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val soundOn by viewModel.soundEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(viewModel = viewModel, title = "Settings")

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("DEVICE CONFIGURATION", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(10.dp))

            // Sound Toggle Option
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = "Sound")
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Interface Sounds", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text("Bleeps, confirm, and startup tones", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Switch(checked = soundOn, onCheckedChange = { viewModel.toggleSound() })
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dark Mode switch
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DarkMode, contentDescription = "Theme")
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("System Theme", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text("Use Dark visual background mode", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Switch(checked = isDark, onCheckedChange = { viewModel.toggleDarkTheme() })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Theme selection options
            Text("POKÉDEX CASE THEMES", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(10.dp))

            val themes = listOf(
                PhonedexThemeMode.RED to "RED POKÉDEX",
                PhonedexThemeMode.BLUE to "GREAT BLUE",
                PhonedexThemeMode.GREEN to "SAFARI GREEN",
                PhonedexThemeMode.BLACK to "SHADOW CYBER",
                PhonedexThemeMode.WHITE to "PREMIER WHITE"
            )

            for ((mode, title) in themes) {
                val isSelected = themeMode == mode
                Card(
                    onClick = { viewModel.setThemeMode(mode) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("theme_option_${mode.name.lowercase()}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (mode) {
                                            PhonedexThemeMode.RED -> Color(0xFFD32F2F)
                                            PhonedexThemeMode.BLUE -> Color(0xFF1976D2)
                                            PhonedexThemeMode.GREEN -> Color(0xFF388E3C)
                                            PhonedexThemeMode.BLACK -> Color(0xFF212121)
                                            PhonedexThemeMode.WHITE -> Color(0xFFEEEEEE)
                                        }
                                    )
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                        
                        if (isSelected) {
                            Icon(Icons.Filled.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

// --- Auxiliary lists sub-screens (Moves, Abilities, Items) ---
@Composable
fun MovesListScreen(viewModel: PhonedexViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(viewModel = viewModel, title = "Attack Moves", onBackClick = { viewModel.navigateBack() })
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(PokemonStaticData.moves) { move ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(move.name, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(getPokemonTypeColor(move.type))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(move.type.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Category: ${move.category}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                Text("Power: ${move.power ?: "-"}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                Text("PP: ${move.pp}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(move.description, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AbilitiesListScreen(viewModel: PhonedexViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(viewModel = viewModel, title = "Abilities", onBackClick = { viewModel.navigateBack() })
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(PokemonStaticData.abilities) { ability ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(ability.name, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(ability.description, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Battle Effect: ${ability.battleEffect}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemsListScreen(viewModel: PhonedexViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        PhonedexDeviceHeader(viewModel = viewModel, title = "Key Items", onBackClick = { viewModel.navigateBack() })
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .border(4.dp, Color(0xFF8B0000), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A1A))
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(PokemonStaticData.items) { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(model = item.imageUrl, contentDescription = item.name, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                                Text("Category: ${item.category}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.description, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}
