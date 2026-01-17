package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.RegisterRequest // ✅ Pastikan Import ini benar
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var nama by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var noHp by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var message by mutableStateOf("")

    // Fungsi register menerima parameter onSuccess
    fun register(onSuccess: () -> Unit) {
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || noHp.isEmpty()) {
            message = "Semua kolom harus diisi!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // ✅ PERBAIKAN DI SINI:
                // Gunakan 'RegisterRequest', JANGAN 'User'
                val request = RegisterRequest(
                    nama = nama,
                    email = email,
                    password = password,
                    noHp = noHp
                )

                // Kirim request ke API
                val response = RetrofitClient.instance.register(request)

                message = response.message

                // Jika sukses, panggil navigasi
                onSuccess()

            } catch (e: Exception) {
                message = "Registrasi Gagal: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}