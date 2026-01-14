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
                .toUiStateTensi(true)
        }
    }

    fun updateUiState(detailTensi: DetailTensi) {
        tensiUiState = UIStateTensi(detailTensi = detailTensi, isEntryValid = true)
    }

    suspend fun updateTensi() {
        if (tensiUiState.isEntryValid) {
            repositoryTensi.updateTensi(tensiUiState.detailTensi.toTensi())
        }
    }
}