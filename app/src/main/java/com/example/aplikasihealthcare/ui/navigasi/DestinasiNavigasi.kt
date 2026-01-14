package com.example.aplikasihealthcare.ui.navigasi

// Interface dasar
interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

// 1. Halaman HOME
object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Health Track"
}

// 2. Halaman ENTRY (Tambah Data)
object DestinasiEntry : DestinasiNavigasi {
    override val route = "item_entry"
    override val titleRes = "Catat Tensi"
}

// 3. Halaman DETAIL
object DestinasiDetail : DestinasiNavigasi {
    override val route = "item_details"
    override val titleRes = "Detail Riwayat"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

// 4. Halaman EDIT
object DestinasiEdit : DestinasiNavigasi {
    override val route = "item_edit"
    override val titleRes = "Edit Data"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}