package com.example.compustoree.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CheckoutViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(produkId) {
        viewModel.loadProduk(produkId)
    }

    LaunchedEffect(viewModel.statusTransaksi) {
        if (viewModel.statusTransaksi.contains("Gagal")) {
            Toast.makeText(context, viewModel.statusTransaksi, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Konfirmasi Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Bayar:", fontWeight = FontWeight.Bold)
                        Text(
                            "Rp ${formatRupiah(viewModel.totalBayar)}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.prosesCheckout(onSuccess) },
                        enabled = !viewModel.isLoading && (viewModel.metodePengiriman == "Ambil Ditempat" || viewModel.alamat.isNotEmpty()),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("BUAT PESANAN SEKARANG")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val item = viewModel.produk
            if (item != null) {

                // === 1. DATA BARANG ===
                Text("Barang yang dibeli", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // --- PERBAIKAN DI SINI (Pakai ?: "Default") ---
                            Text(
                                text = item.nama ?: "Nama Produk Tidak Tersedia",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Harga: Rp ${formatRupiah(item.harga ?: 0.0)}",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.kurangJumlah() }) {
                                Icon(Icons.Default.KeyboardArrowDown, null)
                            }
                            Text(
                                text = "${viewModel.jumlahBeli}",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = { viewModel.tambahJumlah() }) {
                                Icon(Icons.Default.KeyboardArrowUp, null)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === 2. PILIH PENGIRIMAN ===
                Text("Pilih Pengiriman", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

                SelectableItem(
                    selected = viewModel.metodePengiriman == "Diantar",
                    title = "Diantar Kurir (JNE/J&T)",
                    icon = Icons.Default.ShoppingCart,
                    onClick = { viewModel.metodePengiriman = "Diantar" }
                )

                SelectableItem(
                    selected = viewModel.metodePengiriman == "Ambil Ditempat",
                    title = "Ambil Sendiri di Toko (Pickup)",
                    icon = Icons.Default.Home,
                    onClick = { viewModel.metodePengiriman = "Ambil Ditempat" }
                )

                if (viewModel.metodePengiriman == "Diantar") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.alamat,
                        onValueChange = { viewModel.alamat = it },
                        label = { Text("Alamat Lengkap Pengiriman") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 2
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF006064))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Silakan ambil pesanan di: Jl. Komputer Jaya No. 99, Jakarta Pusat.",
                                fontSize = 12.sp,
                                color = Color(0xFF006064)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === 3. METODE PEMBAYARAN ===
                Text("Metode Pembayaran", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

                SelectableItem(
                    selected = viewModel.metodePembayaran == "Transfer Bank",
                    title = "Transfer Bank (BCA/Mandiri)",
                    icon = Icons.Default.Star,
                    onClick = { viewModel.metodePembayaran = "Transfer Bank" }
                )

                SelectableItem(
                    selected = viewModel.metodePembayaran.contains("QRIS"),
                    title = "QRIS (GoPay/OVO/Dana)",
                    icon = Icons.Default.Share,
                    onClick = { viewModel.metodePembayaran = "QRIS / QR Code" }
                )

                if (viewModel.metodePembayaran.contains("QRIS")) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Scan QR Code ini:", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))

                            AsyncImage(
                                model = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=BayarTagihanCompuStore",
                                contentDescription = "QRIS Code",
                                modifier = Modifier.size(200.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Total: Rp ${formatRupiah(viewModel.totalBayar)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text("Otomatis dicek sistem", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                SelectableItem(
                    selected = viewModel.metodePembayaran.contains("COD"),
                    title = "COD (Bayar Ditempat)",
                    icon = Icons.Default.ThumbUp,
                    onClick = { viewModel.metodePembayaran = "COD (Bayar Ditempat)" }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text("Informasi Pemesan", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(viewModel.namaUser, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(viewModel.noHpUser, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(viewModel.emailUser, fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun SelectableItem(
    selected: Boolean,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.White
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if(selected) MaterialTheme.colorScheme.primary else Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Black
            )
        }
    }
}