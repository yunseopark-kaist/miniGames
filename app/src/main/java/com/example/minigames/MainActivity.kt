package com.example.minigames

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.minigames.databinding.ActivityMainBinding
import com.example.minigames.server.viewmodel.UserViewModel
import com.example.minigames.ui.home.HomeViewModel
import com.example.minigames.ui.sudoku.SudokuActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sudokuActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener {
            showCreateGameDialog()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_ranking, R.id.nav_profile, R.id.nav_friend
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (profileViewModel.isLoggedIn()) {
            // 유저가 로그인되어 있다면 메인 화면으로 이동
            checkIdValid(navView)
        }

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            showLogoutConfirmationDialog(navController, drawerLayout)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_login -> hideUIElements()
                else -> showUIElements()
            }
        }

        sudokuActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("mainactivity sudokuactivity ended", "update home view")
            // 리사이클러뷰 업데이트
            homeViewModel.loadGameList(this.filesDir)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.nav_home) {
                    finish()
                } else {
                    navController.navigateUp()
                }
            }
        })

    }

    private fun checkIdValid(navView: NavigationView){
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val id = profileViewModel.kakaoId
            userViewModel.isThereId(id?.toInt()?:0){ exists ->
                if(exists){
                    updateNavHeader(navView)
                    navController.navigate(R.id.action_login_to_home)
                } else{
                    Toast.makeText(this@MainActivity, "비정상적인 로그인 정보입니다.", Toast.LENGTH_SHORT).show()
                    profileViewModel.clearLoginInfo() // 유저 정보 클리어
                    navController.navigate(R.id.action_logout)
                }
            }
    }

    private fun showCreateGameDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Game Name")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val gameName = input.text.toString()
            createNewGame(gameName)
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun createNewGame(gameName: String) {
        val file = File(this.filesDir, "$gameName.json")
        if(file.exists()){
            Toast.makeText(this@MainActivity, "같은 이름의 게임이 진행중입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, SudokuActivity::class.java)
        intent.putExtra("GAME_NAME", gameName)
        sudokuActivityLauncher.launch(intent)
    }

    private fun hideUIElements() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.navView.visibility = View.GONE
        binding.appBarMain.fab.hide()
    }

    private fun showUIElements() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        binding.navView.visibility = View.VISIBLE
        binding.appBarMain.fab.show()
    }

    private fun updateNavHeader(navView: NavigationView) {
        val headerView = navView.getHeaderView(0)
        val nicknameTextView = headerView.findViewById<TextView>(R.id.nickname)
        val userIdTextView = headerView.findViewById<TextView>(R.id.user_id)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profile_image)

        nicknameTextView.text = profileViewModel.nickname
        userIdTextView.text = profileViewModel.kakaoId.toString()

        profileViewModel.profileImageUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .into(profileImageView)
        }
    }

    private fun showLogoutConfirmationDialog(navController: NavController, drawerLayout: DrawerLayout) {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                profileViewModel.clearLoginInfo() // 유저 정보 클리어
                navController.navigate(R.id.action_logout) // 로그인 화면으로 이동
                drawerLayout.closeDrawers() // 드로어 닫기
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}