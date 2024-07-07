package com.example.minigames.ui.sudoku

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.minigames.R
import com.example.minigames.databinding.ActivitySudokuBinding

class SudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var binding: ActivitySudokuBinding
    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sudokuBoardView.registerListener(this)

        viewModel = ViewModelProvider(this).get(PlaySudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, Observer { updateHighlightedKeys(it) })

        numberButtons = listOf(
            binding.oneButton, binding.twoButton, binding.threeButton,
            binding.fourButton, binding.fiveButton, binding.sixButton,
            binding.sevenButton, binding.eightButton, binding.nineButton
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }

        binding.notesButton.setOnClickListener { viewModel.sudokuGame.changeNoteTakingState() }
        binding.deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        binding.sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.colorPrimary) else android.graphics.Color.LTGRAY
        binding.notesButton.background.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.colorPrimary) else android.graphics.Color.LTGRAY
            button.background.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}