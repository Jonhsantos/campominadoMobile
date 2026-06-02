package com.aula.campominado.api

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class Partida(
    val id: Int?,
    val jogador: String,
    val pontuacao: Int,
    @SerializedName("data_partida") val dataPartida: String?
)

data class SalvarPartidaRequest(
    val jogador: String,
    val pontuacao: Int
)

data class PartidaSalva(
    val id: Int?,
    val jogador: String,
    val pontuacao: Int
)
