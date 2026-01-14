package com.example.aplikasihealthcare.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasihealthcare.repositori.RepositoryTensi
import com.example.aplikasihealthcare.ui.navigasi.DestinasiDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryTensi: RepositoryTensi
) : ViewModel() {

    private val tensiId: Int = checkNotNull(savedStateHandle[DestinasiDetail.itemIdArg])

    val uiState: StateFlow<ItemDetailsUiState> = repositoryTensi.getTensiStream(tensiId)
        .filterNotNull()
        // Sekarang .toDetailTensi() sudah DIKENALI karena ada di DataUtils.kt
        .map { ItemDetailsUiState(detailTensi = it.toDetailTensi()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ItemDetailsUiState()
        )

    suspend fun deleteItem() {
        // .toTensi() juga sudah dikenali
        repositoryTensi.deleteTensi(uiState.value.detailTensi.toTensi())
    }
}

data class ItemDetailsUiState(
    val detailTensi: DetailTensi = DetailTensi()
)