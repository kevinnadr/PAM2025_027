package com.example.compustoree.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.* // Pastikan import ini ada
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compustoree.model.Produk
import com.example.compustoree.model.UserSession
import com.example.compustoree.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProdukClick: (Int) -> Unit,
    onRiwayatClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onEditProductClick: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val user = UserSession.currentUser
    val isAdmin = user?.role == "admin"
    val namaUser = user?.nama ?: "Tamu"

    // --- STATE UNTUK SEARCH BAR (PERBAIKAN DISINI) ---
    var searchQuery by remember { mutableStateOf("") } // 1. Variabel penampung teks

    // State untuk Dialog Hapus
    var showDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Produk") },
            text = { Text("Apakah Anda yakin ingin menghapus produk ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.let { id -> viewModel.deleteProduct(id) }
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(if (isAdmin) "Admin Dashboard" else "CompuStore", fontWeight = FontWeight.Bold)
                        Text("Halo, $namaUser", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onAddProductClick) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {

            // --- SEARCH BAR YANG SUDAH BISA DIKETIK ---
            OutlinedTextField(
                value = searchQuery, // 2. Hubungkan ke variabel
                onValueChange = { newText ->
                    searchQuery = newText // 3. Simpan ketikan user ke variabel
                },
                placeholder = { Text("Cari laptop, monitor...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Filter produk berdasarkan apa yang diketik (Opsional, fitur tambahan)
                    val filteredProducts = viewModel.products.filter {
                        it.nama?.contains(searchQuery, ignoreCase = true) == true ||
                                it.kategori?.contains(searchQuery, ignoreCase = true) == true
                    }

                    items(filteredProducts) { produk ->
                        ProductItem(
                            produk = produk,
                            isAdmin = isAdmin,
                            onClick = { onProdukClick(produk.id) },
                            onEditClick = { onEditProductClick(produk.id) },
                            onDeleteClick = {
                                productToDelete = produk.id
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

// ... (Bagian ProductItem di bawah biarkan sama seperti sebelumnya) ...
@Composable
fun ProductItem(
    produk: Produk,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(produk.gambar)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.Warning)
                )

                if (isAdmin) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
                                .size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = Color.Blue)
                        }
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
                                .size(32.dp)
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = Color.Red)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = produk.nama ?: "Tanpa Nama",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Rp ${produk.harga?.toInt() ?: 0}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                if (isAdmin) {
                    Text("Stok: ${produk.stok}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}