package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- Classic Red Pokédex Theme ---
val RedPrimaryLight = Color(0xFFC62828)
val RedSecondaryLight = Color(0xFFB71C1C)
val RedBackgroundLight = Color(0xFF1A1A1A)
val RedSurfaceLight = Color(0xFF262626)

val RedPrimaryDark = Color(0xFFC62828)
val RedSecondaryDark = Color(0xFFB71C1C)
val RedBackgroundDark = Color(0xFF1A1A1A)
val RedSurfaceDark = Color(0xFF262626)

// --- Great Blue Theme ---
val BluePrimaryLight = Color(0xFF1976D2)
val BlueSecondaryLight = Color(0xFF2196F3)
val BlueBackgroundLight = Color(0xFFE3F2FD)
val BlueSurfaceLight = Color(0xFFBBDEFB)

val BluePrimaryDark = Color(0xFF42A5F5)
val BlueSecondaryDark = Color(0xFF90CAF9)
val BlueBackgroundDark = Color(0xFF0A141D)
val BlueSurfaceDark = Color(0xFF1C2D3D)

// --- Safari Green Theme ---
val GreenPrimaryLight = Color(0xFF388E3C)
val GreenSecondaryLight = Color(0xFF4CAF50)
val GreenBackgroundLight = Color(0xFFE8F5E9)
val GreenSurfaceLight = Color(0xFFC8E6C9)

val GreenPrimaryDark = Color(0xFF66BB6A)
val GreenSecondaryDark = Color(0xFFA5D6A7)
val GreenBackgroundDark = Color(0xFF0A1D0D)
val GreenSurfaceDark = Color(0xFF1C3D23)

// --- Shadow Black Theme ---
val BlackPrimaryLight = Color(0xFF212121)
val BlackSecondaryLight = Color(0xFF484848)
val BlackBackgroundLight = Color(0xFFF5F5F5)
val BlackSurfaceLight = Color(0xFFE0E0E0)

val BlackPrimaryDark = Color(0xFFB0B0B0)
val BlackSecondaryDark = Color(0xFF757575)
val BlackBackgroundDark = Color(0xFF0F0F0F)
val BlackSurfaceDark = Color(0xFF1E1E1E)

// --- Premier White Theme ---
val WhitePrimaryLight = Color(0xFF757575)
val WhiteSecondaryLight = Color(0xFF9E9E9E)
val WhiteBackgroundLight = Color(0xFFFAFAFA)
val WhiteSurfaceLight = Color(0xFFFFFFFF)

val WhitePrimaryDark = Color(0xFFEEEEEE)
val WhiteSecondaryDark = Color(0xFFBDBDBD)
val WhiteBackgroundDark = Color(0xFF121212)
val WhiteSurfaceDark = Color(0xFF212121)

// --- Pokémon Type Colors (for Chips, Cards, and Badges) ---
val TypeFire = Color(0xFFF08030)
val TypeWater = Color(0xFF6890F0)
val TypeGrass = Color(0xFF78C850)
val TypeElectric = Color(0xFFF8D030)
val TypePsychic = Color(0xFFF85888)
val TypeIce = Color(0xFF98D8D8)
val TypeDragon = Color(0xFF7038F8)
val TypeDark = Color(0xFF705848)
val TypeFairy = Color(0xFFEE99AC)
val TypeNormal = Color(0xFFA8A878)
val TypeFighting = Color(0xFFC03028)
val TypeFlying = Color(0xFFA890F0)
val TypePoison = Color(0xFFA040A0)
val TypeGround = Color(0xFFE0C068)
val TypeRock = Color(0xFFB8A038)
val TypeBug = Color(0xFFA8B820)
val TypeGhost = Color(0xFF705898)
val TypeSteel = Color(0xFFB8B8D0)

fun getPokemonTypeColor(type: String): Color {
    return when (type.lowercase().trim()) {
        "fire" -> TypeFire
        "water" -> TypeWater
        "grass" -> TypeGrass
        "electric" -> TypeElectric
        "psychic" -> TypePsychic
        "ice" -> TypeIce
        "dragon" -> TypeDragon
        "dark" -> TypeDark
        "fairy" -> TypeFairy
        "normal" -> TypeNormal
        "fighting" -> TypeFighting
        "flying" -> TypeFlying
        "poison" -> TypePoison
        "ground" -> TypeGround
        "rock" -> TypeRock
        "bug" -> TypeBug
        "ghost" -> TypeGhost
        "steel" -> TypeSteel
        else -> Color.Gray
    }
}
