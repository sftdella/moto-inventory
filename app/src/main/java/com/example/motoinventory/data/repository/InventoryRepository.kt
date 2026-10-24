package com.example.motoinventory.data.repository

import com.example.motoinventory.data.dao.BiayaTambahanDao
import com.example.motoinventory.data.dao.MotorDao
import com.example.motoinventory.data.entity.BiayaTambahan
import com.example.motoinventory.data.entity.Motor

class InventoryRepository(
    private val motorDao: MotorDao,
    private val biayaTambahanDao: BiayaTambahanDao
) {
    // Motor
    val allMotors = motorDao.getAllMotors()
    val motorTersedia = motorDao.getMotorTersedia()
    val motorServis = motorDao.getMotorServis()
    val motorApproval = motorDao.getMotorApproval()
    val motorTerjual = motorDao.getMotorTerjual()
    val motorStok = motorDao.getMotorStok()
    val recentMotors = motorDao.getRecentMotors()
    val countTersedia = motorDao.countTersedia()
    val countTerjual = motorDao.countTerjual()
    val countStok = motorDao.countStok()
    val countApproval = motorDao.countApproval()
    val totalHargaBeli = motorDao.totalHargaBeli()
    val totalPemasukan = motorDao.totalPemasukan()
    val allMerks = motorDao.getAllMerks()

    suspend fun insertMotor(motor: Motor): Long = motorDao.insertMotor(motor)
    suspend fun updateMotor(motor: Motor) = motorDao.updateMotor(motor)
    suspend fun deleteMotor(motor: Motor) = motorDao.deleteMotor(motor)
    suspend fun getMotorById(id: Int) = motorDao.getMotorById(id)

    fun searchMotors(query: String) = motorDao.searchMotors(query)
    fun filterMotors(merk: String = "", tahun: Int = 0, warna: String = "", status: String = "") =
        motorDao.filterMotors(merk, tahun, warna, status)

    fun pengeluaranBulan(ym: String) = motorDao.pengeluaranBulan(ym)
    fun pemasukanBulan(ym: String) = motorDao.pemasukanBulan(ym)
    fun countTerjualBulan(ym: String) = motorDao.countTerjualBulan(ym)
    fun countMotorLama(beforeTimestamp: Long) = motorDao.countMotorLama(beforeTimestamp)

    // Biaya
    fun getBiayaByMotor(motorId: Int) = biayaTambahanDao.getBiayaByMotor(motorId)
    fun totalBiayaByMotor(motorId: Int) = biayaTambahanDao.totalBiayaByMotor(motorId)
    fun biayaTambahanBulan(ym: String) = biayaTambahanDao.biayaTambahanBulan(ym)
    val allBiaya = biayaTambahanDao.getAllBiaya()

    suspend fun insertBiaya(biaya: BiayaTambahan) = biayaTambahanDao.insertBiaya(biaya)
    suspend fun updateBiaya(biaya: BiayaTambahan) = biayaTambahanDao.updateBiaya(biaya)
    suspend fun deleteBiaya(biaya: BiayaTambahan) = biayaTambahanDao.deleteBiaya(biaya)
}
