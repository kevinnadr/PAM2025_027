package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.RiwayatOrder
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

// State Pattern untuk Mengatur Tampilan UI (Loading, Sukses, Error)
sealed interface RiwayatUiState {
    data class Success(val orders: List<RiwayatOrder>) : RiwayatUiState
    object Error : RiwayatUiState
    object Loading : RiwayatUiState
}

class RiwayatViewModel : ViewModel() {

    // Variable State Utama
    var uiState: RiwayatUiState by mutableStateOf(RiwayatUiState.Loading)
        private set

    // Cek apakah yang login adalah Admin?
    val isAdmin = UserSession.currentUser?.role == "admin"

    // 1. Fungsi Load Data (Pintar Membedakan User/Admin)
    fun loadData() {
        viewModelScope.launch {
            uiState = RiwayatUiState.Loading // Set status loading dulu
            try {
                val result = if (isAdmin) {
                    // Jika Admin: Panggil API Ambil SEMUA Transaksi
                    RetrofitClient.instance.getAllTransactions()
                } else {
                    // Jika User Biasa: Panggil API Ambil Riwayat Sendiri
                    val email = UserSession.currentUser?.email
                    RetrofitClient.instance.getRiwayat(email)
                }

                // Sukses
                uiState = RiwayatUiState.Success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = RiwayatUiState.Error
            }
        }
    }

    // 2. Admin: Update Status Pesanan (Diproses -> Dikirim -> Selesai)
    fun updateStatus(idTransaksi: Int, statusBaru: String) {
        viewModelScope.launch {
            try {
                val body = mapOf("status" to statusBaru)
                RetrofitClient.instance.updateStatusOrder(idTransaksi, body)

                // Refresh data otomatis agar tampilan berubah
                loadData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 3. Admin: Hapus Pesanan
    fun deleteOrder(idTransaksi: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteTransaction(idTransaksi)

                // Refresh data otomatis agar pesanan hilang dari layar
                loadData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}