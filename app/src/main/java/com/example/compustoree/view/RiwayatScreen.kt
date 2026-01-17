package com.example.compustoree.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable // ✅ INI YANG HILANG SEBELUMNYA
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.model.RiwayatOrder
import com.example.compustoree.viewmodel.RiwayatUiState
import com.example.compustoree.viewmodel.RiwayatViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RiwayatScreen(
    viewModel: RiwayatViewModel = viewModel()
) {
    // Load data saat layar dibuka
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Background Abu-abu muda
            .padding(16.dp)
    ) {
        Text(
            text = if (viewModel.isAdmin) "Kelola Pesanan Masuk" else "Riwayat Belanja Saya",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = viewModel.uiState) {
            is RiwayatUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RiwayatUiState.Success -> {
                if (state.orders.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada riwayat transaksi", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(state.orders) { order ->
                            if (viewModel.isAdmin) {
                                AdminOrderCard(order, viewModel)
                            } else {
                                UserOrderCard(order)
                            }
                        }
                    }
                }
            }
            is RiwayatUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Gagal memuat data", color = Color.Red)
                }
            }
        }
    }
}

// ==========================================
// 1. TAMPILAN USER BIASA
// ==========================================
@Composable
fun UserOrderCard(order: RiwayatOrder) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- HEADER: Status & Tanggal ---
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusBadge(order.statusPengiriman ?: "Diproses")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(order.tanggalTransaksi),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

            // --- BODY: Gambar & Info Produk ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gambar Produk
                AsyncImage(
                    model = order.gambarProduk ?: "https://via.placeholder.com/150",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Detail Produk
                Column {
                    Text(
                        text = order.namaProduk ?: "Produk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // ✅ MENGGUNAKAN formatCurrency (Agar tidak bentrok)
                    val hargaSatuan = (order.totalHarga ?: 0.0) / (order.jumlah ?: 1)
                    Text(
                        text = "${order.jumlah} x ${formatCurrency(hargaSatuan)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- FOOTER: Total Belanja ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Pesanan", fontSize = 12.sp, color = Color.Gray)
                // ✅ MENGGUNAKAN formatCurrency
                Text(
                    text = formatCurrency(order.totalHarga ?: 0.0),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ==========================================
// 2. TAMPILAN ADMIN
// ==========================================
@Composable
fun AdminOrderCard(order: RiwayatOrder, viewModel: RiwayatViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- Info Pembeli ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.namaPembeli ?: "User",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(order.statusPengiriman ?: "Diproses")
            }

            Text(
                text = "Alamat: ${order.alamatPengiriman ?: "-"}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 26.dp, bottom = 8.dp)
            )

            Divider(color = Color(0xFFEEEEEE))

            // --- Info Produk Ringkas ---
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = order.gambarProduk ?: "",
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(order.namaProduk ?: "-", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    // ✅ MENGGUNAKAN formatCurrency
                    Text("Total: ${formatCurrency(order.totalHarga ?: 0.0)}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            // --- Tombol Aksi Admin ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Hapus
                IconButton(onClick = { viewModel.deleteOrder(order.idTransaksi) }) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                }

                // Tombol Update Status
                Button(
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Update Status", fontSize = 12.sp)
                }
            }
        }
    }

    // Dialog Update Status
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update Status Pesanan") },
            text = {
                Column {
                    listOf("Diproses", "Dikirim", "Selesai", "Dibatalkan").forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { // ✅ CLICKABLE SUDAH BISA DIPAKAI
                                    viewModel.updateStatus(order.idTransaksi, status)
                                    showDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (order.statusPengiriman == status),
                                onClick = {
                                    viewModel.updateStatus(order.idTransaksi, status)
                                    showDialog = false
                                }
                            )
                            Text(status)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }
}

// ==========================================
// 3. HELPER COMPONENTS
// ==========================================

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "selesai" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32)) // Hijau
        "dikirim" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0)) // Biru
        "dibatalkan" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828)) // Merah
        else -> Pair(Color(0xFFFFF3E0), Color(0xFFEF6C00)) // Kuning/Oranye (Diproses)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

// Fungsi format tanggal sederhana
fun formatDate(dateString: String?): String {
    if (dateString == null) return "-"
    return try {
        // Format dari Server (ISO 8601 biasanya)
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = parser.parse(dateString)
        // Format ke UI (16 Jan 2026)
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        formatter.format(date ?: "")
    } catch (e: Exception) {
        if (dateString.length >= 10) dateString.substring(0, 10) else dateString
    }
}

// ✅ KITA GANTI NAMA FUNGSINYA AGAR TIDAK BENTROK
// Menjadi 'formatCurrency' dan bersifat private
private fun formatCurrency(number: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(number).replace("Rp", "Rp ").substringBefore(",")
}