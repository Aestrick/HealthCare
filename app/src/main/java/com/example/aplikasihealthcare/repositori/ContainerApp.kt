package com.example.aplikasihealthcare.repositori

import android.content.Context
import com.example.aplikasihealthcare.data.HealthDatabase // IMPORT INI PENTING

interface ContainerApp {
    val repositoryTensi: RepositoryTensi
}

class AppDataContainer(private val context: Context) : ContainerApp {
    override val repositoryTensi: RepositoryTensi by lazy {
        OfflineRepositoryTensi(HealthDatabase.getDatabase(context).tensiDao())
    }
}