package com.example.aplikasihealthcare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aplikasihealthcare.repositori.RepositoryTensi // Pake Repository ya, jangan DAO langsung biar konsisten

data class UIStateTensi(
    val detailTensi: DetailTensi = DetailTensi(),
    val isEntryValid: Boolean = false
)

data class DetailTensi(
    val id: Int = 0,
    val sistolik: String = "",
    val diastolik: String = "",
    val nadi: String = "",
    val tanggal: String = ""
)

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
            repositoryTensi.insertTensi(uiStateTensi.detailTensi.toTensi())
        }
    }

    private fun validasiInput(uiState: DetailTensi = uiStateTensi.detailTensi): Boolean {
        return with(uiState) {
            sistolik.isNotBlank() && diastolik.isNotBlank() && nadi.isNotBlank()
        }
    }
}