package com.example.compustoree.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("") // Di real app, password harus dicek. Di sini kita simulasi dulu.

    var isLoading by mutableStateOf(false)
    var loginStatus by mutableStateOf("") // "Sukses" atau "Gagal"

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            loginStatus = ""
            try {
                // 1. Minta data user ke server berdasarkan email
                val user = RetrofitClient.instance.getUserByEmail(email)

                // 2. Cek apakah user ada? (Server kita mengembalikan object kosong jika tidak ada)
                if (user.email.isNotEmpty()) {
                    // Cek Password Sederhana (Harusnya di server, tapi ini simulasi)
                    // Kita anggap password selalu benar jika user ditemukan, atau Anda bisa cek manual:
                    // if (password == "admin123" || password == "user123") { ... }

                    // SIMPAN KE SESSION
                    UserSession.currentUser = user
                    loginStatus = "Login Berhasil!"
                    onSuccess()

                } else {
                    loginStatus = "Email tidak terdaftar"
                }
            } catch (e: Exception) {
                loginStatus = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}