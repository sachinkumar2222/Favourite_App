package com.example.favourite.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.favourite.data.Movie
import com.example.favourite.data.RetrofitInstance
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.example.favourite.data.Cast
import com.example.favourite.data.MovieDetail
import com.example.favourite.data.PersonDetail

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    
    private val apiKey = "c21b18d183786cd4be5c3a6f768b1d95"
    private val prefs = application.getSharedPreferences("movie_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    var apiData by mutableStateOf<List<Movie>>(emptyList())
        private set
        
    var heroMovies by mutableStateOf<List<Movie>>(emptyList())
        private set
        
    var isLoading by mutableStateOf(false)
        private set
        
    var isPaginating by mutableStateOf(false)
        private set
        
    var favorites = mutableStateOf<List<Movie>>(emptyList())
        private set

    // New States
    var selectedMovie by mutableStateOf<MovieDetail?>(null)
        private set
    var credits by mutableStateOf<List<Cast>>(emptyList())
        private set
    var recommendations by mutableStateOf<List<Movie>>(emptyList())
        private set
    var personDetails by mutableStateOf<PersonDetail?>(null)
        private set
    var personCredits by mutableStateOf<List<Movie>>(emptyList())
        private set
    var searchResults by mutableStateOf<List<Movie>>(emptyList())
        private set
    
    // Genre Screen State
    var genreMovies by mutableStateOf<List<Movie>>(emptyList())
        private set


    var searchHistory by mutableStateOf<List<Movie>>(emptyList())
        private set

    init {
        loadHistory()
        fetchData(reset = true)
    }

    private fun saveHistory() {
        val json = gson.toJson(searchHistory)
        prefs.edit().putString("search_history", json).apply()
    }

    private fun loadHistory() {
        val json = prefs.getString("search_history", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Movie>>() {}.type
                searchHistory = gson.fromJson(json, type)
            } catch (e: Exception) {
                e.printStackTrace()
                searchHistory = emptyList()
            }
        }
    }

    fun addToHistory(movie: Movie) {
        val current = searchHistory.toMutableList()
        current.removeAll { it.id == movie.id }
        current.add(0, movie)
        searchHistory = current
        saveHistory()
    }

    fun removeFromHistory(movie: Movie) {
        val current = searchHistory.toMutableList()
        current.remove(movie) // Assumes Movie has equals/hashCode or same instance
        // Better to remove by ID to be safe
        val itemToRemove = current.find { it.id == movie.id }
        if (itemToRemove != null) {
            current.remove(itemToRemove)
        }
        searchHistory = current
        saveHistory()
    }

    var director by mutableStateOf<String?>(null)
        private set
    var directorId by mutableStateOf<Int?>(null)
        private set
    var directorProfilePath by mutableStateOf<String?>(null)
        private set



    // Pagination States
    private var currentPage = 1
    private var currentCategory = "trending" // "trending" or genreId

    init {
        fetchData(reset = true)
    }

    fun fetchData(reset: Boolean = true) {
        viewModelScope.launch {
            if (reset) {
                if (isLoading) return@launch
                isLoading = true
            } else {
                if (isPaginating) return@launch
                isPaginating = true
            }
            
            if (reset) {
                currentPage = 1
                currentCategory = "trending"
                // apiData = emptyList() // Optional: Clear list or keep for smooth transition
            } else {
                currentPage++
            }

            try {
                // Fetch Popular Movies
                val movieResponse = RetrofitInstance.api.getPopularMovies(apiKey, currentPage)
                val movies = movieResponse.results.map { it.copy(mediaType = "movie") }
                
                // Fetch Popular TV
                val tvResponse = RetrofitInstance.api.getPopularTV(apiKey, currentPage)
                val tvs = tvResponse.results.map { it.copy(mediaType = "tv") }
                
                // Combine results
                val combined = movies + tvs
                
                if (reset) {
                    apiData = combined
                    // Set Hero Movies (Just take top 10 movies from page 1)
                    heroMovies = movies.take(10)
                } else {
                    apiData = apiData + combined
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                if (!reset) currentPage-- // Revert page on failure
            } finally {
                if (reset) isLoading = false else isPaginating = false
            }
        }
    }
    
    fun fetchByGenre(genreId: String, reset: Boolean = true) {
        viewModelScope.launch {
            if (reset) {
                if (isLoading) return@launch
                isLoading = true
            } else {
                if (isPaginating) return@launch
                isPaginating = true
            }

            if (reset) {
                currentPage = 1
                currentCategory = genreId
            } else {
                currentPage++
            }
            
            try {
                // Fetch Movies by Genre
                val movieResponse = RetrofitInstance.api.getMoviesByGenre(apiKey, genreId, currentPage)
                val movies = movieResponse.results.map { it.copy(mediaType = "movie") }
                
                // Fetch TV by Genre
                val tvResponse = RetrofitInstance.api.getTVByGenre(apiKey, genreId, currentPage)
                val tvs = tvResponse.results.map { it.copy(mediaType = "tv") }
                
                val combined = movies + tvs
                
                if (reset) {
                    apiData = combined
                } else {
                    apiData = apiData + combined
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                if (!reset) currentPage-- // Revert page on failure
            } finally {
                if (reset) isLoading = false else isPaginating = false
            }
        }
    }
    
    fun loadMore() {
        if (currentCategory == "trending") {
            fetchData(reset = false)
        } else {
            // Check if currentCategory is a valid genre ID (digits) just in case, 
            // though we control it.
            fetchByGenre(currentCategory, reset = false)
        }
    }

    var trailerKey by mutableStateOf<String?>(null)
        private set

    fun fetchMovieDetails(type: String, id: Int) {
        viewModelScope.launch {
            isLoading = true
            selectedMovie = null
            director = null
            directorId = null
            trailerKey = null // Reset trailer key
            try {
                selectedMovie = RetrofitInstance.api.getDetails(type, id, apiKey)
                val creditsResponse = RetrofitInstance.api.getCredits(type, id, apiKey)
                credits = creditsResponse.cast
                
                val directorItem = creditsResponse.crew.find { it.job == "Director" }
                director = directorItem?.name
                directorId = directorItem?.id
                directorProfilePath = directorItem?.profilePath

                // Fetch Videos for Trailer
                val videoResponse = RetrofitInstance.api.getVideos(type, id, apiKey)
                val trailer = videoResponse.results.find { it.site == "YouTube" && it.type == "Trailer" } 
                    ?: videoResponse.results.find { it.site == "YouTube" && it.type == "Teaser" } // Fallback to Teaser
                trailerKey = trailer?.key
                
                val similar = RetrofitInstance.api.getSimilar(type, id, apiKey)
                recommendations = similar.results.map { it.copy(mediaType = type) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    
    // Pagination variables
    private var allPersonCredits: List<Movie> = emptyList()

    fun fetchPersonDetails(id: Int) {
        viewModelScope.launch {
            isLoading = true
            personDetails = null
            personCredits = emptyList()
            allPersonCredits = emptyList()
            try {
                personDetails = RetrofitInstance.api.getPersonDetail(id, apiKey)
                val creditsResponse = RetrofitInstance.api.getPersonCredits(id, apiKey)
                
                // Correct mapping for person credits using PersonCast
                val mappedCredits = creditsResponse.cast.map { 
                     Movie(
                        id = it.id,
                        title = it.title,
                        name = it.name,
                        posterPath = it.posterPath, 
                        backdropPath = null, // Not typically needed for the grid card
                        overview = null,
                        voteAverage = it.voteAverage ?: 0.0,
                        releaseDate = it.releaseDate,
                        firstAirDate = it.firstAirDate,
                        mediaType = it.mediaType ?: "movie"
                    )
                }
                
                allPersonCredits = mappedCredits
                // Load initial batch (12 items)
                personCredits = allPersonCredits.take(12)
                
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    
    fun loadMorePersonCredits() {
        if (personCredits.size >= allPersonCredits.size) return
        
        val nextBatch = allPersonCredits.drop(personCredits.size).take(12)
        personCredits = personCredits + nextBatch
    }
    
    fun search(query: String) {
        viewModelScope.launch {
             isLoading = true
             try {
                val movieResponse = RetrofitInstance.api.searchMovies(apiKey, query, 1)
                val movies = movieResponse.results.map { it.copy(mediaType = "movie") }
                
                val tvResponse = RetrofitInstance.api.searchTV(apiKey, query, 1)
                val tvs = tvResponse.results.map { it.copy(mediaType = "tv") }
                
                searchResults = movies + tvs
             } catch (e: Exception) {
                 e.printStackTrace()
             } finally {
                 isLoading = false
             }
        }
    }

    // Separate fetch for GenreScreen to avoid conflicting with HomeScreen pagination
    fun fetchGenreMovies(genreId: String) {
        viewModelScope.launch {
            isLoading = true
            genreMovies = emptyList()
            try {
                // Fetch just the first page for now for simplicity, or implement separate pagination if needed
                val movieResponse = RetrofitInstance.api.getMoviesByGenre(apiKey, genreId, 1)
                val movies = movieResponse.results.map { it.copy(mediaType = "movie") }
                
                val tvResponse = RetrofitInstance.api.getTVByGenre(apiKey, genreId, 1)
                val tvs = tvResponse.results.map { it.copy(mediaType = "tv") }
                
                genreMovies = movies + tvs
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    init {
        loadHistory()
        loadFavorites()
        fetchData(reset = true)
    }

    private fun saveFavorites() {
        val json = gson.toJson(favorites.value)
        prefs.edit().putString("favorites_list", json).apply()
    }

    private fun loadFavorites() {
        val json = prefs.getString("favorites_list", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Movie>>() {}.type
                favorites.value = gson.fromJson(json, type)
            } catch (e: Exception) {
                e.printStackTrace()
                favorites.value = emptyList()
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        val currentFavorites = favorites.value.toMutableList()
        val existing = currentFavorites.find { it.id == movie.id }
        
        if (existing != null) {
            currentFavorites.remove(existing)
        } else {
            currentFavorites.add(movie)
        }
        favorites.value = currentFavorites
        saveFavorites()
    }
    
    fun isFavorite(movieId: Int): Boolean {
        return favorites.value.any { it.id == movieId }
    }
}
