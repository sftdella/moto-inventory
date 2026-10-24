package com.example.motoinventory.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val username: String,
    val passwordHash: String,
    val role: String, // ADMIN, MANAGER, STAFF
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long? = null
) {
    companion object {
        const val ROLE_ADMIN = "ADMIN"
        const val ROLE_MANAGER = "MANAGER"
        const val ROLE_STAFF = "STAFF"
    }
}
