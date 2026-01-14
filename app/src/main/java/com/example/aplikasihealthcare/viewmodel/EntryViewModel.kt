package com.example.aplikasihealthcare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aplikasihealthcare.repositori.RepositoryTensi // Pake Repository ya, jangan DAO langsung biar konsisten

// --- STATE UNTUK UI ---
data class UIStateTensi(
    val detailTensi: DetailTensi = DetailTensi(),
    val isEntryValid: Boolean = false
)

// Shared Data Class (Ini dipake bareng-bareng sama DetailViewModel & EditViewModel)
data class DetailTensi(
    val id: Int = 0,
    val sistolik: String = "",
    val diastolik: String = "",
    val nadi: String = "",
    val tanggal: String = ""
)

// --- VIEW MODEL ---
class EntryViewModel(private val repositoryTensi: RepositoryTensi) : ViewModel() {

    var uiStateTensi by mutableStateOf(UIStateTensi())
        private set

    fun updateUiState(detailTensi: DetailTensi) {
        uiStateTensi = UIStateTensi(
            detailTensi = detailTensi,
            isEntryValid = validasiInput(detailTensi)
        )
    }

    suspend fun saveTensi() {
        if (validasiInput(uiStateTensi.detailTensi)) {
            // Panggil .toTensi() dari DataUtils.kt
            repositoryTensi.insertTensi(uiStateTensi.detailTensi.toTensi())
        }
    }

    private fun validasiInput(uiState: DetailTensi = uiStateTensi.detailTensi): Boolean {
        return with(uiState) {
            sistolik.isNotBlank() && diastolik.isNotBlank() && nadi.isNotBlank()
        }
    }
}