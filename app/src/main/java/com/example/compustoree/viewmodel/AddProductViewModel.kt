package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class AddProductViewModel : ViewModel() {

    // --- STATE INPUT DATA ---
    var nama by mutableStateOf("")
    var kategori by mutableStateOf("")
    var harga by mutableStateOf("")
    var stok by mutableStateOf("")
    var deskripsi by mutableStateOf("")
    var gambarUrl by mutableStateOf("")

    // --- STATE UI ---
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf("")

    fun saveProduct(onSuccess: () -> Unit) {
        if (nama.isEmpty() || harga.isEmpty() || gambarUrl.isEmpty()) {
            message = "Mohon lengkapi Nama, Harga, dan Link Gambar!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // 🔥 STRATEGI: KIRIM SEMUA KEMUNGKINAN KEY
                val data = HashMap<String, Any>()

                // 1. Variasi Nama Produk (Kirim 3 macam key sekaligus)
                data["nama"] = nama          // Kemungkinan 1
                data["nama_produk"] = nama   // Kemungkinan 2 (Sesuai DB)
                data["name"] = nama          // Kemungkinan 3 (Bahasa Inggris)
                data["product_name"] = nama  // Kemungkinan 4

                // 2. Variasi Gambar
                data["gambar"] = gambarUrl   // Kemungkinan 1
                data["image"] = gambarUrl    // Kemungkinan 2
                data["photo"] = gambarUrl    // Kemungkinan 3
                data["url"] = gambarUrl      // Kemungkinan 4

                // 3. Variasi Merk & Deskripsi
                data["merk"] = "Generic"
                data["brand"] = "Generic"
                data["deskripsi"] = deskripsi
                data["description"] = deskripsi

                // 4. Data Angka (Biasanya standar)
                data["kategori"] = kategori
                data["harga"] = harga.toDoubleOrNull() ?: 0.0
                data["price"] = harga.toDoubleOrNull() ?: 0.0 // Jaga-jaga server pake bahasa inggris
                data["stok"] = stok.toIntOrNull() ?: 0
                data["stock"] = stok.toIntOrNull() ?: 0

                // Kirim Paket Lengkap ini ke Server
                RetrofitClient.instance.addProduct(data)

                message = "Produk Berhasil Disimpan!"
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                message = "Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}