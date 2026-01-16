package com.example.compustoree.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.User
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    // Data Form
    var nama by mutableStateOf("")
    var email by mutableStateOf("")
    var noHp by mutableStateOf("")
    var alamat by mutableStateOf("")

    // Status
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf("")

    fun register(onSuccess: () -> Unit) {
        if (nama.isEmpty() || email.isEmpty()) {
            message = "Nama dan Email wajib diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // Siapkan data user baru
                // Note: Password default di server saat ini 'user123' (sesuai server.js kita kemarin)
                val newUser = User(
                    email = email,
                    nama = nama,
                    noHp = noHp,
                    alamat = alamat,
                    role = "user"
                )

                // Kirim ke server
                val response = RetrofitClient.instance.updateUser(email, newUser)

                message = "Registrasi Berhasil!"
                onSuccess()

            } catch (e: Exception) {
                message = "Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}