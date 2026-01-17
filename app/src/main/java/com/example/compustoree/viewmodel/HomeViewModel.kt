package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.Produk
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // ✅ Variable penampung data produk (List)
    var products: List<Produk> by mutableStateOf(emptyList())

    // Status Loading
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    // ✅ Fungsi Load Data
    fun loadProducts() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Panggil API getProducts
                products = RetrofitClient.instance.getProducts()
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // ✅ FUNGSI HAPUS PRODUK (BARU)
    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            // Jangan set isLoading true agar layar tidak berkedip parah, cukup background process
            try {
                // Panggil API Delete
                RetrofitClient.instance.deleteProduct(id)

                // Jika sukses, muat ulang daftar produk biar item yang dihapus hilang
                loadProducts()

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Gagal menghapus: ${e.message}"
            }
        }
    }
}