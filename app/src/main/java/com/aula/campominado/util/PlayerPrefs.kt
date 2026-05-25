package com.aula.campominado.util

import android.content.Context

object PlayerPrefs {
    private const val PREFS = "campo_minado_prefs"
    private const val KEY_NOME = "nome_jogador"

    fun saveNome(context: Context, nome: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NOME, nome)
            .apply()
    }

    fun getNome(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_NOME, null)
}
