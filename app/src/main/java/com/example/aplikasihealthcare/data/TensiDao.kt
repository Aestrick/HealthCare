package com.example.aplikasihealthcare.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aplikasihealthcare.model.Tensi
import kotlinx.coroutines.flow.Flow

@Dao
interface TensiDao {
    @Query("SELECT * from tensi ORDER BY id DESC")
    fun getAllTensi(): Flow<List<Tensi>>

    @Query("SELECT * from tensi WHERE id = :id")
    fun getTensi(id: Int): Flow<Tensi>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tensi: Tensi)

    @Update
    suspend fun update(tensi: Tensi)

    @Delete
    suspend fun delete(tensi: Tensi)
}