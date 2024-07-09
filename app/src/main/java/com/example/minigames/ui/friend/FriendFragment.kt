package com.example.minigames.ui.friend

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minigames.databinding.FragmentFriendBinding

class FriendManagementFragment : Fragment() {

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 예시 데이터 설정
        binding.tvNickname.text = "MyNickname"

        // 버튼 클릭 리스너 설정
        binding.btnAddFriend.setOnClickListener {
            // Add Friend 버튼 클릭 시 동작
        }

        binding.btnRemoveFriend.setOnClickListener {
            // Remove Friend 버튼 클릭 시 동작
        }

        binding.btnFriendRequests.setOnClickListener {
            // Friend Requests 버튼 클릭 시 동작
        }

        // 리사이클러뷰 설정
        val friendList = listOf(
            Friend("Friend1", 100),
            Friend("Friend2", 150),
            Friend("Friend3", 200)
        )
        val adapter = FriendAdapter(friendList)
        binding.recyclerViewFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFriends.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}