package com.example.aplikasihealthcare.repositori

import com.example.aplikasihealthcare.data.TensiDao
import com.example.aplikasihealthcare.model.Tensi
import kotlinx.coroutines.flow.Flow

interface RepositoryTensi {
    fun getAllTensiStream(): Flow<List<Tensi>>
    fun getTensiStream(id: Int): Flow<Tensi?>
    suspend fun insertTensi(tensi: Tensi)
    suspend fun deleteTensi(tensi: Tensi)
    suspend fun updateTensi(tensi: Tensi)
}

class OfflineRepositoryTensi(private val tensiDao: TensiDao) : RepositoryTensi {
    override fun getAllTensiStream(): Flow<List<Tensi>> = tensiDao.getAllTensi()
    override fun getTensiStream(id: Int): Flow<Tensi?> = tensiDao.getTensi(id)
    override suspend fun insertTensi(tensi: Tensi) = tensiDao.insert(tensi)
    override suspend fun deleteTensi(tensi: Tensi) = tensiDao.delete(tensi)
    override suspend fun updateTensi(tensi: Tensi) = tensiDao.update(tensi)
}