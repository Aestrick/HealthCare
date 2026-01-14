package com.example.aplikasihealthcare.repositori

import android.app.Application

class HealthApplication : Application() {
    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}