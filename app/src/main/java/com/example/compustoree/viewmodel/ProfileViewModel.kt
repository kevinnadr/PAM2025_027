package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    // State Mode Edit
    var isEditing by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    // Data Form
    var nama by mutableStateOf(UserSession.currentUser?.nama ?: "")
    var phone by mutableStateOf(UserSession.currentUser?.noHp ?: "")
    var alamat by mutableStateOf(UserSession.currentUser?.alamat ?: "")

    // Pesan Toast
    var message by mutableStateOf("")

    fun saveProfile() {
        val user = UserSession.currentUser
        if (user == null) return

        viewModelScope.launch {
            isLoading = true
            try {
                val body = mapOf(
                    "nama" to nama,
                    "no_hp" to phone,
                    "alamat" to alamat
                )

                val response = RetrofitClient.instance.updateUser(user.id, body)

                // Update Session dengan data baru dari server
                if (response.user != null) {
                    UserSession.currentUser = response.user
                    // Reset field form
                    nama = response.user.nama ?: ""
                    phone = response.user.noHp ?: ""
                    alamat = response.user.alamat ?: ""

                    message = "Profil Berhasil Diupdate!"
                    isEditing = false // Kembali ke mode baca
                }
            } catch (e: Exception) {
                message = "Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun cancelEdit() {
        // Reset kembali ke data asli
        nama = UserSession.currentUser?.nama ?: ""
        phone = UserSession.currentUser?.noHp ?: ""
        alamat = UserSession.currentUser?.alamat ?: ""
        isEditing = false
    }
}