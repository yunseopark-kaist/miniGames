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
        val saveBtn = binding.button
        val root: View = binding.root

        saveBtn.setOnClickListener {
            val userId = profileViewModel.kakaoId.toString()
            //val directory = context?.filesDir
            Log.d("SaveBtn", "button clicked")
            val gameName = "sudoku"
            val gameFile = File(requireContext().filesDir, "$gameName.json")
            if(!gameFile.exists())
                Log.d("SaveBtn", "file doesn't exist")
            val gameState = gameFile.readText()
            val saveGameDto = SaveGameDto(userId, gameName, gameState)
            gameViewModel.saveGame(saveGameDto)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}