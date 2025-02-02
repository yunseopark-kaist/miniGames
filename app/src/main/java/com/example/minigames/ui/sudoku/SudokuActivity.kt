package com.example.minigames.ui.sudoku

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.minigames.ProfileViewModel
import com.example.minigames.R
import com.example.minigames.databinding.ActivitySudokuBinding
import com.example.minigames.server.viewmodel.UserViewModel
import java.io.File

class SudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var binding: ActivitySudokuBinding
    private val viewModel: SudokuViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var gameName: String
    private lateinit var numberButtons: List<Button>
    private var userId: Long?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameName = intent.getStringExtra("GAME_NAME")?: "default_game"

        userId= profileViewModel.kakaoId
        // Restore game state if available
        restoreGame()

        binding.sudokuBoardView.registerListener(this)

        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, Observer { updateHighlightedKeys(it) })
        viewModel.sudokuGame.isSolvedLiveData.observe(this, Observer { showClearDialog(it) })
        viewModel.sudokuGame.isFailedLiveData.observe(this, Observer { showFailedDialog(it) })
        viewModel.sudokuGame.incorrectAttemptsLiveData.observe(this, Observer { updateIncorrectAttempts(it) })
        viewModel.sudokuGame.scoreLiveData.observe(this, Observer { updateScore(it) })
        viewModel.sudokuGame.timeTakenLiveData.observe(this, Observer { updateTimeTaken(it) })

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

    override fun onPause(){
        super.onPause()
        val solved = viewModel.sudokuGame.isSolvedLiveData.value?: false
        val failed = viewModel.sudokuGame.isFailedLiveData.value?: false
        if(solved||failed)
            return

        viewModel.sudokuGame.saveGameState(this, gameName)
    }

    private fun restoreGame() {
        val savedGameState = viewModel.sudokuGame.loadGameState(this, gameName)

        if (savedGameState != null) {
            // ViewModel에 데이터 설정
            viewModel.sudokuGame.restoreGameState(savedGameState)
        }
    }

    private fun deleteSavedGame() {
        val file = File(filesDir, "$gameName.json")
        if (file.exists()) {
            file.delete()
            Log.d("deleteSavedGame", "deleted successfully")
        }

    }

    private fun updateCells(cells: List<Cell>?) {
        cells?.let {
            binding.sudokuBoardView.updateCells(it)
        }
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) {
        cell?.let {
            binding.sudokuBoardView.updateSelectedCellUI(it.first, it.second)
        }
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) {
        isNoteTaking?.let {
            val color = if (it) ContextCompat.getColor(this, R.color.colorPrimary) else android.graphics.Color.LTGRAY
            binding.notesButton.background.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    private fun updateHighlightedKeys(set: Set<Int>?) {
        set?.let {
            numberButtons.forEachIndexed { index, button ->
                val color = if (it.contains(index + 1)) ContextCompat.getColor(this, R.color.colorPrimary) else android.graphics.Color.LTGRAY
                button.background.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    private val userViewModel: UserViewModel by viewModels()

    private fun showClearDialog(isSolved: Boolean?) {
        if (isSolved == true) {

            val score = viewModel.sudokuGame.scoreLiveData.value ?: 0
            val incorrectAttempts = viewModel.sudokuGame.incorrectAttemptsLiveData.value ?: 0
            val timeTaken = viewModel.sudokuGame.timeTakenLiveData.value ?: 0

            val minutes = (timeTaken / 60000).toInt()
            val seconds = (timeTaken / 1000 % 60).toInt()

            userId?.let{id->
                userViewModel.userScoreUp(id.toInt(), score)
            }

            AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage(
                    "You completed the Sudoku puzzle!\n\n" +
                            "Time taken: ${minutes}m ${seconds}s\n" +
                            "Incorrect attempts: $incorrectAttempts\n" +
                            "Score: $score"
                )
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    deleteSavedGame() // Delete the saved game on success
                    finish()
                }
                .show()
        }
    }

    private fun showFailedDialog(isFailed: Boolean?) {
        if (isFailed == true) {
            AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("You have made 4 incorrect attempts. Game Over.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    deleteSavedGame() // Delete the saved game on failure
                    finish()
                }
                .show()
        }
    }

    private fun updateIncorrectAttempts(incorrectAttempts: Int?) {
        // UI 업데이트가 필요할 경우 여기에 추가
    }

    private fun updateScore(score: Int?) {
        // UI 업데이트가 필요할 경우 여기에 추가
    }

    private fun updateTimeTaken(timeTaken: Long?) {
        timeTaken?.let {
            val minutes = (it / 60000).toInt()
            val seconds = (it / 1000 % 60).toInt()
            binding.timeTextView.text = String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}
