package com.example.aplikasihealthcare.viewmodel

import com.example.aplikasihealthcare.model.Tensi

fun Tensi.toDetailTensi(): DetailTensi = DetailTensi(
    id = id,
    tanggal = tanggal,
    sistolik = sistolik,
    diastolik = diastolik,
    nadi = nadi
)

fun DetailTensi.toTensi(): Tensi = Tensi(
    id = id,
    tanggal = tanggal,
    sistolik = sistolik,
    diastolik = diastolik,
    nadi = nadi
)

fun Tensi.toUiStateTensi(isEntryValid: Boolean = false): UIStateTensi = UIStateTensi(
    detailTensi = this.toDetailTensi(),
    isEntryValid = isEntryValid
)