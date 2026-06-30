package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    // --- Favorites ---
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE compositeId = :compositeId")
    suspend fun deleteFavorite(compositeId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE compositeId = :compositeId)")
    fun isFavorite(compositeId: String): Flow<Boolean>

    // --- Teams ---
    @Query("SELECT * FROM teams ORDER BY createdAt DESC")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)

    @Query("DELETE FROM teams WHERE id = :id")
    suspend fun deleteTeam(id: Int)

    // --- Recently Viewed ---
    @Query("SELECT * FROM recently_viewed ORDER BY timestamp DESC LIMIT 20")
    fun getRecentlyViewed(): Flow<List<RecentlyViewedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyViewed(recentlyViewed: RecentlyViewedEntity)

    @Query("DELETE FROM recently_viewed WHERE pokemonId = :pokemonId")
    suspend fun deleteRecentlyViewed(pokemonId: Int)

    // --- Pokemon Offline/Online Cache ---
    @Query("SELECT * FROM pokemon_cache WHERE id = :id")
    suspend fun getCachedPokemon(id: Int): PokemonCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedPokemon(cache: PokemonCacheEntity)
}
