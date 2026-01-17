package com.example.compustoree.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compustoree.viewmodel.ProfileViewModel
import com.example.compustoree.model.UserSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser = UserSession.currentUser

    // Handle Toast Message
    LaunchedEffect(viewModel.message) {
        if (viewModel.message.isNotEmpty()) {
            Toast.makeText(context, viewModel.message, Toast.LENGTH_SHORT).show()
            viewModel.message = ""
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5) // Background abu-abu muda
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- HEADER MODERN ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                // Background Gradient Melengkung
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF6200EE))
                            )
                        )
                )

                // Judul Halaman
                Text(
                    text = "Profil Saya",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp)
                )

                // Foto Profil & Nama
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Icon
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(100.dp).border(4.dp, Color.White, CircleShape)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.background(Color(0xFFE0E0E0))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nama User (Tampil hanya jika TIDAK sedang edit)
                    if (!viewModel.isEditing) {
                        Text(
                            text = viewModel.nama.ifEmpty { "User" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = currentUser?.email ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // --- FORM DATA ---
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Tombol Edit di Pojok Kanan (Jika belum edit)
                if (!viewModel.isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Profil")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Field: Nama Lengkap
                ProfileField(
                    label = "Nama Lengkap",
                    value = viewModel.nama,
                    onValueChange = { viewModel.nama = it },
                    isEditing = viewModel.isEditing,
                    icon = Icons.Default.Person
                )

                // Field: Nomor HP
                ProfileField(
                    label = "Nomor HP",
                    value = viewModel.phone,
                    onValueChange = { viewModel.phone = it },
                    isEditing = viewModel.isEditing,
                    icon = Icons.Default.Phone
                )

                // Field: Alamat (NEW)
                ProfileField(
                    label = "Alamat Pengiriman",
                    value = viewModel.alamat,
                    onValueChange = { viewModel.alamat = it },
                    isEditing = viewModel.isEditing,
                    icon = Icons.Default.Home,
                    isMultiLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- TOMBOL AKSI ---
                if (viewModel.isEditing) {
                    // Tombol SAVE & CANCEL
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.cancelEdit() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal")
                        }

                        Button(
                            onClick = { viewModel.saveProfile() },
                            modifier = Modifier.weight(1f),
                            enabled = !viewModel.isLoading
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Simpan")
                            }
                        }
                    }
                } else {
                    // Tombol LOGOUT (Warna Merah)
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- KOMPONEN INPUT / TEXT FIELD ---
@Composable
fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    icon: ImageVector,
    isMultiLine: Boolean = false
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        if (isEditing) {
            // Tampilan saat Mode Edit (TextField)
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                leadingIcon = { Icon(icon, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = if (isMultiLine) 3 else 1
            )
        } else {
            // Tampilan saat Mode Baca (Hanya Text)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(label, fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = if (value.isEmpty()) "Belum diatur" else value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (value.isEmpty()) Color.LightGray else Color.Black
                    )
                }
            }
            Divider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(top = 12.dp))
        }
    }
}