package com.example.motoinventory.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.motoinventory.MainActivity
import com.example.motoinventory.data.entity.Aktivitas
import com.example.motoinventory.databinding.ActivityLoginBinding
import com.example.motoinventory.utils.NotificationHelper
import com.example.motoinventory.utils.SessionManager
import com.example.motoinventory.viewmodel.AktivitasViewModel
import com.example.motoinventory.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val userViewModel: UserViewModel by viewModels()
    private val aktivitasViewModel: AktivitasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        NotificationHelper.createChannels(this)

        // Auto login if session active
        if (sessionManager.isLoggedIn()) {
            goToMain()
            return
        }

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener { doLogin() }

        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doLogin()
                true
            } else false
        }
    }

    private fun setupObservers() {
        userViewModel.loginResult.observe(this) { user ->
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true

            if (user != null) {
                sessionManager.saveSession(user)
                userViewModel.updateLastLogin(user.id)
                aktivitasViewModel.logAktivitas(
                    user.id, user.username, user.role,
                    Aktivitas.AKSI_LOGIN, "Login dari perangkat"
                )
                goToMain()
            } else {
                binding.tvError.text = "Username atau password salah"
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun doLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            binding.tvError.text = "Username dan password wajib diisi"
            binding.tvError.visibility = View.VISIBLE
            return
        }

        binding.tvError.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        userViewModel.authenticate(username, password)
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
