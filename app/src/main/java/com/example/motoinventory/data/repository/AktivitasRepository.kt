package com.example.motoinventory.data.repository

import com.example.motoinventory.data.dao.AktivitasDao
import com.example.motoinventory.data.entity.Aktivitas

class AktivitasRepository(private val aktivitasDao: AktivitasDao) {

    val allAktivitas = aktivitasDao.getAllAktivitas()
    val recentAktivitas = aktivitasDao.getRecentAktivitas(50)

    suspend fun insertAktivitas(aktivitas: Aktivitas) = aktivitasDao.insertAktivitas(aktivitas)
    suspend fun deleteAktivitas(aktivitas: Aktivitas) = aktivitasDao.deleteAktivitas(aktivitas)

    fun getAktivitasByUser(userId: Int) = aktivitasDao.getAktivitasByUser(userId)
    fun getAktivitasByAksi(aksi: String) = aktivitasDao.getAktivitasByAksi(aksi)

    suspend fun logAktivitas(userId: Int, username: String, role: String, aksi: String, detail: String = "") {
        aktivitasDao.insertAktivitas(
            Aktivitas(
                userId = userId,
                username = username,
                role = role,
                aksi = aksi,
                detail = detail
            )
        )
    }
}
