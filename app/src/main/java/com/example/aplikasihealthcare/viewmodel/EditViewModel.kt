package com.example.aplikasihealthcare.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasihealthcare.repositori.RepositoryTensi
import com.example.aplikasihealthcare.ui.navigasi.DestinasiEdit
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryTensi: RepositoryTensi
) : ViewModel() {

    var tensiUiState by mutableStateOf(UIStateTensi())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[DestinasiEdit.itemIdArg])

    init {
        viewModelScope.launch {
            tensiUiState = repositoryTensi.getTensiStream(itemId)
                .filterNotNull()
                .first()
                // Sekarang dia pake fungsi dari DataUtils.kt (Gak bentrok lagi)
                .toUiStateTensi(true)
        }
    }

    fun updateUiState(detailTensi: DetailTensi) {
        tensiUiState = UIStateTensi(detailTensi = detailTensi, isEntryValid = true)
    }

    suspend fun updateTensi() {
        if (tensiUiState.isEntryValid) {
            // Ini juga pake dari DataUtils.kt
            repositoryTensi.updateTensi(tensiUiState.detailTensi.toTensi())
        }
    }
}

// ⚠️ SAYA SUDAH MENGHAPUS FUNGSI 'fun Tensi.toUiStateTensi' DI BAWAH SINI
// KARENA SUDAH ADA DI DataUtils.kt. JANGAN DITAMBAH LAGI YA!