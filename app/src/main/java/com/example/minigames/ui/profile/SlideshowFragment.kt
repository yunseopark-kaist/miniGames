package com.example.minigames.ui.profile

import android.os.Bundle
import android.provider.ContactsContract.Directory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.minigames.ProfileViewModel
import com.example.minigames.R
import com.example.minigames.databinding.FragmentSlideshowBinding
import com.example.minigames.server.model.SaveGameDto
import com.example.minigames.server.viewmodel.GameViewModel
import com.example.minigames.server.viewmodel.UserViewModel
import com.google.gson.Gson
import java.io.File

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val gameViewModel: GameViewModel by viewModels()
    val profileViewModel: ProfileViewModel by activityViewModels()
    val userViewModel: UserViewModel by activityViewModels()
    private val gson = Gson()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val saveBtn = binding.buttonSave
        val loadBtn = binding.buttonLoad
        val editBtn = binding.buttonEdit
        val root: View = binding.root

        val userId = profileViewModel.kakaoId.toString()
        val nickname = profileViewModel.nickname
        val profileImage = profileViewModel.profileImageUrl

        binding.profileId.text = userId
        binding.profileName.text = nickname
        Glide.with(this)
            .load(profileImage)
            .into(binding.profileImage)

        saveBtn.setOnClickListener {
            Log.d("SaveBtn", "button clicked")

            val directory = File(requireContext().filesDir.toString())
            val jsonFiles = directory.listFiles { file -> file.extension == "json" }

            if (jsonFiles.isNullOrEmpty()) {
                Log.d("SaveBtn", "No JSON files found")
            } else {
                for (file in jsonFiles) {
                    val gameName = file.nameWithoutExtension
                    val gameState = file.readText()
                    val saveGameDto = SaveGameDto(userId, gameName, gameState)
                    gameViewModel.saveGame(saveGameDto)
                }
            }
        }

        loadBtn.setOnClickListener {
            Log.d("LoadBtn", "button clicked")
            gameViewModel.loadGames(userId)
        }

        editBtn.setOnClickListener {
            showEditNicknameDialog()
        }

        gameViewModel.loadGameResult.observe(viewLifecycleOwner) { games ->
            games?.let {
                for (game in games) {
                    Log.d("saving loaded game", "gamename: ${game.name}")
                    val fileName = "${game.name}.json"
                    val file = File(requireContext().filesDir, fileName)
                    file.writeText(game.gameState)
                }
                Log.d("LoadBtn", "Games loaded and saved locally")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showEditNicknameDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val editTextNickname = dialogView.findViewById<EditText>(R.id.editTextNickname)
        val buttonConfirm = dialogView.findViewById<Button>(R.id.buttonConfirm)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        buttonConfirm.setOnClickListener {
            val newNickname = editTextNickname.text.toString()
            if (newNickname.isNotEmpty()) {
                userViewModel.updateUser(profileViewModel.kakaoId?.toInt()?: 0, newNickname)
                profileViewModel.nickname = newNickname
                binding.profileName.text = newNickname
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}