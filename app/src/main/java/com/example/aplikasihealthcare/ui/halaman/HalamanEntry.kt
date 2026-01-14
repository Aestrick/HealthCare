package com.example.aplikasihealthcare.ui.halaman

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll // <--- INI OBATNYA!
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplikasihealthcare.ui.navigasi.DestinasiNavigasi
import com.example.aplikasihealthcare.viewmodel.DetailTensi
import com.example.aplikasihealthcare.viewmodel.EntryViewModel
import com.example.aplikasihealthcare.viewmodel.PenyediaViewModel
import com.example.aplikasihealthcare.viewmodel.UIStateTensi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

object DestinasiEntry : DestinasiNavigasi {
    override val route = "item_entry"
    override val titleRes = "Entry Tensi"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryTensiScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    // State buat nentuin lagi mode FORM atau mode KAMERA
    var isCameraOpen by remember { mutableStateOf(false) }

    // Launcher Izin Kamera
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isCameraOpen = true
        } else {
            Toast.makeText(context, "Perlu izin kamera buat scan!", Toast.LENGTH_SHORT).show()
        }
    }

    if (isCameraOpen) {
        // --- TAMPILAN KAMERA LIVE SCAN ---
        LiveCameraScanner(
            onResultFound = { sis, dia, nad ->
                // Kalau ketemu, tutup kamera, update form
                isCameraOpen = false
                viewModel.updateUiState(
                    viewModel.uiStateTensi.detailTensi.copy(
                        sistolik = sis,
                        diastolik = dia,
                        nadi = nad
                    )
                )
                Toast.makeText(context, "Data Terbaca: $sis/$dia ($nad)", Toast.LENGTH_SHORT).show()
            },
            onClose = { isCameraOpen = false }
        )
    } else {
        // --- TAMPILAN FORMULIR BIASA ---
        Scaffold(
            // SEKARANG BARIS INI GAK BAKAL MERAH LAGI
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(DestinasiEntry.titleRes) },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            EntryBody(
                uiStateTensi = viewModel.uiStateTensi,
                onTensiValueChange = viewModel::updateUiState,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.saveTensi()
                        navigateBack()
                    }
                },
                onScanClick = {
                    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        isCameraOpen = true
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            )
        }
    }
}

// ==========================================
// 📷 KOMPONEN KAMERA LIVE SCANNER
// ==========================================
@Composable
fun LiveCameraScanner(
    onResultFound: (String, String, String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Feedback ke user kalau lagi mikir
    var scanningStatus by remember { mutableStateOf("Mencari angka stabil...") }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val executor = Executors.newSingleThreadExecutor()

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // INI OTAKNYA: ANALYZER
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(executor, TensiAnalyzer { sis, dia, nad, isStable ->
                                if (isStable) {
                                    // Pindah ke Main Thread buat update UI
                                    previewView.post { onResultFound(sis, dia, nad) }
                                } else {
                                    // Update status doang
                                    previewView.post { scanningStatus = "Mendeteksi: $sis/$dia..." }
                                }
                            })
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("Camera", "Gagal bind camera", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay UI (Tombol Tutup & Status)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(30.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = scanningStatus,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(10.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Batal / Tutup")
            }
        }
    }
}

// ==========================================
// 🧠 OTAK PENGANALISIS GAMBAR (ANALYZER)
// ==========================================
class TensiAnalyzer(private val onResult: (String, String, String, Boolean) -> Unit) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Variabel buat logika "STABIL" (Nunggu bentar)
    private var lastSis = ""
    private var lastDia = ""
    private var stabilityCounter = 0
    // Saya ganti jadi camelCase biar gak kuning lagi warningnya
    private val stabilityThreshold = 5 // Harus sama terus selama 5 frame

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // LOGIKA POSISI (Sama kayak yang tadi, tapi lebih cepet)
                    val kandidatAngka = mutableListOf<Pair<Int, Int>>()

                    for (block in visionText.textBlocks) {
                        for (line in block.lines) {
                            val angka = Regex("\\d+").find(line.text)?.value?.toIntOrNull()
                            val box = line.boundingBox

                            // Filter angka wajar tensi
                            if (angka != null && box != null && angka > 40 && angka < 250) {
                                kandidatAngka.add(Pair(box.top, angka)) // Simpan Posisi Y & Angka
                            }
                        }
                    }

                    // Urutkan dari ATAS ke BAWAH
                    kandidatAngka.sortBy { it.first }

                    if (kandidatAngka.size >= 2) {
                        // Kita asumsikan 3 teratas adalah Sis, Dia, Nadi (atau minimal 2)
                        val currentSis = kandidatAngka[0].second.toString()
                        val currentDia = kandidatAngka[1].second.toString()
                        val currentNad = if (kandidatAngka.size > 2) kandidatAngka[2].second.toString() else "0"

                        // CEK KESTABILAN (Biar gak "dongo" berubah2 mulu)
                        if (currentSis == lastSis && currentDia == lastDia) {
                            stabilityCounter++
                        } else {
                            stabilityCounter = 0 // Reset kalau angka berubah
                        }

                        // Simpan angka sekarang buat dibandingin frame depan
                        lastSis = currentSis
                        lastDia = currentDia

                        // Kalau sudah stabil selama X frame, LOCK hasilnya!
                        if (stabilityCounter >= stabilityThreshold) {
                            onResult(currentSis, currentDia, currentNad, true) // TRUE = FINAL
                        } else {
                            onResult(currentSis, currentDia, currentNad, false) // FALSE = Masih mikir
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close() // PENTING: Wajib tutup biar frame lanjut
                }
        } else {
            imageProxy.close()
        }
    }
}

// ==========================================
// UI FORMULIR BIASA
// ==========================================
@Composable
fun EntryBody(
    uiStateTensi: UIStateTensi,
    onTensiValueChange: (DetailTensi) -> Unit,
    onSaveClick: () -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Tombol Scan Lebih Besar
        Button(
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("📹 SCAN LIVE (TAHAN KAMERA)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        FormInputTensi(
            detailTensi = uiStateTensi.detailTensi,
            onValueChange = onTensiValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSaveClick,
            enabled = uiStateTensi.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Data")
        }
    }
}

@Composable
fun FormInputTensi(
    detailTensi: DetailTensi,
    onValueChange: (DetailTensi) -> Unit,
    modifier: Modifier = Modifier
) {
    if (detailTensi.tanggal.isEmpty()) {
        val currentDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        onValueChange(detailTensi.copy(tanggal = currentDateTime))
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = detailTensi.tanggal,
            onValueChange = {},
            label = { Text("Waktu Pengecekan") },
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        OutlinedTextField(
            value = detailTensi.sistolik,
            onValueChange = { onValueChange(detailTensi.copy(sistolik = it)) },
            label = { Text("Sistolik (Atas)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = detailTensi.diastolik,
            onValueChange = { onValueChange(detailTensi.copy(diastolik = it)) },
            label = { Text("Diastolik (Bawah)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = detailTensi.nadi,
            onValueChange = { onValueChange(detailTensi.copy(nadi = it)) },
            label = { Text("Denyut Nadi") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}