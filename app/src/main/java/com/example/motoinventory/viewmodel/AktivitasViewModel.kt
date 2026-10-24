package com.example.motoinventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.motoinventory.data.database.InventoryDatabase
import com.example.motoinventory.data.entity.Aktivitas
import com.example.motoinventory.data.repository.AktivitasRepository
import kotlinx.coroutines.launch

class AktivitasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AktivitasRepository
    val allAktivitas: LiveData<List<Aktivitas>>
    val recentAktivitas: LiveData<List<Aktivitas>>

    init {
        val db = InventoryDatabase.getDatabase(application)
        repository = AktivitasRepository(db.aktivitasDao())
        allAktivitas = repository.allAktivitas
        recentAktivitas = repository.recentAktivitas
    }

    fun logAktivitas(userId: Int, username: String, role: String, aksi: String, detail: String = "") =
        viewModelScope.launch {
            repository.logAktivitas(userId, username, role, aksi, detail)
        }

    fun deleteAktivitas(aktivitas: Aktivitas) = viewModelScope.launch {
        repository.deleteAktivitas(aktivitas)
    }

    fun getAktivitasByUser(userId: Int) = repository.getAktivitasByUser(userId)
}
