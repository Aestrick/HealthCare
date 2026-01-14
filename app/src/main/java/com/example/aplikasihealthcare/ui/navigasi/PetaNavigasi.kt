package com.example.aplikasihealthcare.ui.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// --- IMPORT MANUAL (PASTIKAN EJAANNYA SAMA) ---
import com.example.aplikasihealthcare.ui.halaman.DestinasiHome
import com.example.aplikasihealthcare.ui.halaman.HalamanHome
import com.example.aplikasihealthcare.ui.halaman.DestinasiEntry
import com.example.aplikasihealthcare.ui.halaman.EntryTensiScreen

@Composable
fun HealthAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = modifier
    ) {
        composable(route = DestinasiHome.route) {
            HalamanHome(
                navigateToItemEntry = { navController.navigate(DestinasiEntry.route) }
            )
        }

        composable(route = DestinasiEntry.route) {
            EntryTensiScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}