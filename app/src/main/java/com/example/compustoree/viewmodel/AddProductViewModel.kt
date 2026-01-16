package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.Produk
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class AddProductViewModel : ViewModel() {
    // Data Form Input
    var nama by mutableStateOf("")
    var kategori by mutableStateOf("")
    var harga by mutableStateOf("")
    var stok by mutableStateOf("")
    var deskripsi by mutableStateOf("")
    var gambarUrl by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var status by mutableStateOf("")

    fun tambahProduk(onSuccess: () -> Unit) {
        if (nama.isEmpty() || harga.isEmpty()) {
            status = "Nama dan Harga wajib diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // Buat object produk
                val newProduk = Produk(
                    id = 0, // <--- TAMBAHKAN INI (Formalitas, backend yg atur ID asli)
                    nama = nama,
                    kategori = kategori,
                    merk = "Generic",
                    // Konversi ke tipe data yang sesuai (Double? dan Int?)
                    harga = harga.toDoubleOrNull(),
                    stok = stok.toIntOrNull(),
                    deskripsi = deskripsi,
                    gambar = gambarUrl
                )

                // Kirim ke server
                RetrofitClient.instance.addProduct(newProduk)

                status = "Berhasil Menambah Produk!"
                onSuccess()

            } catch (e: Exception) {
                status = "Gagal: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}