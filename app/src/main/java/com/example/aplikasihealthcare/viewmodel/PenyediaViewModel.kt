package com.example.aplikasihealthcare.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.aplikasihealthcare.repositori.HealthApplication

object PenyediaViewModel {
    val Factory = viewModelFactory {

        initializer {
            HomeViewModel(aplikasiHealth().container.repositoryTensi)
        }

        initializer {
            EntryViewModel(aplikasiHealth().container.repositoryTensi)
        }

        initializer {
            DetailViewModel(
                this.createSavedStateHandle(),
                aplikasiHealth().container.repositoryTensi
            )
        }

        initializer {
            EditViewModel(
                this.createSavedStateHandle(),
                aplikasiHealth().container.repositoryTensi
            )
        }
    }
}

fun CreationExtras.aplikasiHealth(): HealthApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HealthApplication)