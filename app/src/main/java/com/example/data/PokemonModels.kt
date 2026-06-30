package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetail(
    val id: Int, // National Dex Number
    val name: String,
    val species: String,
    val generation: Int,
    val types: List<String>,
    val abilities: List<String>,
    val hiddenAbility: String?,
    val height: String, // e.g. "1.7 m"
    val weight: String, // e.g. "90.5 kg"
    val genderRatio: String, // e.g. "87.5% M / 12.5% F"
    val catchRate: Int,
    val baseFriendship: Int,
    val eggGroups: List<String>,
    val growthRate: String,
    val expYield: Int,
    val evYield: String,
    val color: String,
    val shape: String,
    
    // Form Categories
    val forms: List<String> = emptyList(),
    val regionalForms: List<String> = emptyList(),
    val megaEvolutions: List<String> = emptyList(),
    val gigantamax: List<String> = emptyList(),
    val paradoxForms: List<String> = emptyList(),
    val ultraBeasts: List<String> = emptyList(),
    
    // Status Flags
    val isLegendary: Boolean = false,
    val isMythical: Boolean = false,
    val isStarter: Boolean = false,
    val isBaby: Boolean = false,
    val isPseudoLegendary: Boolean = false,
    
    // Base Stats
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val spAttack: Int,
    val spDefense: Int,
    val speed: Int,
    val bstTotal: Int,
    
    // Visual Assets
    val artworkUrl: String,
    val spriteUrl: String,
    val description: String
)

@Serializable
data class MoveInfo(
    val name: String,
    val type: String,
    val category: String, // Physical, Special, Status
    val power: Int?, // null for status moves or variable power
    val accuracy: Int?, // null for non-missing moves
    val pp: Int,
    val priority: Int = 0,
    val description: String,
    val generation: Int,
    val learnMethods: List<String> // TM, Egg, Tutor, Level-up (e.g. "Level 15")
)

@Serializable
data class AbilityInfo(
    val name: String,
    val description: String,
    val battleEffect: String,
    val isHidden: Boolean = false,
    val pokemonWithAbility: List<String> = emptyList()
)

@Serializable
data class ItemInfo(
    val name: String,
    val imageUrl: String,
    val description: String,
    val category: String,
    val effect: String,
    val heldByPokemon: List<String> = emptyList(),
    val games: List<String> = emptyList()
)

@Serializable
data class EvolutionNode(
    val id: Int,
    val name: String,
    val artworkUrl: String,
    val triggerMethod: String, // Level, Item, Trade, Friendship, etc.
    val triggerDetails: String? = null,
    val children: List<EvolutionNode> = emptyList()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val compositeId: String, // "pokemon_id", "move_name", "ability_name", "item_name"
    val type: String, // "pokemon", "move", "ability", "item"
    val idOrName: String, // ID for pokemon, name for others
    val name: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val pokemonIdsJson: String, // JSON array of up to 6 pokemon IDs/names
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val pokemonId: Int,
    val name: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pokemon_cache")
data class PokemonCacheEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val pokemonDetailJson: String, // Serialized PokemonDetail
    val timestamp: Long = System.currentTimeMillis()
)
