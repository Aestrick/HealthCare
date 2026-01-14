package com.example.aplikasihealthcare.viewmodel

import com.example.aplikasihealthcare.model.Tensi

// Konversi dari Database (Entity) ke UI (DetailTensi)
fun Tensi.toDetailTensi(): DetailTensi = DetailTensi(
    id = id,
    tanggal = tanggal,
    sistolik = sistolik,
    diastolik = diastolik,
    nadi = nadi
)

// Konversi dari UI (DetailTensi) ke Database (Entity)
// Kita pake ignoreCase = true biar aman
fun DetailTensi.toTensi(): Tensi = Tensi(
    id = id,
    tanggal = tanggal,
    sistolik = sistolik,
    diastolik = diastolik,
    nadi = nadi
)

// Konversi ke UIState lengkap
fun Tensi.toUiStateTensi(isEntryValid: Boolean = false): UIStateTensi = UIStateTensi(
    detailTensi = this.toDetailTensi(),
    isEntryValid = isEntryValid
)