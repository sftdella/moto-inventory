package com.example.motoinventory.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.motoinventory.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE isActive = 1 ORDER BY nama ASC")
    fun getAllActiveUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users ORDER BY nama ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE username = :username AND isActive = 1 LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT COUNT(*) FROM users WHERE isActive = 1")
    fun countActiveUsers(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun countByUsername(username: String): Int

    @Query("UPDATE users SET lastLogin = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: Int, timestamp: Long)

    @Query("UPDATE users SET passwordHash = :hash WHERE id = :userId")
    suspend fun updatePassword(userId: Int, hash: String)
}
