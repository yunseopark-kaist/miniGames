package com.example.minigames.ui.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.minigames.databinding.ItemFriendBinding

data class Friend(val id: String, val nickname: String, val score: Int)

class FriendAdapter(private var friends: List<Friend>, private val onFriendClick: (Friend) -> Unit) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
        holder.itemView.setOnClickListener{
            onFriendClick(friend)
        }
    }

    fun updateFriends(newFriends: List<Friend>) {
        friends = newFriends
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = friends.size

    class FriendViewHolder(private val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.tvNickname.text = friend.nickname
            binding.tvScore.text = "Score: ${friend.score}"
            // 추가적으로 친구의 프로필 사진 설정 가능
        }
    }
}