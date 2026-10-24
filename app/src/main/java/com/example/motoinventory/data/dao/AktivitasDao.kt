package com.example.motoinventory.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.motoinventory.data.entity.Aktivitas

@Dao
interface AktivitasDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAktivitas(aktivitas: Aktivitas)

    @Delete
    suspend fun deleteAktivitas(aktivitas: Aktivitas)

    @Query("SELECT * FROM aktivitas ORDER BY timestamp DESC")
    fun getAllAktivitas(): LiveData<List<Aktivitas>>

    @Query("SELECT * FROM aktivitas ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAktivitas(limit: Int = 50): LiveData<List<Aktivitas>>

    @Query("SELECT * FROM aktivitas WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAktivitasByUser(userId: Int): LiveData<List<Aktivitas>>

    @Query("SELECT * FROM aktivitas WHERE aksi = :aksi ORDER BY timestamp DESC")
    fun getAktivitasByAksi(aksi: String): LiveData<List<Aktivitas>>

    @Query("DELETE FROM aktivitas WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOldAktivitas(beforeTimestamp: Long)

    @Query("SELECT COUNT(*) FROM aktivitas")
    fun countAktivitas(): LiveData<Int>
}
