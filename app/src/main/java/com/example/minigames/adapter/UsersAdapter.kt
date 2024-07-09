package com.example.minigames.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minigames.R
import com.example.minigames.server.model.User
//import com.squareup.picasso.Picasso

class UsersAdapter(private val users: List<User>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nicknameTextView: TextView = itemView.findViewById(R.id.nicknameTextView)
        private val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        private val rankingTextView: TextView = itemView.findViewById(R.id.rankingTextView)
        //private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)

        fun bind(user: User) {
            nicknameTextView.text = user.nickname
            scoreTextView.text = "Score: ${user.score}"
            rankingTextView.text = "Ranking: ${user.ranking}"
            /*if (!user.profileImageUrl.isNullOrEmpty()) {
                //Picasso.get().load(user.profileImageUrl).into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.default_profile_image)
            }*/
        }
    }
}