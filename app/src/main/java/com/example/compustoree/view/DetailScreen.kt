package com.example.compustoree.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.model.UserSession
// ✅ Import ViewModel dari package yang benar
import com.example.compustoree.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    onBuyClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit, // Parameter untuk Admin
    viewModel: DetailViewModel = viewModel()
) {
    val isAdmin = UserSession.currentUser?.role == "admin"

    // Load data produk saat ID berubah
    LaunchedEffect(produkId) {
        viewModel.loadProduk(produkId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    // Tombol Edit di AppBar (Hanya Admin)
                    if (isAdmin) {
                        IconButton(onClick = { onEditClick(produkId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Produk")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!isAdmin) {
                Button(
                    onClick = { onBuyClick(produkId) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp)
                ) {
                    Text("BELI SEKARANG")
                }
            }
        }
    ) { innerPadding ->
        val produk = viewModel.produk
        if (produk != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = produk.gambar,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(produk.nama ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Rp ${produk.harga?.toInt()}", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Deskripsi:", fontWeight = FontWeight.Bold)
                    Text(produk.deskripsi ?: "Tidak ada deskripsi", color = Color.Gray)

                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Info Admin:", fontWeight = FontWeight.Bold)
                        Text("Stok Tersedia: ${produk.stok}")
                        Text("Kategori: ${produk.kategori}")
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}