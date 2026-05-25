package com.aula.campominado

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aula.campominado.databinding.ActivityNomeBinding
import com.aula.campominado.util.Constants
import com.aula.campominado.util.PlayerPrefs

class NomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PlayerPrefs.getNome(this)?.let { nomeSalvo ->
            binding.etNome.setText(nomeSalvo)
        }

        binding.btnEntrar.setOnClickListener {
            val nome = binding.etNome.text?.toString()?.trim().orEmpty()
            if (nome.isEmpty()) {
                Toast.makeText(this, R.string.erro_nome_vazio, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PlayerPrefs.saveNome(this, nome)
            startActivity(
                Intent(this, MenuActivity::class.java)
                    .putExtra(Constants.EXTRA_NOME_JOGADOR, nome)
            )
            finish()
        }
    }
}
