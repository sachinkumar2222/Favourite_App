package com.example.favourite.data

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieResponse

    @GET("tv/popular")
    suspend fun getPopularTV(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): MovieResponse

    @GET("search/tv")
    suspend fun searchTV(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): MovieResponse
    
    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String,
        @Query("page") page: Int
    ): MovieResponse
    
    @GET("discover/tv")
    suspend fun getTVByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String,
        @Query("page") page: Int
    ): MovieResponse

    @GET("{type}/{id}")
    suspend fun getDetails(
        @retrofit2.http.Path("type") type: String,
        @retrofit2.http.Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): MovieDetail

    @GET("{type}/{id}/credits")
    suspend fun getCredits(
        @retrofit2.http.Path("type") type: String,
        @retrofit2.http.Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): CreditsResponse

    @GET("{type}/{id}/similar")
    suspend fun getSimilar(
        @retrofit2.http.Path("type") type: String,
        @retrofit2.http.Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): MovieResponse

    @GET("person/{id}")
    suspend fun getPersonDetail(
        @retrofit2.http.Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): PersonDetail

    @GET("person/{id}/combined_credits")
    suspend fun getPersonCredits(
        @retrofit2.http.Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): PersonCreditsResponse

    @GET("{type}/{id}/videos")
    suspend fun getVideos(
        @retrofit2.http.Path("type") type: String,
        @retrofit2.http.Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): VideoResponse
}
