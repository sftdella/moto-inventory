package com.example.motoinventory.data.repository

import com.example.motoinventory.data.dao.UserDao
import com.example.motoinventory.data.entity.User
import com.example.motoinventory.utils.HashUtils

class UserRepository(private val userDao: UserDao) {

    val allActiveUsers = userDao.getAllActiveUsers()
    val allUsers = userDao.getAllUsers()
    val countActiveUsers = userDao.countActiveUsers()

    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun getUserById(id: Int) = userDao.getUserById(id)
    suspend fun getUserByUsername(username: String) = userDao.getUserByUsername(username)
    suspend fun countByUsername(username: String) = userDao.countByUsername(username)

    suspend fun updateLastLogin(userId: Int, timestamp: Long) =
        userDao.updateLastLogin(userId, timestamp)

    suspend fun resetPassword(userId: Int, newPassword: String) {
        val hash = HashUtils.sha256(newPassword)
        userDao.updatePassword(userId, hash)
    }

    suspend fun authenticate(username: String, password: String): User? {
        val user = userDao.getUserByUsername(username) ?: return null
        if (!user.isActive) return null
        return if (HashUtils.verifyPassword(password, user.passwordHash)) user else null
    }
}
