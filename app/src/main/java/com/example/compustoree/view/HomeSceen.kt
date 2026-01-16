package com.example.compustoree.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart // <-- Ikon Standar (Pasti Ada)
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.compustoree.model.Produk
import com.example.compustoree.model.UserSession
import com.example.compustoree.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProdukClick: (Int) -> Unit,
    onRiwayatClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddProductClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val isAdmin = UserSession.currentUser?.role == "admin"
    var searchQuery by remember { mutableStateOf("") }

    // Load data otomatis saat layar dibuka
    LaunchedEffect(Unit) {
        viewModel.loadProduk()
    }

    Scaffold(
        containerColor = Color(0xFFF5F6F8), // Background abu-abu muda modern
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "CompuStore",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { viewModel.loadProduk() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search Bar Modern
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari Laptop, Mouse...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            disabledContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }
        },
        floatingActionButton = {
            // Tombol Tambah Produk (Hanya Admin)
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
                }
            }
        }
    ) { innerPadding ->

        // Logika Filter Pencarian (Aman dari Null)
        val filteredList = if (searchQuery.isEmpty()) {
            viewModel.listProduk
        } else {
            viewModel.listProduk.filter { produk ->
                produk.nama?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage.isNotEmpty()) {
                // Tampilan Error
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Warning, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Gagal memuat data", color = Color.Gray)
                    Button(onClick = { viewModel.loadProduk() }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Coba Lagi")
                    }
                }
            } else {
                // Grid Produk
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList) { produk ->
                        ItemProduk(produk) {
                            onProdukClick(produk.id)
                        }
                    }
                }
            }
        }
    }
}

// --- KOMPONEN KARTU PRODUK MODERN ---
@Composable
fun ItemProduk(
    produk: Produk,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            // 1. AREA GAMBAR (Aman Loading & Error)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (!produk.gambar.isNullOrEmpty()) {
                    SubcomposeAsyncImage(
                        model = produk.gambar,
                        contentDescription = null,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            // Icon Warning jika gambar rusak
                            Icon(Icons.Default.Warning, null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                        },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Tampilan jika database gambar NULL
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                        Text("No Image", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            // 2. DETAIL PRODUK
            Column(modifier = Modifier.padding(12.dp)) {
                // Kategori
                Text(
                    text = (produk.kategori ?: "UMUM").uppercase(),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Nama Produk (Dibatasi 2 baris agar tinggi kartu rata)
                Text(
                    text = produk.nama ?: "Tanpa Nama",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating & Terjual (Hiasan)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Text(" 4.8", fontSize = 11.sp, color = Color.Gray)
                    Text(" | Terjual ${produk.stok?.times(3) ?: 10}", fontSize = 11.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Harga & Tombol Cart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Rp ${formatRupiah(produk.harga ?: 0.0)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp
                    )

                    // Icon Cart Mini
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // MENGGUNAKAN ICON SHOPPING CART STANDAR
                            Icon(Icons.Default.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// Fungsi Format Rupiah
fun formatRupiah(number: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(number).replace("Rp", "").trim()
}