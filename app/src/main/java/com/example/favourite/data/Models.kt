package com.example.favourite.data

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String?,
    val name: String?,
    
    @SerializedName("poster_path")
    val posterPath: String?,
    
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    
    val overview: String?,
    
    @SerializedName("vote_average")
    val voteAverage: Double,
    
    @SerializedName("release_date")
    val releaseDate: String?,
    
    @SerializedName("first_air_date")
    val firstAirDate: String?,
    
    @SerializedName("media_type")
    val mediaType: String?
)

data class MovieDetail(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    val overview: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    val runtime: Int?,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>?,
    val genres: List<Genre>?,
    val status: String?,
    val budget: Long?,
    val tagline: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>?
)

data class ProductionCountry(
    @SerializedName("iso_3166_1") val iso31661: String,
    val name: String
)

data class Genre(
    val id: Int,
    val name: String
)

data class CreditsResponse(
    val cast: List<Cast>,
    val crew: List<Crew>
)

data class Cast(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    val character: String?
)

data class Crew(
    val id: Int,
    val name: String,
    val job: String,
    val department: String,
    @SerializedName("profile_path") val profilePath: String?
)

data class PersonDetail(
    val id: Int,
    val name: String,
    val biography: String?,
    @SerializedName("place_of_birth") val placeOfBirth: String?,
    val birthday: String?,
    @SerializedName("known_for_department") val knownForDepartment: String?,
    @SerializedName("profile_path") val profilePath: String?,
    val gender: Int?,
    @SerializedName("also_known_as") val alsoKnownAs: List<String>?,
    @SerializedName("imdb_id") val imdbId: String?,
    val homepage: String?
)

data class PersonCreditsResponse(
    val cast: List<PersonCast>
)

data class PersonCast(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    val character: String?,
    @SerializedName("media_type") val mediaType: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?
)

data class VideoResponse(
    val results: List<Video>
)

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)
