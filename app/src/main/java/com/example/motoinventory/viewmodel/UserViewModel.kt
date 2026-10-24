package com.example.motoinventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.motoinventory.data.database.InventoryDatabase
import com.example.motoinventory.data.entity.User
import com.example.motoinventory.data.repository.UserRepository
import com.example.motoinventory.utils.HashUtils
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    val allUsers: LiveData<List<User>>
    val countActiveUsers: LiveData<Int>

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    init {
        val db = InventoryDatabase.getDatabase(application)
        repository = UserRepository(db.userDao())
        allUsers = repository.allActiveUsers
        countActiveUsers = repository.countActiveUsers
    }

    fun authenticate(username: String, password: String) = viewModelScope.launch {
        val user = repository.authenticate(username, password)
        _loginResult.postValue(user)
    }

    fun insertUser(nama: String, username: String, password: String, role: String) =
        viewModelScope.launch {
            val user = User(
                nama = nama,
                username = username,
                passwordHash = HashUtils.sha256(password),
                role = role
            )
            repository.insertUser(user)
        }

    fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    fun deleteUser(user: User) = viewModelScope.launch {
        repository.updateUser(user.copy(isActive = false))
    }

    fun resetPassword(userId: Int, newPassword: String) = viewModelScope.launch {
        repository.resetPassword(userId, newPassword)
    }

    fun updateLastLogin(userId: Int) = viewModelScope.launch {
        repository.updateLastLogin(userId, System.currentTimeMillis())
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        return repository.countByUsername(username) == 0
    }
}
