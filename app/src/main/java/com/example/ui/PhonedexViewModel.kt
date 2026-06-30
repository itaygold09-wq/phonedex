package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.ui.theme.PhonedexThemeMode
import com.example.utils.SoundEffects
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class PhonedexViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val repository = PokemonRepository(application)
    private var tts: TextToSpeech? = null
    private var ttsInitialized = false

    // --- UI Navigation State ---
    private val _currentRoute = MutableStateFlow("splash")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _previousRoutes = MutableStateFlow<List<String>>(emptyList())
    val previousRoutes: StateFlow<List<String>> = _previousRoutes.asStateFlow()

    // --- Theme & Configuration States ---
    private val _currentTheme = MutableStateFlow(PhonedexThemeMode.RED)
    val currentTheme: StateFlow<PhonedexThemeMode> = _currentTheme.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    // --- Data States ---
    private val _allPokemons = MutableStateFlow<List<PokemonDetail>>(emptyList())
    val allPokemons: StateFlow<List<PokemonDetail>> = _allPokemons.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPokemonId = MutableStateFlow<Int?>(null)
    val selectedPokemonId: StateFlow<Int?> = _selectedPokemonId.asStateFlow()

    val selectedPokemon: StateFlow<PokemonDetail?> = combine(_selectedPokemonId, _allPokemons) { id, pokemons ->
        if (id != null) pokemons.firstOrNull { it.id == id } else null
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // --- Filters ---
    private val _selectedTypeFilter = MutableStateFlow<String?>(null)
    val selectedTypeFilter: StateFlow<String?> = _selectedTypeFilter.asStateFlow()

    private val _selectedGenerationFilter = MutableStateFlow<Int?>(null)
    val selectedGenerationFilter: StateFlow<Int?> = _selectedGenerationFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<String?>(null) // "Legendary", "Mythical", "Starter", "Ultra Beast", "Paradox", "Mega", "Gigantamax"
    val selectedStatusFilter: StateFlow<String?> = _selectedStatusFilter.asStateFlow()

    // --- Filtered Pokemon List ---
    val filteredPokemons: StateFlow<List<PokemonDetail>> = combine(
        _allPokemons,
        _searchQuery,
        _selectedTypeFilter,
        _selectedGenerationFilter,
        _selectedStatusFilter
    ) { pokemons, query, type, gen, status ->
        var list = pokemons
        if (query.isNotEmpty()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.id.toString() == query ||
                it.types.any { t -> t.contains(query, ignoreCase = true) } ||
                it.abilities.any { a -> a.contains(query, ignoreCase = true) } ||
                it.hiddenAbility?.contains(query, ignoreCase = true) == true
            }
        }
        if (type != null) {
            list = list.filter { it.types.any { t -> t.equals(type, ignoreCase = true) } }
        }
        if (gen != null) {
            list = list.filter { it.generation == gen }
        }
        if (status != null) {
            list = when (status) {
                "Legendary" -> list.filter { it.isLegendary }
                "Mythical" -> list.filter { it.isMythical }
                "Starter" -> list.filter { it.isStarter }
                "Ultra Beast" -> list.filter { it.ultraBeasts.isNotEmpty() }
                "Paradox" -> list.filter { it.paradoxForms.isNotEmpty() }
                "Mega" -> list.filter { it.megaEvolutions.isNotEmpty() }
                "Gigantamax" -> list.filter { it.gigantamax.isNotEmpty() }
                else -> list
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Favorites state ---
    val favorites: StateFlow<List<FavoriteEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Teams state ---
    val teams: StateFlow<List<TeamEntity>> = repository.getTeams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Recently Viewed state ---
    val recentlyViewed: StateFlow<List<RecentlyViewedEntity>> = repository.getRecentlyViewed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Battle Helper State ---
    private val _battlePoke1 = MutableStateFlow<PokemonDetail?>(null)
    val battlePoke1: StateFlow<PokemonDetail?> = _battlePoke1.asStateFlow()

    private val _battlePoke2 = MutableStateFlow<PokemonDetail?>(null)
    val battlePoke2: StateFlow<PokemonDetail?> = _battlePoke2.asStateFlow()

    // --- Assistant Voice State ---
    private val _assistantResponse = MutableStateFlow<String?>(null)
    val assistantResponse: StateFlow<String?> = _assistantResponse.asStateFlow()

    private val _isAssistantSpeaking = MutableStateFlow(false)
    val isAssistantSpeaking: StateFlow<Boolean> = _isAssistantSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    // --- Camera Scan Simulator State ---
    private val _scannedPokemon = MutableStateFlow<PokemonDetail?>(null)
    val scannedPokemon: StateFlow<PokemonDetail?> = _scannedPokemon.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    init {
        // Initialize static offline database
        _allPokemons.value = repository.getStaticPokemons()
        
        // Initialize Text to Speech
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            // Ignore TTS errors if not supported in container
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let {
                val result = it.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsInitialized = true
                }
            }
        }
    }

    // --- Voice Assistant Actions ---
    fun askVoiceAssistant(query: String) {
        if (query.isBlank()) return
        SoundEffects.playConfirm()
        _isListening.value = false
        _isAssistantSpeaking.value = true
        _assistantResponse.value = "Scanning Databanks..."
        
        viewModelScope.launch {
            val response = repository.askDexAssistant(query)
            _assistantResponse.value = response
            speakAloud(response)
        }
    }

    fun speakAloud(text: String) {
        if (!ttsInitialized || tts == null) {
            _isAssistantSpeaking.value = false
            return
        }
        viewModelScope.launch {
            _isAssistantSpeaking.value = true
            // Strip anime markers if any for natural speaking
            val speechText = text.replace("Zzt-zzt!", "").replace("Alola!", "").trim()
            tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "phonedex_tts")
            // Simulate speaking end after reading
            val duration = (speechText.length * 60L).coerceAtLeast(1500L)
            kotlinx.coroutines.delay(duration)
            _isAssistantSpeaking.value = false
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        _isAssistantSpeaking.value = false
    }

    fun startListening() {
        SoundEffects.playBeep()
        _isListening.value = true
    }

    // --- Camera Simulator Scan Action ---
    fun simulateCardScan(scanCategory: String) {
        SoundEffects.playChime()
        _isScanning.value = true
        _scannedPokemon.value = null
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Beautiful scanning pause
            val matches = repository.getStaticPokemons().shuffled()
            val chosen = when (scanCategory) {
                "Plushie" -> matches.first { it.id == 25 } // Pikachu
                "Card" -> matches.first { it.id == 6 } // Charizard
                "Figure" -> matches.first { it.id == 150 } // Mewtwo
                else -> matches.first()
            }
            _scannedPokemon.value = chosen
            _isScanning.value = false
            SoundEffects.playConfirm()
        }
    }

    // --- Favorites Interactions ---
    fun toggleFavorite(type: String, idOrName: String, name: String) {
        SoundEffects.playBeep()
        viewModelScope.launch {
            repository.toggleFavorite(type, idOrName, name)
        }
    }

    fun isFavorite(type: String, idOrName: String): Flow<Boolean> {
        return repository.isFavoriteFlow(type, idOrName)
    }

    // --- Navigation Actions ---
    fun navigateTo(route: String) {
        SoundEffects.playBeep()
        stopSpeaking()
        val current = _currentRoute.value
        if (current != route) {
            val history = _previousRoutes.value.toMutableList()
            history.add(current)
            _previousRoutes.value = history
            _currentRoute.value = route
        }
    }

    fun navigateBack() {
        SoundEffects.playBeep()
        stopSpeaking()
        val history = _previousRoutes.value.toMutableList()
        if (history.isNotEmpty()) {
            val prev = history.removeAt(history.size - 1)
            _previousRoutes.value = history
            _currentRoute.value = prev
        } else {
            _currentRoute.value = "home"
        }
    }

    fun showPokemonDetail(id: Int) {
        _selectedPokemonId.value = id
        // Save to recently viewed
        viewModelScope.launch {
            val p = getPokemonById(id)
            if (p != null) {
                repository.addRecentlyViewed(id, p.name)
            }
        }
        navigateTo("pokemon_detail")
    }

    private suspend fun getPokemonById(id: Int): PokemonDetail? {
        return repository.getPokemonById(id)
    }

    // --- Filters ---
    fun setTypeFilter(type: String?) {
        SoundEffects.playBeep()
        _selectedTypeFilter.value = type
    }

    fun setGenerationFilter(gen: Int?) {
        SoundEffects.playBeep()
        _selectedGenerationFilter.value = gen
    }

    fun setStatusFilter(status: String?) {
        SoundEffects.playBeep()
        _selectedStatusFilter.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearAllFilters() {
        SoundEffects.playBeep()
        _searchQuery.value = ""
        _selectedTypeFilter.value = null
        _selectedGenerationFilter.value = null
        _selectedStatusFilter.value = null
    }

    // --- Team Builder Actions ---
    fun createTeam(name: String, pokemonIds: List<Int>) {
        SoundEffects.playConfirm()
        viewModelScope.launch {
            repository.saveTeam(name, pokemonIds)
        }
    }

    fun deleteTeam(id: Int) {
        SoundEffects.playBeep()
        viewModelScope.launch {
            repository.deleteTeam(id)
        }
    }

    // --- Battle Helper Actions ---
    fun selectBattlePokemon(slot: Int, pokemon: PokemonDetail?) {
        SoundEffects.playBeep()
        if (slot == 1) {
            _battlePoke1.value = pokemon
        } else {
            _battlePoke2.value = pokemon
        }
    }

    // --- Settings Actions ---
    fun setThemeMode(mode: PhonedexThemeMode) {
        SoundEffects.playConfirm()
        _currentTheme.value = mode
    }

    fun toggleDarkTheme() {
        SoundEffects.playBeep()
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun toggleSound() {
        val next = !_soundEnabled.value
        _soundEnabled.value = next
        SoundEffects.enabled = next
        SoundEffects.playBeep()
    }

    // --- Random Actions ---
    fun navigateToRandomPokemon(category: String) {
        SoundEffects.playConfirm()
        val pool = _allPokemons.value
        if (pool.isEmpty()) return
        val chosen = when (category) {
            "Legendary" -> pool.filter { it.isLegendary }.shuffled().firstOrNull() ?: pool.random()
            "Starter" -> pool.filter { it.isStarter }.shuffled().firstOrNull() ?: pool.random()
            "Gen 1" -> pool.filter { it.generation == 1 }.shuffled().firstOrNull() ?: pool.random()
            "Electric" -> pool.filter { it.types.contains("Electric") }.shuffled().firstOrNull() ?: pool.random()
            else -> pool.random()
        }
        showPokemonDetail(chosen.id)
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }
}
