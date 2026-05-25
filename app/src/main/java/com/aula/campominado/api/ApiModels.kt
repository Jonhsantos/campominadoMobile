package com.aula.campominado.api

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class Partida(
    val id: Int?,
    @SerializedName("nome_jogador") val nomeJogador: String,
    val pontuacao: Int,
    @SerializedName("data_partida") val dataPartida: String?
)

data class SalvarPartidaRequest(
    @SerializedName("nome_jogador") val nomeJogador: String,
    val pontuacao: Int
)

data class PartidaSalva(
    val id: Int?,
    @SerializedName("nome_jogador") val nomeJogador: String,
    val pontuacao: Int
)
