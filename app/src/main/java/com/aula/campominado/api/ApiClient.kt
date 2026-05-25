package com.aula.campominado.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    /**
     * Padrão XAMPP: C:\xampp\htdocs\projeto_campominado_api\listar.php
     * Emulador: 10.0.2.2 = localhost do PC.
     * Celular físico: use o IP da máquina (ex: 192.168.15.100).
     */
    private const val BASE_URL = "http://192.168.15.100/campominado/"
    // private const val BASE_URL = "http://192.168.15.100/projeto_campominado_api/"

    val api: CampoMinadoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CampoMinadoApi::class.java)
    }
}
