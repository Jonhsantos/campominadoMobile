package com.aula.campominado.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aula.campominado.api.Partida
import com.aula.campominado.databinding.ItemPartidaBinding

class PartidaAdapter : RecyclerView.Adapter<PartidaAdapter.PartidaViewHolder>() {

    private val partidas = mutableListOf<Partida>()

    fun submitList(novaLista: List<Partida>) {
        partidas.clear()
        partidas.addAll(novaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidaViewHolder {
        val binding = ItemPartidaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PartidaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartidaViewHolder, position: Int) {
        holder.bind(partidas[position])
    }

    override fun getItemCount(): Int = partidas.size

    class PartidaViewHolder(
        private val binding: ItemPartidaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(partida: Partida) {
            binding.tvItemNome.text = partida.nomeJogador
            binding.tvItemPontuacao.text =
                binding.root.context.getString(
                    com.aula.campominado.R.string.pontuacao_item,
                    partida.pontuacao
                )
            binding.tvItemData.text = partida.dataPartida ?: "-"
        }
    }
}
