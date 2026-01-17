package com.example.compustoree.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compustoree.viewmodel.EditProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    viewModel: EditProductViewModel = viewModel()
) {
    // ✅ LOAD DATA OTOMATIS SAAT HALAMAN DIBUKA
    LaunchedEffect(produkId) {
        viewModel.loadProduct(produkId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Produk") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (viewModel.isLoading && viewModel.nama.isEmpty()) {
                // Tampilkan loading jika data belum ketarik
                CircularProgressIndicator()
            } else {
                // Form Edit
                OutlinedTextField(
                    value = viewModel.gambarUrl,
                    onValueChange = { viewModel.gambarUrl = it },
                    label = { Text("Link Gambar / Kode Gambar") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.nama,
                    onValueChange = { viewModel.nama = it },
                    label = { Text("Nama Produk") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.kategori,
                    onValueChange = { viewModel.kategori = it },
                    label = { Text("Kategori") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.harga,
                    onValueChange = { viewModel.harga = it },
                    label = { Text("Harga") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.stok,
                    onValueChange = { viewModel.stok = it },
                    label = { Text("Stok") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.deskripsi,
                    onValueChange = { viewModel.deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Panggil fungsi Update dengan ID produk
                        viewModel.updateProduct(produkId, onSuccess = onBackClick)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("SIMPAN PERUBAHAN")
                    }
                }
            }
        }
    }
}