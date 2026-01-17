package com.example.compustoree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.compustoree.view.PengelolaHalaman // Pastikan ini di-import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Panggil fungsi navigasi utama yang sudah kita buat di Navigasi.kt
            PengelolaHalaman()
        }
    }
}