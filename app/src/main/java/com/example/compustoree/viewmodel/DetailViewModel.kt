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

    var produk: Produk? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)

    // Variabel untuk menampung pesan error/sukses
    var statusMessage by mutableStateOf("")

    fun getProductById(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                produk = RetrofitClient.instance.getProductById(id)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // UPDATE FUNGSI DELETE
    fun deleteProduct(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            statusMessage = "" // Reset pesan
            try {
                RetrofitClient.instance.deleteProduct(id)
                statusMessage = "Berhasil Dihapus"
                onSuccess() // Pindah layar hanya jika sukses
            } catch (e: Exception) {
                // Tampilkan error ke layar jika gagal (misal: constraint db)
                statusMessage = "Gagal Hapus: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}