package com.example.motoinventory.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.motoinventory.data.entity.BiayaTambahan

@Dao
interface BiayaTambahanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiaya(biaya: BiayaTambahan)

    @Update
    suspend fun updateBiaya(biaya: BiayaTambahan)

    @Delete
    suspend fun deleteBiaya(biaya: BiayaTambahan)

    @Query("SELECT * FROM biaya_tambahan WHERE motorId = :motorId ORDER BY tanggal DESC")
    fun getBiayaByMotor(motorId: Int): LiveData<List<BiayaTambahan>>

    @Query("SELECT SUM(jumlah) FROM biaya_tambahan WHERE motorId = :motorId")
    fun totalBiayaByMotor(motorId: Int): LiveData<Long?>

    // Monthly biaya tambahan
    @Query("""
        SELECT SUM(jumlah) FROM biaya_tambahan
        WHERE strftime('%Y-%m', datetime(tanggal/1000, 'unixepoch')) = :yearMonth
    """)
    fun biayaTambahanBulan(yearMonth: String): LiveData<Long?>

    @Query("SELECT * FROM biaya_tambahan ORDER BY tanggal DESC")
    fun getAllBiaya(): LiveData<List<BiayaTambahan>>
}
