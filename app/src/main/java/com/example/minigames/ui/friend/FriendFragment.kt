package com.example.minigames.ui.friend

import SharedGameViewModel
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minigames.ProfileViewModel
import com.example.minigames.databinding.FragmentFriendBinding
import com.example.minigames.server.model.SharedGameDto
import com.example.minigames.server.viewmodel.RelationshipViewModel
import com.example.minigames.server.viewmodel.UserViewModel
import com.example.minigames.ui.home.GameItem
import com.example.minigames.ui.home.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class FriendFragment : Fragment() {

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val sharedGameViewModel: SharedGameViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var relationshipViewModel: RelationshipViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        relationshipViewModel = ViewModelProvider(this).get(RelationshipViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUserId = profileViewModel.kakaoId?.toInt()?: 0
        binding.tvNickname.text = profileViewModel.nickname
        userViewModel.getUsers()
        relationshipViewModel.fetchFriends(currentUserId)

        relationshipViewModel.friendsLiveData.observe(viewLifecycleOwner, Observer { fids ->

        })

        // 버튼 클릭 리스너 설정
        binding.btnAddFriend.setOnClickListener {
            val nickname = binding.searchNickname.text.toString()
            val user = userViewModel.searchUserByNickname(nickname)
            if (user != null) {
                addFriend(user.id)
            } else {
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRemoveFriend.setOnClickListener {
        }

        binding.btnFriendRequests.setOnClickListener {

        }

        binding.btnSharedGame.setOnClickListener{
            val userId = profileViewModel.kakaoId.toString()
            sharedGameViewModel.getSharedGames(userId)
        }

        sharedGameViewModel.sharedGamesLiveData.observe(viewLifecycleOwner, Observer { sharedGames ->
            sharedGames?.let {
                showSharedGamesDialog(it)
            }
        })

        val friendList = listOf(
            Friend("1", "Friend1", 100),
            Friend("2","Friend2", 150),
            Friend("3","Friend3", 200))

        val adapter = FriendAdapter(friendList) { friend ->
            showShareGameDialog(friend)
        }


        binding.recyclerViewFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFriends.adapter = adapter
    }

    private fun addFriend(userId: Int) {
        // 현재 사용자의 ID를 가져오는 방법은 상황에 따라 다를 수 있습니다.
        val currentUserId = profileViewModel.kakaoId?.toInt() // 예: 하드코딩된 ID, 실제로는 로그인한 사용자의 ID를 사용해야 합니다.
        if(currentUserId == null) return
        Log.d("addFriend", "try to add friend $userId")
        relationshipViewModel.createRequest(currentUserId, userId)
        Toast.makeText(requireContext(), "친구신청을 완료했습니다.", Toast.LENGTH_SHORT).show()


    }

    private fun removeFriend(friend: Friend) {
        val userId = profileViewModel.kakaoId?.toInt()?: 0 // 실제 유저 ID로 변경
        relationshipViewModel.removeFriend(userId, friend.id.toInt()) { success ->
            if (success) {
                Toast.makeText(context, "Friend removed successfully", Toast.LENGTH_SHORT).show()
                relationshipViewModel.fetchFriends(userId)
            } else {
                Toast.makeText(context, "Failed to remove friend", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showShareGameDialog(friend: Friend) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Share Game with ${friend.nickname}")

        // ViewModel에서 게임 리스트 가져오기
        val games = homeViewModel.gameList.value ?: listOf()
        // GameItem의 name을 CharSequence 배열로 변환
        val gamesArray = games.map { it.name }.toTypedArray()
        var selectedGame: GameItem? = null

        builder.setSingleChoiceItems(gamesArray, -1) { _, which ->
            selectedGame = games[which]
        }

        builder.setPositiveButton("Share") { _, _ ->
            selectedGame?.let {
                shareGameWithFriend(friend, it.name)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showSharedGamesDialog(sharedGames: List<SharedGameDto>) {
        val gameNames = sharedGames.map { it.name }.toTypedArray()
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Download Shared Game")
            .setItems(gameNames) { _, which ->
                val selectedGame = sharedGames[which]
                saveGameLocally(selectedGame)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun shareGameWithFriend(friend: Friend, gameName: String) {
        val sharedgame = File(requireContext().filesDir, "$gameName.json")
        if(!sharedgame.exists()){
            Toast.makeText(requireContext(), "게임을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = profileViewModel.kakaoId.toString()
        Log.d("share game", "my id: $userId")
        val shareduserId = friend.id
        Log.d("share game", "friend id: $shareduserId")
        val gameState = sharedgame.readText()
        sharedGameViewModel.shareGame(shareduserId, userId, gameName, gameState)
        Toast.makeText(requireContext(), "${friend.nickname}님에게 게임을 공유했습니다.", Toast.LENGTH_SHORT).show()


    }

    fun saveGameLocally(game: SharedGameDto) {
        // 로컬 저장소에 저장하는 코드 작성
        val fileName = "${game.name}.json"
        val file = File(requireContext().filesDir, fileName)
        file.writeText(game.gameState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}