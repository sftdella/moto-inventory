package com.example.motoinventory.ui.users

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.motoinventory.R
import com.example.motoinventory.data.entity.User
import com.example.motoinventory.databinding.ItemUserBinding
import com.example.motoinventory.utils.FormatUtils

class UserAdapter(
    private val onEdit: (User) -> Unit,
    private val onResetPassword: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(old: User, new: User) = old.id == new.id
            override fun areContentsTheSame(old: User, new: User) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            val ctx = binding.root.context
            binding.apply {
                tvNama.text = user.nama
                tvUsername.text = "@${user.username}"
                tvAvatar.text = user.nama.take(1).uppercase()
                tvLastLogin.text = if (user.lastLogin != null)
                    "Login: ${FormatUtils.formatDateTime(user.lastLogin)}"
                else "Belum pernah login"

                // Role badge
                val (roleText, roleColor, roleBg) = when (user.role) {
                    User.ROLE_ADMIN -> Triple("Admin",
                        ctx.getColor(R.color.role_admin),
                        ctx.getColor(R.color.role_admin_bg))
                    User.ROLE_MANAGER -> Triple("Manager",
                        ctx.getColor(R.color.role_manager),
                        ctx.getColor(R.color.role_manager_bg))
                    else -> Triple("Staff",
                        ctx.getColor(R.color.role_staff),
                        ctx.getColor(R.color.role_staff_bg))
                }
                tvRole.text = roleText
                tvRole.setTextColor(roleColor)
                tvRole.setBackgroundColor(roleBg)

                // Avatar bg color based on role
                tvAvatar.setBackgroundColor(roleColor)

                btnEdit.setOnClickListener { onEdit(user) }
                btnResetPw.setOnClickListener { onResetPassword(user) }
                btnDelete.setOnClickListener { onDelete(user) }
            }
        }
    }
}
