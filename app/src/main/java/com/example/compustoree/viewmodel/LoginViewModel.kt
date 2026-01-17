package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.LoginRequest // ✅ Pastikan Import ini ada
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var message by mutableStateOf("")

    fun login(onSuccess: () -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            message = "Email dan Password harus diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // ❌ KODE LAMA (Penyebab Error):
                // val body = mapOf("email" to email, "password" to password)

                // ✅ KODE BARU (SOLUSI): Gunakan Data Class LoginRequest
                val request = LoginRequest(email, password)

                val response = RetrofitClient.instance.login(request)

                if (response.user != null) {
                    UserSession.currentUser = response.user
                    message = "Login Berhasil"
                    onSuccess()
                } else {
                    message = "Login Gagal: User tidak ditemukan"
                }
            } catch (e: Exception) {
                // Menangani error koneksi atau password salah
                message = "Login Gagal: Periksa Email/Password"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}