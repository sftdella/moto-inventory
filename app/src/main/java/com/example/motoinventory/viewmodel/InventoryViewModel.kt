package com.example.motoinventory.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.motoinventory.data.database.InventoryDatabase
import com.example.motoinventory.data.entity.BiayaTambahan
import com.example.motoinventory.data.entity.Motor
import com.example.motoinventory.data.repository.InventoryRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository

    val allMotors: LiveData<List<Motor>>
    val motorTersedia: LiveData<List<Motor>>
    val motorServis: LiveData<List<Motor>>
    val motorApproval: LiveData<List<Motor>>
    val motorTerjual: LiveData<List<Motor>>
    val motorStok: LiveData<List<Motor>>
    val recentMotors: LiveData<List<Motor>>
    val countTersedia: LiveData<Int>
    val countTerjual: LiveData<Int>
    val countStok: LiveData<Int>
    val countApproval: LiveData<Int>
    val totalHargaBeli: LiveData<Long?>
    val totalPemasukan: LiveData<Long?>
    val pengeluaranBulanIni: LiveData<Long?>
    val pemasukanBulanIni: LiveData<Long?>
    val biayaTambahanBulanIni: LiveData<Long?>
    val countTerjualBulanIni: LiveData<Int>
    val allMerks: LiveData<List<String>>

    init {
        val db = InventoryDatabase.getDatabase(application)
        repository = InventoryRepository(db.motorDao(), db.biayaTambahanDao())

        val currentYM = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        allMotors = repository.allMotors
        motorTersedia = repository.motorTersedia
        motorServis = repository.motorServis
        motorApproval = repository.motorApproval
        motorTerjual = repository.motorTerjual
        motorStok = repository.motorStok
        recentMotors = repository.recentMotors
        countTersedia = repository.countTersedia
        countTerjual = repository.countTerjual
        countStok = repository.countStok
        countApproval = repository.countApproval
        totalHargaBeli = repository.totalHargaBeli
        totalPemasukan = repository.totalPemasukan
        allMerks = repository.allMerks

        pengeluaranBulanIni = repository.pengeluaranBulan(currentYM)
        pemasukanBulanIni = repository.pemasukanBulan(currentYM)
        biayaTambahanBulanIni = repository.biayaTambahanBulan(currentYM)
        countTerjualBulanIni = repository.countTerjualBulan(currentYM)
    }

    fun insertMotor(motor: Motor) = viewModelScope.launch { repository.insertMotor(motor) }
    fun updateMotor(motor: Motor) = viewModelScope.launch { repository.updateMotor(motor) }
    fun deleteMotor(motor: Motor) = viewModelScope.launch { repository.deleteMotor(motor) }
    suspend fun getMotorById(id: Int): Motor? = repository.getMotorById(id)
    fun searchMotors(query: String) = repository.searchMotors(query)
    fun filterMotors(merk: String = "", tahun: Int = 0, warna: String = "", status: String = "") =
        repository.filterMotors(merk, tahun, warna, status)

    fun getBiayaByMotor(motorId: Int) = repository.getBiayaByMotor(motorId)
    fun totalBiayaByMotor(motorId: Int) = repository.totalBiayaByMotor(motorId)
    fun insertBiaya(biaya: BiayaTambahan) = viewModelScope.launch { repository.insertBiaya(biaya) }
    fun updateBiaya(biaya: BiayaTambahan) = viewModelScope.launch { repository.updateBiaya(biaya) }
    fun deleteBiaya(biaya: BiayaTambahan) = viewModelScope.launch { repository.deleteBiaya(biaya) }

    fun updateStatusMotor(motor: Motor, newStatus: String) = viewModelScope.launch {
        val updated = motor.copy(
            status = newStatus,
            tanggalTerjual = if (newStatus == Motor.STATUS_TERJUAL) System.currentTimeMillis() else motor.tanggalTerjual
        )
        repository.updateMotor(updated)
    }

    fun jualMotor(motor: Motor, hargaJual: Long) = viewModelScope.launch {
        val updated = motor.copy(
            status = Motor.STATUS_TERJUAL,
            hargaJual = hargaJual,
            tanggalTerjual = System.currentTimeMillis()
        )
        repository.updateMotor(updated)
    }

    fun countMotorLama(days: Int = 30): LiveData<Int> {
        val threshold = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000)
        return repository.countMotorLama(threshold)
    }
}
