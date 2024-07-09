package com.example.minigames.ui.profile

import android.os.Bundle
import android.provider.ContactsContract.Directory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.minigames.ProfileViewModel
import com.example.minigames.databinding.FragmentSlideshowBinding
import com.example.minigames.server.model.SaveGameDto
import com.example.minigames.server.viewmodel.GameViewModel
import com.google.gson.Gson
import java.io.File

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val gameViewModel: GameViewModel by viewModels()
    val profileViewModel: ProfileViewModel by activityViewModels()
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
        val root: View = binding.root

        saveBtn.setOnClickListener {
            val userId = profileViewModel.kakaoId.toString()
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
            val userId = profileViewModel.kakaoId.toString()
            Log.d("LoadBtn", "button clicked")
            gameViewModel.loadGames(userId)
        }

        gameViewModel.loadGameResult.observe(viewLifecycleOwner) { games ->
            games?.let {
                for (game in it) {
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
}