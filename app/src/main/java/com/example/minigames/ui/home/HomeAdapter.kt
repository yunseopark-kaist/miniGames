package com.example.minigames.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minigames.R

data class GameItem (val type: String, val name: String, val playtime: Int, val progress: Int)

class HomeAdapter(
    private var items: List<GameItem>
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: GameItem) {
            val nameView = itemView.findViewById<TextView>(R.id.game_name)
            val playtimeView = itemView.findViewById<TextView>(R.id.play_time)
            val progressView = itemView.findViewById<TextView>(R.id.progress_percent)
            nameView.text = item.name
            playtimeView.text = item.playtime.toString()
            progressView.text = item.progress.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.game_list_item, parent, false)
        return HomeViewHolder(v)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<GameItem>) {
        items = newItems
        notifyDataSetChanged() // 어댑터 데이터 변경 알림
    }
}