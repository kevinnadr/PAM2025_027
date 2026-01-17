package com.example.compustoree.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CheckoutViewModel = viewModel()
) {
    val context = LocalContext.current

    // Load data produk saat layar dibuka
    LaunchedEffect(produkId) {
        viewModel.loadProduk(produkId)
    }

    // Handle Pesan Error/Sukses
    LaunchedEffect(viewModel.message) {
        if (viewModel.message.isNotEmpty()) {
            Toast.makeText(context, viewModel.message, Toast.LENGTH_SHORT).show()
        }
    }

    // Ambil data LIVE dari ViewModel agar sinkron
    val produk = viewModel.produk
    val subtotal = viewModel.subtotal
    val ongkir = viewModel.ongkir
    val totalBayar = viewModel.totalBayar

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            // --- BOTTOM BAR STICKY (Tombol Bayar) ---
            Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Pembayaran", fontSize = 12.sp, color = Color.Gray)
                        // ✅ Memanggil fungsi formatRupiah
                        Text(
                            "Rp ${formatRupiah(totalBayar)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = { viewModel.buatPesanan(onSuccess) },
                        modifier = Modifier
                            .height(48.dp)
                            .width(160.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !viewModel.isLoading && produk != null
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Bayar Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FA) // Background abu-abu muda bersih
    ) { innerPadding ->
        if (produk == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // --- 1. ALAMAT PENGIRIMAN ---
                SectionTitle(icon = Icons.Default.LocationOn, title = "Alamat Pengiriman")
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.alamat,
                            onValueChange = { viewModel.alamat = it },
                            placeholder = { Text("Masukkan alamat lengkap (Jalan, RT/RW, Kota)...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 2. PRODUK YANG DIBELI ---
                SectionTitle(icon = Icons.Default.ShoppingCart, title = "Detail Pesanan")
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gambar Produk
                        AsyncImage(
                            model = produk.gambar ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Info & Quantity
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = produk.nama ?: "Produk",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // ✅ Memanggil fungsi formatRupiah
                            Text(
                                "Rp ${formatRupiah(produk.harga ?: 0.0)}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Stepper Quantity (+ -)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                        ) {
                            IconButton(
                                onClick = { if (viewModel.jumlahBeli > 1) viewModel.jumlahBeli-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold)
                            }
                            Text(
                                "${viewModel.jumlahBeli}",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { viewModel.jumlahBeli++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 3. METODE PENGIRIMAN ---
                SectionTitle(icon = Icons.Default.ShoppingCart, title = "Metode Pengiriman")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SelectionCard(
                        text = "JNE",
                        subText = "Rp 20.000",
                        selected = viewModel.kurir == "JNE",
                        onClick = { viewModel.kurir = "JNE" },
                        modifier = Modifier.weight(1f)
                    )
                    SelectionCard(
                        text = "J&T",
                        subText = "Rp 15.000",
                        selected = viewModel.kurir == "J&T",
                        onClick = { viewModel.kurir = "J&T" },
                        modifier = Modifier.weight(1f)
                    )
                    SelectionCard(
                        text = "SiCepat",
                        subText = "Rp 18.000",
                        selected = viewModel.kurir == "SiCepat",
                        onClick = { viewModel.kurir = "SiCepat" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 4. METODE PEMBAYARAN & QR CODE ---
                SectionTitle(icon = Icons.Default.AccountBox, title = "Metode Pembayaran")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    PaymentOptionRow("Transfer Bank", viewModel.metodeBayar) { viewModel.metodeBayar = it }
                    PaymentOptionRow("COD (Bayar di Tempat)", viewModel.metodeBayar) { viewModel.metodeBayar = it }
                    PaymentOptionRow("E-Wallet (OVO/GoPay/QRIS)", viewModel.metodeBayar) { viewModel.metodeBayar = it }

                    // ✨ FITUR QR CODE DINAMIS ✨
                    if (viewModel.metodeBayar == "E-Wallet (OVO/GoPay/QRIS)") {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Scan QRIS untuk Membayar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Load QR Code Dinamis
                                AsyncImage(
                                    model = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=BayarCompuStore_Rp${viewModel.totalBayar.toInt()}",
                                    contentDescription = "QR Code Pembayaran",
                                    modifier = Modifier
                                        .size(180.dp)
                                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                // ✅ Memanggil fungsi formatRupiah
                                Text(
                                    "Total: Rp ${formatRupiah(viewModel.totalBayar)}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 5. RINGKASAN BIAYA ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal Produk", color = Color.Gray)
                            // ✅ Memanggil fungsi formatRupiah
                            Text("Rp ${formatRupiah(subtotal)}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ongkos Kirim (${viewModel.kurir})", color = Color.Gray)
                            // ✅ Memanggil fungsi formatRupiah
                            Text("Rp ${formatRupiah(ongkir)}")
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Belanja", fontWeight = FontWeight.Bold)
                            // ✅ Memanggil fungsi formatRupiah
                            Text("Rp ${formatRupiah(totalBayar)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Spacer ekstra agar konten tidak tertutup bottom bar
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// --- KOMPONEN PENDUKUNG UI (HELPER) ---

@Composable
fun SectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun SelectionCard(text: String, subText: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.White),
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Color.LightGray),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(selected) MaterialTheme.colorScheme.primary else Color.Black)
            Text(subText, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PaymentOptionRow(title: String, currentSelection: String, onSelect: (String) -> Unit) {
    val isSelected = currentSelection == title
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, if(isSelected) MaterialTheme.colorScheme.primary else Color(0xFFEEEEEE), RoundedCornerShape(10.dp))
            .clickable { onSelect(title) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = { onSelect(title) })
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

// ✅ INI FUNGSI YANG SEBELUMNYA HILANG
// Dibuat private agar tidak bentrok dengan RiwayatScreen
private fun formatRupiah(number: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(number).replace("Rp", "").trim()
}