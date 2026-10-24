package com.example.motoinventory.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.motoinventory.data.entity.Motor

@Dao
interface MotorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotor(motor: Motor): Long

    @Update
    suspend fun updateMotor(motor: Motor)

    @Delete
    suspend fun deleteMotor(motor: Motor)

    @Query("SELECT * FROM motors ORDER BY tanggalMasuk DESC")
    fun getAllMotors(): LiveData<List<Motor>>

    @Query("SELECT * FROM motors WHERE status = 'Tersedia' ORDER BY tanggalMasuk DESC")
    fun getMotorTersedia(): LiveData<List<Motor>>

    @Query("SELECT * FROM motors WHERE status = 'Proses Servis' ORDER BY tanggalMasuk DESC")
    fun getMotorServis(): LiveData<List<Motor>>

    @Query("SELECT * FROM motors WHERE status = 'Menunggu Approval' ORDER BY tanggalMasuk DESC")
    fun getMotorApproval(): LiveData<List<Motor>>

    @Query("SELECT * FROM motors WHERE status = 'Terjual' ORDER BY tanggalTerjual DESC")
    fun getMotorTerjual(): LiveData<List<Motor>>

    @Query("SELECT * FROM motors WHERE status != 'Terjual' ORDER BY tanggalMasuk DESC")
    fun getMotorStok(): LiveData<List<Motor>>

    @Query("SELECT * FROM motors WHERE id = :id")
    suspend fun getMotorById(id: Int): Motor?

    @Query("""SELECT * FROM motors WHERE
        (namMotor LIKE '%' || :query || '%' OR
         merk LIKE '%' || :query || '%' OR
         warna LIKE '%' || :query || '%' OR
         nomorPolisi LIKE '%' || :query || '%')
        ORDER BY tanggalMasuk DESC""")
    fun searchMotors(query: String): LiveData<List<Motor>>

    @Query("""SELECT * FROM motors WHERE
        (:merk = '' OR merk = :merk) AND
        (:tahun = 0 OR tahun = :tahun) AND
        (:warna = '' OR warna = :warna) AND
        (:status = '' OR status = :status)
        ORDER BY tanggalMasuk DESC""")
    fun filterMotors(merk: String, tahun: Int, warna: String, status: String): LiveData<List<Motor>>

    // Stats
    @Query("SELECT COUNT(*) FROM motors WHERE status = 'Tersedia'")
    fun countTersedia(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM motors WHERE status = 'Terjual'")
    fun countTerjual(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM motors WHERE status != 'Terjual'")
    fun countStok(): LiveData<Int>

    @Query("SELECT SUM(hargaBeli) FROM motors")
    fun totalHargaBeli(): LiveData<Long?>

    @Query("SELECT SUM(hargaJual) FROM motors WHERE status = 'Terjual'")
    fun totalPemasukan(): LiveData<Long?>

    // Monthly stats
    @Query("""SELECT SUM(m.hargaBeli) FROM motors m
        WHERE strftime('%Y-%m', datetime(m.tanggalMasuk/1000, 'unixepoch')) = :yearMonth""")
    fun pengeluaranBulan(yearMonth: String): LiveData<Long?>

    @Query("""SELECT SUM(m.hargaJual) FROM motors m
        WHERE m.status = 'Terjual'
        AND strftime('%Y-%m', datetime(m.tanggalTerjual/1000, 'unixepoch')) = :yearMonth""")
    fun pemasukanBulan(yearMonth: String): LiveData<Long?>

    @Query("""SELECT COUNT(*) FROM motors m
        WHERE m.status = 'Terjual'
        AND strftime('%Y-%m', datetime(m.tanggalTerjual/1000, 'unixepoch')) = :yearMonth""")
    fun countTerjualBulan(yearMonth: String): LiveData<Int>

    @Query("SELECT * FROM motors ORDER BY tanggalMasuk DESC LIMIT 5")
    fun getRecentMotors(): LiveData<List<Motor>>

    // Motors waiting for approval
    @Query("SELECT COUNT(*) FROM motors WHERE status = 'Menunggu Approval'")
    fun countApproval(): LiveData<Int>

    // Old unsold motors (older than X days)
    @Query("SELECT COUNT(*) FROM motors WHERE status != 'Terjual' AND tanggalMasuk < :beforeTimestamp")
    fun countMotorLama(beforeTimestamp: Long): LiveData<Int>

    // Distinct merks for filter
    @Query("SELECT DISTINCT merk FROM motors ORDER BY merk ASC")
    fun getAllMerks(): LiveData<List<String>>
}
