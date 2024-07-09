package com.example.minigames.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minigames.databinding.FragmentHomeBinding
import com.example.minigames.ui.sudoku.SudokuActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: HomeAdapter
    lateinit var sudokuActivityLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomeAdapter(emptyList(), { gameItem -> openGame(gameItem) }, { gameItem -> deleteGame(gameItem) })
        binding.gameList.adapter = adapter
        binding.gameList.layoutManager = LinearLayoutManager(context)

        // Observe game list changes
        viewModel.gameList.observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })

        sudokuActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("homefragment sudokuactivity ended", "update home view")
            // 리사이클러뷰 업데이트
            viewModel.loadGameList(requireContext().filesDir)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadGameList(requireContext().filesDir)
    }

    private fun openGame(gameItem: GameItem) {
        val intent = Intent(requireContext(), SudokuActivity::class.java)
        intent.putExtra("GAME_NAME", gameItem.name)
        sudokuActivityLauncher.launch(intent)
    }

    private fun deleteGame(gameItem: GameItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Game")
            .setMessage("Are you sure you want to delete this game?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteGame(requireContext().filesDir, gameItem.name)
            }
            .setNegativeButton("No", null)
            .show()
    }
}