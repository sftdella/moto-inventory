package com.example.motoinventory.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motors")
data class Motor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namMotor: String,
    val merk: String,
    val tahun: Int,
    val warna: String,
    val hargaBeli: Long,
    val hargaJual: Long,
    val status: String = STATUS_TERSEDIA,
    val tanggalMasuk: Long = System.currentTimeMillis(),
    val tanggalTerjual: Long? = null,
    val catatan: String = "",
    val nomorPolisi: String = "",
    val nomorRangka: String = "",
    val nomorMesin: String = ""
) {
    companion object {
        const val STATUS_TERSEDIA = "Tersedia"
        const val STATUS_SERVIS = "Proses Servis"
        const val STATUS_APPROVAL = "Menunggu Approval"
        const val STATUS_TERJUAL = "Terjual"

        val ALL_STATUSES = listOf(STATUS_TERSEDIA, STATUS_SERVIS, STATUS_APPROVAL, STATUS_TERJUAL)
    }
}
