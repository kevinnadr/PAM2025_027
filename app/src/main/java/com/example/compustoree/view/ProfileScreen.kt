package com.example.compustoree.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. FOTO PROFIL
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.user?.fotoUrl != null && viewModel.user?.fotoUrl != "") {
                    AsyncImage(model = viewModel.user?.fotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(80.dp), tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. DATA USER
            val user = viewModel.user
            if (user != null) {
                Text(user.nama ?: "Tanpa Nama", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(user.email, color = Color.Gray)

                Spacer(modifier = Modifier.height(32.dp))

                // Info Tambahan
                InfoRow("Role", user.role.uppercase())
                InfoRow("No HP", user.noHp ?: "-")
                InfoRow("Alamat", user.alamat ?: "-")
            }

            Spacer(modifier = Modifier.weight(1.0f)) // Dorong tombol ke bawah

            // 3. TOMBOL LOGOUT
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("KELUAR (LOGOUT)")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Divider(modifier = Modifier.padding(top = 4.dp))
    }
}