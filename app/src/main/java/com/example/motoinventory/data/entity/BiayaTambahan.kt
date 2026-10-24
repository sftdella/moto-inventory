package com.example.motoinventory.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "biaya_tambahan",
    foreignKeys = [
        ForeignKey(
            entity = Motor::class,
            parentColumns = ["id"],
            childColumns = ["motorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BiayaTambahan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val motorId: Int,
    val kategori: String, // Perbaikan, Spare Part, Administrasi, Lainnya
    val keterangan: String,
    val jumlah: Long,
    val tanggal: Long = System.currentTimeMillis()
)
