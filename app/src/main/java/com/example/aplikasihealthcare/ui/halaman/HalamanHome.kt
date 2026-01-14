package com.example.aplikasihealthcare.ui.halaman

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplikasihealthcare.model.Tensi
import com.example.aplikasihealthcare.ui.navigasi.DestinasiNavigasi
import com.example.aplikasihealthcare.viewmodel.HomeUiState
import com.example.aplikasihealthcare.viewmodel.HomeViewModel
import com.example.aplikasihealthcare.viewmodel.PenyediaViewModel

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Riwayat Tensi"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHome(
    navigateToItemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()
    var selectedTensi by remember { mutableStateOf<Tensi?>(null) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(DestinasiHome.titleRes) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Data")
            }
        },
    ) { innerPadding ->
        HomeStatus(
            homeUiState = homeUiState,
            modifier = Modifier.padding(innerPadding),
            onDetailClick = { tensi -> selectedTensi = tensi }
        )

        if (selectedTensi != null) {
            DetailDialog(
                tensi = selectedTensi!!,
                onDismiss = { selectedTensi = null }
            )
        }
    }
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState,
    modifier: Modifier = Modifier,
    onDetailClick: (Tensi) -> Unit
) {
    when (homeUiState) {
        is HomeUiState.Loading -> BoxLoading(modifier)
        is HomeUiState.Success ->
            if (homeUiState.tensi.isEmpty()) {
                BoxKosong(modifier)
            } else {
                TensiLayout(
                    listTensi = homeUiState.tensi,
                    modifier = modifier,
                    onItemClick = onDetailClick
                )
            }
        is HomeUiState.Error -> BoxError(modifier)
    }
}

@Composable
fun TensiLayout(
    listTensi: List<Tensi>,
    modifier: Modifier = Modifier,
    onItemClick: (Tensi) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(listTensi) { tensi ->
            TensiCard(
                tensi = tensi,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(tensi) }
            )
        }
    }
}

@Composable
fun TensiCard(
    tensi: Tensi,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = tensi.tanggal, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Tekanan Darah", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "${tensi.sistolik}/${tensi.diastolik}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.Red)
                    Text(text = "${tensi.nadi} BPM", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun DetailDialog(
    tensi: Tensi,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Detail Pengecekan") },
        text = {
            Column {
                Text("Waktu: ${tensi.tanggal}")
                Spacer(modifier = Modifier.height(10.dp))
                Text("Sistolik (Atas): ${tensi.sistolik} mmHg")
                Text("Diastolik (Bawah): ${tensi.diastolik} mmHg")
                Text("Denyut Nadi: ${tensi.nadi} bpm")
                Spacer(modifier = Modifier.height(10.dp))

                val sis = tensi.sistolik.toString().toIntOrNull() ?: 0
                val status = if (sis > 140) "Tinggi (Hipertensi)" else if (sis < 90) "Rendah" else "Normal"
                Text("Status: $status", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        }
    )
}

@Composable
fun BoxLoading(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Sedang memuat data...")
    }
}

@Composable
fun BoxError(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Gagal memuat data")
    }
}

@Composable
fun BoxKosong(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.height(50.dp))
        Text("Belum ada data tensi", modifier = Modifier.padding(top = 8.dp))
    }
}