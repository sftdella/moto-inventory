package com.example.motoinventory.ui.users

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motoinventory.R
import com.example.motoinventory.data.entity.Aktivitas
import com.example.motoinventory.data.entity.User
import com.example.motoinventory.databinding.DialogUserBinding
import com.example.motoinventory.databinding.FragmentUserManagementBinding
import com.example.motoinventory.utils.SessionManager
import com.example.motoinventory.viewmodel.AktivitasViewModel
import com.example.motoinventory.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class UserManagementFragment : Fragment() {

    private var _binding: FragmentUserManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()
    private val aktivitasViewModel: AktivitasViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        observeData()

        binding.btnAddUser.setOnClickListener { showUserDialog(null) }
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            onEdit = { user -> showUserDialog(user) },
            onResetPassword = { user -> showResetPasswordDialog(user) },
            onDelete = { user -> confirmDelete(user) }
        )
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun observeData() {
        viewModel.allUsers.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
            binding.tvUserCount.text = "${users.size} pengguna aktif"
        }
    }

    private fun showUserDialog(existingUser: User?) {
        val dialogBinding = DialogUserBinding.inflate(layoutInflater)
        val isEdit = existingUser != null

        // Default: Staff chip selected
        dialogBinding.chipStaff.isChecked = true

        if (isEdit) {
            dialogBinding.etNama.setText(existingUser!!.nama)
            dialogBinding.etUsername.setText(existingUser.username)
            dialogBinding.tilPassword.hint = "Password (kosongkan jika tidak berubah)"
            when (existingUser.role) {
                User.ROLE_ADMIN -> dialogBinding.chipAdmin.isChecked = true
                User.ROLE_MANAGER -> dialogBinding.chipManager.isChecked = true
                else -> dialogBinding.chipStaff.isChecked = true
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (isEdit) "Edit Pengguna" else "Tambah Pengguna")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.etNama.text.toString().trim()
                val username = dialogBinding.etUsername.text.toString().trim()
                val password = dialogBinding.etPassword.text.toString()
                val role = when (dialogBinding.chipGroupRole.checkedChipId) {
                    R.id.chip_admin -> User.ROLE_ADMIN
                    R.id.chip_manager -> User.ROLE_MANAGER
                    else -> User.ROLE_STAFF
                }

                if (nama.isEmpty() || username.isEmpty()) {
                    Toast.makeText(requireContext(), "Nama dan username wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (!isEdit && password.isEmpty()) {
                    Toast.makeText(requireContext(), "Password wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (isEdit) {
                    val updated = existingUser!!.copy(nama = nama, username = username, role = role)
                    viewModel.updateUser(updated)
                    if (password.isNotEmpty()) viewModel.resetPassword(existingUser.id, password)
                    logAktivitas(Aktivitas.AKSI_EDIT_USER, "Edit user: $username")
                } else {
                    lifecycleScope.launch {
                        if (!viewModel.isUsernameAvailable(username)) {
                            Toast.makeText(requireContext(), "Username sudah digunakan", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        viewModel.insertUser(nama, username, password, role)
                        logAktivitas(Aktivitas.AKSI_TAMBAH_USER, "Tambah user: $username ($role)")
                        Toast.makeText(requireContext(), "User berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showResetPasswordDialog(user: User) {
        val input = android.widget.EditText(requireContext()).apply {
            hint = "Password baru"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(48, 32, 48, 32)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Reset Password")
            .setMessage("Reset password untuk ${user.nama}")
            .setView(input)
            .setPositiveButton("Reset") { _, _ ->
                val newPw = input.text.toString()
                if (newPw.length < 4) {
                    Toast.makeText(requireContext(), "Password minimal 4 karakter", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.resetPassword(user.id, newPw)
                logAktivitas(Aktivitas.AKSI_EDIT_USER, "Reset password: ${user.username}")
                Toast.makeText(requireContext(), "Password berhasil direset", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmDelete(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Pengguna")
            .setMessage("Hapus akun ${user.nama} (@${user.username})?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteUser(user)
                logAktivitas(Aktivitas.AKSI_HAPUS_USER, "Hapus user: ${user.username}")
                Toast.makeText(requireContext(), "Pengguna dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun logAktivitas(aksi: String, detail: String) {
        aktivitasViewModel.logAktivitas(
            sessionManager.getUserId(), sessionManager.getUsername(),
            sessionManager.getRole(), aksi, detail
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
