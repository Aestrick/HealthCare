package com.example.aplikasihealthcare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplikasihealthcare.model.Tensi

@Database(entities = [Tensi::class], version = 2, exportSchema = false)
abstract class HealthDatabase : RoomDatabase() {

    abstract fun tensiDao(): TensiDao

    companion object {
        @Volatile
        private var Instance: HealthDatabase? = null

        fun getDatabase(context: Context): HealthDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, HealthDatabase::class.java, "tensi_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}