package com.example.compustoree.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.model.UserSession
import com.example.compustoree.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    onBuyClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val isAdmin = UserSession.currentUser?.role == "admin"
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(produkId) {
        viewModel.getProductById(produkId)
    }

    // --- TAMBAHAN: Pantau Error Delete ---
    LaunchedEffect(viewModel.statusMessage) {
        if (viewModel.statusMessage.isNotEmpty()) {
            Toast.makeText(context, viewModel.statusMessage, Toast.LENGTH_LONG).show()
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Produk?") },
            text = { Text("Produk ini akan dihapus permanen. Jika produk pernah dibeli, penghapusan mungkin ditolak database.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false // Tutup dialog dulu
                        viewModel.deleteProduct(produkId) {
                            // Callback ini hanya jalan kalau SUKSES
                            Toast.makeText(context, "Produk Dihapus", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Ya, Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.White) {
                if (isAdmin) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { onEditClick(produkId) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Icon(Icons.Default.Edit, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit")
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hapus")
                        }
                    }
                } else {
                    Button(
                        onClick = { onBuyClick(produkId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp)
                    ) {
                        Text("Beli Sekarang")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val produk = viewModel.produk
            if (produk != null) {
                AsyncImage(
                    model = produk.gambar ?: "https://via.placeholder.com/300",
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = produk.nama ?: "Tanpa Nama",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = produk.kategori ?: "-",
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Rp ${formatRupiah(produk.harga ?: 0.0)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Deskripsi", fontWeight = FontWeight.Bold)
                    Text(text = produk.deskripsi ?: "Tidak ada deskripsi")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Stok Tersedia: ${produk.stok ?: 0}")
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}