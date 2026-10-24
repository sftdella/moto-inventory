package com.example.motoinventory.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.motoinventory.data.entity.User

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pcm_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_NAMA = "nama"
        private const val KEY_ROLE = "role"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(user: User) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USERNAME, user.username)
            putString(KEY_NAMA, user.nama)
            putString(KEY_ROLE, user.role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""
    fun getNama(): String = prefs.getString(KEY_NAMA, "") ?: ""
    fun getRole(): String = prefs.getString(KEY_ROLE, "") ?: ""

    fun isAdmin(): Boolean = getRole() == User.ROLE_ADMIN
    fun isManager(): Boolean = getRole() == User.ROLE_MANAGER
    fun isStaff(): Boolean = getRole() == User.ROLE_STAFF

    fun canAccessDashboard(): Boolean = isAdmin() || isManager()
    fun canAccessInventory(): Boolean = true
    fun canAccessLaporan(): Boolean = isAdmin() || isManager()
    fun canAccessUsers(): Boolean = isAdmin()
    fun canAccessAktivitas(): Boolean = isAdmin()
    fun canExportPdf(): Boolean = isAdmin() || isManager()
    fun canAddMotor(): Boolean = isAdmin() || isStaff()
    fun canEditMotor(): Boolean = isAdmin() || isStaff()
    fun canDeleteMotor(): Boolean = isAdmin()
    fun canApproveJual(): Boolean = isAdmin() || isManager()
    fun canUpdateStatus(): Boolean = isAdmin() || isStaff()
    fun canAddBiaya(): Boolean = isAdmin() || isStaff()
    fun canManageUsers(): Boolean = isAdmin()
}
