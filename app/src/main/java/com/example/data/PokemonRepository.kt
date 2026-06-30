package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PokemonRepository(context: Context) {
    private val db = PokemonDatabase.getDatabase(context)
    private val dao = db.pokemonDao()
    private val json = Json { ignoreUnknownKeys = true }

    // --- Pokémon Details ---
    fun getStaticPokemons(): List<PokemonDetail> {
        return PokemonStaticData.pokemons
    }

    suspend fun getPokemonById(id: Int): PokemonDetail? {
        // Try static list first
        val staticPokemon = getStaticPokemons().firstOrNull { it.id == id }
        if (staticPokemon != null) {
            return staticPokemon
        }
        
        // Try local database cache
        val cached = dao.getCachedPokemon(id)
        if (cached != null) {
            try {
                return json.decodeFromString<PokemonDetail>(cached.pokemonDetailJson)
            } catch (e: Exception) {
                // Ignore decoding errors and load online
            }
        }
        return null
    }

    suspend fun getPokemonByName(name: String): PokemonDetail? {
        val lowercaseName = name.lowercase().trim()
        val staticPokemon = getStaticPokemons().firstOrNull { it.name.lowercase() == lowercaseName }
        if (staticPokemon != null) {
            return staticPokemon
        }
        
        // Return null if not in static list for now
        return null
    }

    suspend fun cachePokemon(pokemon: PokemonDetail) {
        val cacheEntity = PokemonCacheEntity(
            id = pokemon.id,
            name = pokemon.name,
            pokemonDetailJson = json.encodeToString(pokemon)
        )
        dao.insertCachedPokemon(cacheEntity)
    }

    // --- Favorites ---
    fun getFavorites(): Flow<List<FavoriteEntity>> = dao.getAllFavorites()

    suspend fun toggleFavorite(type: String, idOrName: String, name: String) {
        val compositeId = "${type}_${idOrName}"
        val isFav = dao.isFavorite(compositeId)
        
        // Check manually in a transaction-like way or simply try to insert/delete based on a quick check
        // To be safe and simple, let's write a suspend check
        // Wait, can we collect the first flow emission? Yes!
        var exists = false
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                exists = dao.isFavorite(compositeId).firstOrNull() ?: false
            } catch (e: Exception) {
                exists = false
            }
        }
        
        if (exists) {
            dao.deleteFavorite(compositeId)
        } else {
            dao.insertFavorite(
                FavoriteEntity(
                    compositeId = compositeId,
                    type = type,
                    idOrName = idOrName,
                    name = name
                )
            )
        }
    }

    fun isFavoriteFlow(type: String, idOrName: String): Flow<Boolean> {
        return dao.isFavorite("${type}_${idOrName}")
    }

    // --- Teams ---
    fun getTeams(): Flow<List<TeamEntity>> = dao.getAllTeams()

    suspend fun saveTeam(name: String, pokemonIds: List<Int>) {
        val pokemonIdsJson = json.encodeToString(pokemonIds)
        dao.insertTeam(TeamEntity(name = name, pokemonIdsJson = pokemonIdsJson))
    }

    suspend fun deleteTeam(id: Int) {
        dao.deleteTeam(id)
    }

    // --- Recently Viewed ---
    fun getRecentlyViewed(): Flow<List<RecentlyViewedEntity>> = dao.getRecentlyViewed()

    suspend fun addRecentlyViewed(pokemonId: Int, name: String) {
        dao.insertRecentlyViewed(RecentlyViewedEntity(pokemonId = pokemonId, name = name))
    }

    // --- Voice Assistant & Search ---
    suspend fun askDexAssistant(query: String): String {
        val systemPrompt = """
            You are the voice of the Rotom Pokédex from the Pokémon anime. 
            Keep your responses under 3 sentences, extremely enthusiastic, and start with an animated exclamation like 'Zzt-zzt!' or 'Alola!'. 
            Always describe the requested Pokémon with accurate, official lore, types, and fun trivia from the Pokémon Database.
            If the user asks about something unrelated to Pokémon, gently remind them that your database is strictly specialized in Pokémon.
        """.trimIndent()
        return GeminiHelper.askGemini(query, systemPrompt)
    }
}
