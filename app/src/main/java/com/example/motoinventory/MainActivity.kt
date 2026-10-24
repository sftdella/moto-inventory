package com.example.motoinventory

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.motoinventory.databinding.ActivityMainBinding
import com.example.motoinventory.ui.login.LoginActivity
import com.example.motoinventory.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        // Redirect to login if not logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // Show/hide nav items based on role
        val menu = binding.bottomNavigation.menu
        menu.findItem(R.id.userManagementFragment)?.isVisible = sessionManager.isAdmin()
        menu.findItem(R.id.aktivitasFragment)?.isVisible = sessionManager.isAdmin()
        menu.findItem(R.id.laporanFragment)?.isVisible = sessionManager.canAccessLaporan()

        // Hide bottom nav on detail/add screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideOnScreens = setOf(
                R.id.addMotorFragment,
                R.id.detailMotorFragment
            )
            binding.bottomNavigation.visibility =
                if (destination.id in hideOnScreens) View.GONE else View.VISIBLE
        }
    }

    fun logout() {
        val sessionManager = SessionManager(this)
        sessionManager.clearSession()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
