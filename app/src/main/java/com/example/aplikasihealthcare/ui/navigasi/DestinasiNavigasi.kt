package com.example.aplikasihealthcare.ui.navigasi

interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Health Track"
}

object DestinasiEntry : DestinasiNavigasi {
    override val route = "item_entry"
    override val titleRes = "Catat Tensi"
}

object DestinasiDetail : DestinasiNavigasi {
    override val route = "item_details"
    override val titleRes = "Detail Riwayat"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

object DestinasiEdit : DestinasiNavigasi {
    override val route = "item_edit"
    override val titleRes = "Edit Data"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}