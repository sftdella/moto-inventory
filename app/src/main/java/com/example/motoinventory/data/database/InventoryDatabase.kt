package com.example.motoinventory.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.motoinventory.data.dao.AktivitasDao
import com.example.motoinventory.data.dao.BiayaTambahanDao
import com.example.motoinventory.data.dao.MotorDao
import com.example.motoinventory.data.dao.UserDao
import com.example.motoinventory.data.entity.Aktivitas
import com.example.motoinventory.data.entity.BiayaTambahan
import com.example.motoinventory.data.entity.Motor
import com.example.motoinventory.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [Motor::class, BiayaTambahan::class, User::class, Aktivitas::class],
    version = 2,
    exportSchema = false
)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun motorDao(): MotorDao
    abstract fun biayaTambahanDao(): BiayaTambahanDao
    abstract fun userDao(): UserDao
    abstract fun aktivitasDao(): AktivitasDao

    companion object {
        @Volatile
        private var INSTANCE: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDatabase::class.java,
                    "pcm_inventory_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                seedDefaultAdmin(getDatabase(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun hashPassword(password: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val bytes = md.digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        private suspend fun seedDefaultAdmin(db: InventoryDatabase) {
            val adminExists = db.userDao().countByUsername("admin") > 0
            if (!adminExists) {
                db.userDao().insertUser(
                    User(
                        nama = "Administrator",
                        username = "admin",
                        passwordHash = hashPassword("admin123"),
                        role = User.ROLE_ADMIN
                    )
                )
            }
        }
    }
}
