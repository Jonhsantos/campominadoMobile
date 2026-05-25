package com.aula.campominado.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
interface CampoMinadoApi {

    @GET("listar.php")
    fun listarPartidas(): Call<ApiResponse<List<Partida>>>

    @POST("salvar.php")
    fun salvarPartida(@Body body: SalvarPartidaRequest): Call<ApiResponse<PartidaSalva>>
}
