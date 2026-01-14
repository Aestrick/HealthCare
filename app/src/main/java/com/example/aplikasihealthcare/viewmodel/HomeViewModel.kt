package com.example.aplikasihealthcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasihealthcare.model.Tensi
import com.example.aplikasihealthcare.repositori.RepositoryTensi // <--- Ganti jadi Repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface HomeUiState {
    data class Success(val tensi: List<Tensi>) : HomeUiState
    object Error : HomeUiState
    object Loading : HomeUiState
}

class HomeViewModel(private val repositoryTensi: RepositoryTensi) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState> = repositoryTensi.getAllTensiStream()
        .filterNotNull()
        .map { HomeUiState.Success(it) as HomeUiState }
        .catch {
            emit(HomeUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )
}