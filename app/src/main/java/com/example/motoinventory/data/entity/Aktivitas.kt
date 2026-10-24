package com.example.motoinventory.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aktivitas")
data class Aktivitas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val username: String,
    val role: String,
    val aksi: String, // LOGIN, LOGOUT, TAMBAH_MOTOR, EDIT_MOTOR, HAPUS_MOTOR, TAMBAH_BIAYA, JUAL_MOTOR, EXPORT_PDF
    val detail: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val AKSI_LOGIN = "Login"
        const val AKSI_LOGOUT = "Logout"
        const val AKSI_TAMBAH_MOTOR = "Tambah Motor"
        const val AKSI_EDIT_MOTOR = "Edit Motor"
        const val AKSI_HAPUS_MOTOR = "Hapus Motor"
        const val AKSI_TAMBAH_BIAYA = "Tambah Pengeluaran"
        const val AKSI_HAPUS_BIAYA = "Hapus Pengeluaran"
        const val AKSI_JUAL_MOTOR = "Approve Penjualan"
        const val AKSI_UPDATE_STATUS = "Update Status Motor"
        const val AKSI_EXPORT_PDF = "Export PDF"
        const val AKSI_TAMBAH_USER = "Tambah User"
        const val AKSI_EDIT_USER = "Edit User"
        const val AKSI_HAPUS_USER = "Hapus User"
    }
}
