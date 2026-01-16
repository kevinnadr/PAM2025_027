package com.example.compustoree.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.model.Produk
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

// ================== 1. VIEW MODEL (PERBAIKAN DI SINI) ==================
class EditProductViewModel : ViewModel() {
    var nama by mutableStateOf("")
    var kategori by mutableStateOf("")
    var harga by mutableStateOf("")
    var stok by mutableStateOf("")
    var deskripsi by mutableStateOf("")
    var gambarUrl by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    // Load Data Lama
    fun loadProduk(id: Int) {
        viewModelScope.launch {
            try {
                val p = RetrofitClient.instance.getProductById(id)

                // --- PERBAIKAN: GUNAKAN ELVIS OPERATOR (?:) AGAR TIDAK ERROR NULL ---
                nama = p.nama ?: ""
                kategori = p.kategori ?: ""

                // Konversi Double? ke String (Jika null, jadi "0")
                harga = (p.harga ?: 0.0).toInt().toString()

                // Konversi Int? ke String (Jika null, jadi "0")
                stok = (p.stok ?: 0).toString()

                deskripsi = p.deskripsi ?: ""
                gambarUrl = p.gambar ?: ""

            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // Simpan Perubahan
    fun updateProduk(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val update = Produk(
                    id = id,
                    nama = nama,
                    kategori = kategori,
                    merk = "Generic",
                    harga = harga.toDoubleOrNull() ?: 0.0,
                    stok = stok.toIntOrNull() ?: 0,
                    deskripsi = deskripsi,
                    gambar = gambarUrl
                )
                RetrofitClient.instance.updateProduct(id, update)
                onSuccess()
            } catch (e: Exception) { e.printStackTrace() }
            finally { isLoading = false }
        }
    }
}

// ================== 2. UI EDIT SCREEN ==================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EditProductViewModel = viewModel()
) {
    val context = LocalContext.current

    // State untuk gambar baru
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(produkId) {
        viewModel.loadProduk(produkId)
    }

    // --- LAUNCHER GALERI ---
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Panggil fungsi uriToBase64 (Pastikan fungsi ini ada di AddProductScreen.kt)
            // Jika masih merah, import atau copy fungsi private di bawah
            val base64String = uriToBase64(context, uri)

            if (base64String != null) {
                viewModel.gambarUrl = "data:image/jpeg;base64,$base64String"

                val inputStream = context.contentResolver.openInputStream(uri)
                selectedBitmap = BitmapFactory.decodeStream(inputStream)
            } else {
                Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Produk") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- AREA PREVIEW FOTO ---
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = "Preview Baru",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (viewModel.gambarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = viewModel.gambarUrl,
                        contentDescription = "Preview Lama",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TOMBOL UBAH FOTO
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Ubah Foto")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM INPUT ---
            OutlinedTextField(value = viewModel.nama, onValueChange = { viewModel.nama = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.kategori, onValueChange = { viewModel.kategori = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.harga, onValueChange = { viewModel.harga = it }, label = { Text("Harga") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.stok, onValueChange = { viewModel.stok = it }, label = { Text("Stok") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.deskripsi, onValueChange = { viewModel.deskripsi = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            // TextField URL SUDAH DIHAPUS (AMAN)

            Spacer(modifier = Modifier.height(24.dp))

            // TOMBOL UPDATE
            Button(
                onClick = { viewModel.updateProduk(produkId, onSuccess) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !viewModel.isLoading
            ) {
                if(viewModel.isLoading) CircularProgressIndicator(color = Color.White) else Text("UPDATE PRODUK")
            }
        }
    }
}