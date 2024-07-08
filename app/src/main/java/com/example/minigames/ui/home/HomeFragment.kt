package com.example.minigames.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minigames.databinding.FragmentHomeBinding
import com.example.minigames.ui.sudoku.SudokuActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: HomeAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = HomeAdapter(emptyList(), { gameItem -> openGame(gameItem) }, { gameItem -> deleteGame(gameItem) })
        binding.gameList.adapter = adapter
        binding.gameList.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.gameList.observe(viewLifecycleOwner) { games ->
            adapter.updateData(games)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadGameList(requireContext().filesDir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openGame(gameItem: GameItem) {
        val intent = Intent(requireContext(), SudokuActivity::class.java)
        intent.putExtra("GAME_NAME", gameItem.name)
        startActivity(intent)
    }

    private fun deleteGame(gameItem: GameItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Game")
            .setMessage("Are you sure you want to delete this game?")
            .setPositiveButton("Yes") { _, _ ->
                homeViewModel.deleteGame(requireContext().filesDir, gameItem.name)
            }
            .setNegativeButton("No", null)
            .show()
    }
}