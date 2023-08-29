package br.com.drawiin.myapplication.data

import retrofit2.http.GET

interface Service {
    @GET("character")
    suspend fun getCharacters(): CharacterResponse
}