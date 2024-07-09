package com.example.minigames.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.minigames.R
import com.example.minigames.databinding.GameListItemBinding

data class GameItem (val type: String, val name: String, val playtime: Long, val progress: Int)

class HomeAdapter(
    private var data: List<GameItem>,
    private val onClick: (GameItem) -> Unit,
    private val onLongClick: (GameItem) -> Unit
) : RecyclerView.Adapter<HomeAdapter.GameViewHolder>() {

    class GameViewHolder(val binding: GameListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GameListItemBinding.inflate(inflater, parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val gameItem = data[position]
        val percentage = gameItem.progress.toString()
        val minutes = (gameItem.playtime / 60000).toInt()
        val seconds = (gameItem.playtime / 1000 % 60).toInt()
        holder.binding.gameName.text = gameItem.name
        holder.binding.playTime.text = String.format("%02d:%02d", minutes, seconds)
        holder.binding.gameProgress.progress = gameItem.progress
        holder.binding.progressPercent.text = "$percentage%"
        holder.binding.root.setOnClickListener { onClick(gameItem) }

        holder.binding.root.setOnLongClickListener {
            onLongClick(gameItem)
            true
        }
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<GameItem>) {
        data = newData
        notifyDataSetChanged()
    }
}