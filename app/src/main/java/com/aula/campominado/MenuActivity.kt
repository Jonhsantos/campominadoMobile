package com.aula.campominado

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aula.campominado.databinding.ActivityMenuBinding
import com.aula.campominado.util.Constants
import com.aula.campominado.util.PlayerPrefs

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var nomeJogador: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nomeJogador = intent.getStringExtra(Constants.EXTRA_NOME_JOGADOR)
            ?: PlayerPrefs.getNome(this).orEmpty()

        if (nomeJogador.isEmpty()) {
            startActivity(Intent(this, NomeActivity::class.java))
            finish()
            return
        }

        binding.headerJogador.tvNomeJogador.text =
            getString(R.string.jogador_label, nomeJogador)

        binding.btnJogar.setOnClickListener {
            startActivity(
                Intent(this, JogoActivity::class.java)
                    .putExtra(Constants.EXTRA_NOME_JOGADOR, nomeJogador)
            )
        }

        binding.btnHistorico.setOnClickListener {
            startActivity(
                Intent(this, HistoricoActivity::class.java)
                    .putExtra(Constants.EXTRA_NOME_JOGADOR, nomeJogador)
            )
        }
    }
}
