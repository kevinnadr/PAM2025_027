package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.Produk
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    // State data produk
    var produk: Produk? by mutableStateOf(null)

    // State loading
    var isLoading by mutableStateOf(false)

    // State error message
    var errorMessage by mutableStateOf("")

    // ✅ PASTE FUNGSI INI (Inilah yang dicari oleh DetailScreen)
    fun loadProduk(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                // Panggil API
                produk = RetrofitClient.instance.getProductById(id)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Gagal memuat: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}