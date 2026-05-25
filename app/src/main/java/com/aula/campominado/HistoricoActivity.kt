package com.aula.campominado

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aula.campominado.api.ApiClient
import com.aula.campominado.api.ApiResponse
import com.aula.campominado.api.Partida
import com.aula.campominado.databinding.ActivityHistoricoBinding
import com.aula.campominado.ui.PartidaAdapter
import com.aula.campominado.util.Constants
import com.aula.campominado.util.PlayerPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoBinding
    private val adapter = PartidaAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nomeJogador = intent.getStringExtra(Constants.EXTRA_NOME_JOGADOR)
            ?: PlayerPrefs.getNome(this).orEmpty()

        binding.headerJogador.tvNomeJogador.text =
            getString(R.string.jogador_label, nomeJogador)

        binding.rvHistorico.layoutManager = LinearLayoutManager(this)
        binding.rvHistorico.adapter = adapter

        binding.btnVoltar.setOnClickListener { finish() }

        carregarHistorico()
    }

    private fun carregarHistorico() {
        binding.progressHistorico.visibility = View.VISIBLE
        binding.tvHistoricoVazio.visibility = View.GONE

        ApiClient.api.listarPartidas().enqueue(object : Callback<ApiResponse<List<Partida>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Partida>>>,
                response: Response<ApiResponse<List<Partida>>>
            ) {
                binding.progressHistorico.visibility = View.GONE
                val body = response.body()
                if (!response.isSuccessful || body?.success != true) {
                    Toast.makeText(
                        this@HistoricoActivity,
                        body?.message ?: getString(R.string.erro_carregar_historico),
                        Toast.LENGTH_LONG
                    ).show()
                    binding.tvHistoricoVazio.visibility = View.VISIBLE
                    return
                }
                val partidas = body.data.orEmpty()
                adapter.submitList(partidas)
                binding.tvHistoricoVazio.visibility =
                    if (partidas.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onFailure(call: Call<ApiResponse<List<Partida>>>, t: Throwable) {
                binding.progressHistorico.visibility = View.GONE
                binding.tvHistoricoVazio.visibility = View.VISIBLE
                Toast.makeText(
                    this@HistoricoActivity,
                    getString(R.string.erro_conexao, t.message ?: ""),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
