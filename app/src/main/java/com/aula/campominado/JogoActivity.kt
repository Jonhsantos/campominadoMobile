package com.aula.campominado

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aula.campominado.api.ApiClient
import com.aula.campominado.api.SalvarPartidaRequest
import com.aula.campominado.databinding.ActivityJogoBinding
import com.aula.campominado.game.CellState
import com.aula.campominado.game.MinesweeperGame
import com.aula.campominado.util.Constants
import com.aula.campominado.util.PlayerPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJogoBinding
    private lateinit var nomeJogador: String
    private val game = MinesweeperGame()
    private val cellButtons = mutableListOf<Button>()
    private var partidaSalva = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nomeJogador = intent.getStringExtra(Constants.EXTRA_NOME_JOGADOR)
            ?: PlayerPrefs.getNome(this).orEmpty()

        binding.headerJogador.tvNomeJogador.text =
            getString(R.string.jogador_label, nomeJogador)

        binding.btnNovoJogo.setOnClickListener { iniciarNovoJogo() }
        binding.btnVoltarMenu.setOnClickListener { finish() }

        iniciarNovoJogo()
    }

    private fun iniciarNovoJogo() {
        game.reset()
        partidaSalva = false
        binding.tvStatus.setText(R.string.toque_revelar)
        criarTabuleiro()
        atualizarPontuacao()
    }

    private fun criarTabuleiro() {
        binding.gridTabuleiro.removeAllViews()
        cellButtons.clear()

        val cellSize = resources.displayMetrics.density * 36

        for (row in 0 until game.rows) {
            for (col in 0 until game.cols) {
                val btn = Button(this).apply {
                    text = ""
                    gravity = Gravity.CENTER
                    setBackgroundColor(ContextCompat.getColor(context, R.color.celula_oculta))
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = cellSize.toInt()
                        height = cellSize.toInt()
                        rowSpec = GridLayout.spec(row)
                        columnSpec = GridLayout.spec(col)
                        setMargins(2, 2, 2, 2)
                    }
                    setOnClickListener { onCellClick(row, col) }
                    setOnLongClickListener {
                        onCellLongClick(row, col)
                        true
                    }
                }
                cellButtons.add(btn)
                binding.gridTabuleiro.addView(btn)
            }
        }
    }

    private fun onCellClick(row: Int, col: Int) {
        if (game.gameOver) return
        game.reveal(row, col)
        atualizarTabuleiro()
        atualizarPontuacao()
        if (game.gameOver) {
            finalizarPartida()
        }
    }

    private fun onCellLongClick(row: Int, col: Int) {
        if (game.gameOver) return
        game.toggleFlag(row, col)
        atualizarTabuleiro()
    }

    private fun atualizarPontuacao() {
        binding.tvPontuacao.text =
            getString(R.string.pontuacao_atual, game.calculateScore())
    }

    private fun atualizarTabuleiro() {
        var index = 0
        for (row in 0 until game.rows) {
            for (col in 0 until game.cols) {
                val btn = cellButtons[index++]
                val cell = game.getCell(row, col)
                when (cell.state) {
                    CellState.HIDDEN -> {
                        btn.text = ""
                        btn.setBackgroundColor(
                            ContextCompat.getColor(this, R.color.celula_oculta)
                        )
                        btn.isEnabled = !game.gameOver
                    }
                    CellState.FLAGGED -> {
                        btn.text = "🚩"
                        btn.setBackgroundColor(
                            ContextCompat.getColor(this, R.color.celula_bandeira)
                        )
                        btn.isEnabled = !game.gameOver
                    }
                    CellState.REVEALED -> {
                        btn.isEnabled = false
                        if (cell.isMine) {
                            btn.text = "💣"
                            btn.setBackgroundColor(Color.parseColor("#B71C1C"))
                        } else if (cell.adjacentMines > 0) {
                            btn.text = cell.adjacentMines.toString()
                            btn.setTextColor(corNumero(cell.adjacentMines))
                            btn.setBackgroundColor(
                                ContextCompat.getColor(this, R.color.celula_revelada)
                            )
                        } else {
                            btn.text = ""
                            btn.setBackgroundColor(
                                ContextCompat.getColor(this, R.color.celula_revelada)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun corNumero(num: Int): Int = when (num) {
        1 -> Color.parseColor("#1565C0")
        2 -> Color.parseColor("#2E7D32")
        3 -> Color.parseColor("#C62828")
        4 -> Color.parseColor("#6A1B9A")
        5 -> Color.parseColor("#E65100")
        else -> Color.parseColor("#37474F")
    }

    private fun finalizarPartida() {
        val pontuacao = game.calculateScore()
        val mensagem = if (game.won) {
            binding.tvStatus.setText(R.string.vitoria)
            getString(R.string.msg_vitoria, pontuacao)
        } else {
            binding.tvStatus.setText(R.string.derrota)
            getString(R.string.msg_derrota, pontuacao)
        }

        atualizarTabuleiro()
        salvarPartida(pontuacao)

        AlertDialog.Builder(this)
            .setTitle(if (game.won) R.string.vitoria else R.string.derrota)
            .setMessage(mensagem)
            .setPositiveButton(R.string.novo_jogo) { _, _ -> iniciarNovoJogo() }
            .setNegativeButton(R.string.voltar_menu) { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun salvarPartida(pontuacao: Int) {
        if (partidaSalva) return
        partidaSalva = true

        ApiClient.api.salvarPartida(
            body = SalvarPartidaRequest(nomeJogador, pontuacao)
        ).enqueue(object : Callback<com.aula.campominado.api.ApiResponse<com.aula.campominado.api.PartidaSalva>> {
            override fun onResponse(
                call: Call<com.aula.campominado.api.ApiResponse<com.aula.campominado.api.PartidaSalva>>,
                response: Response<com.aula.campominado.api.ApiResponse<com.aula.campominado.api.PartidaSalva>>
            ) {
                if (!response.isSuccessful || response.body()?.success != true) {
                    Toast.makeText(
                        this@JogoActivity,
                        R.string.erro_salvar_partida,
                        Toast.LENGTH_LONG
                    ).show()
                    partidaSalva = false
                }
            }

            override fun onFailure(
                call: Call<com.aula.campominado.api.ApiResponse<com.aula.campominado.api.PartidaSalva>>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@JogoActivity,
                    getString(R.string.erro_conexao, t.message ?: ""),
                    Toast.LENGTH_LONG
                ).show()
                partidaSalva = false
            }
        })
    }
}
