package com.example.compustoree.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compustoree.model.UserSession

// --- DATA CLASS UNTUK ITEM BOTTOM BAR ---
sealed class BottomBarScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomBarScreen("home", "Home", Icons.Default.Home)
    object Riwayat : BottomBarScreen("riwayat", "Riwayat", Icons.Default.DateRange)
    object Profile : BottomBarScreen("profile", "Profil", Icons.Default.Person)
}

@Composable
fun PengelolaHalaman(
    navController: NavHostController = rememberNavController()
) {
    // Daftar halaman yang WAJIB menampilkan Bottom Bar
    val screensWithBottomBar = listOf("home", "riwayat", "profile")

    // Ambil rute saat ini untuk menentukan visibilitas Bottom Bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Hanya tampilkan Bottom Bar jika rute saat ini ada di dalam list
            if (currentRoute in screensWithBottomBar) {
                NavigationBar {
                    val items = listOf(
                        BottomBarScreen.Home,
                        BottomBarScreen.Riwayat,
                        BottomBarScreen.Profile
                    )
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    // Agar tidak menumpuk halaman saat diklik berkali-kali
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        // --- NAV HOST UTAMA ---
        NavHost(
            navController = navController,
            startDestination = "splash", // ✅ APLIKASI DIMULAI DARI SPLASH
            modifier = Modifier.padding(innerPadding)
        ) {

            // 0. SPLASH SCREEN
            composable("splash") {
                SplashScreen(
                    onTimeout = {
                        // Pindah ke Home setelah selesai timer
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true } // Hapus splash dari backstack
                        }
                    }
                )
            }

            // 1. HOME SCREEN (Bisa diakses Guest)
            composable("home") {
                HomeScreen(
                    onProdukClick = { id -> navController.navigate("detail/$id") },
                    // Parameter navigasi manual (opsional karena sudah ada BottomBar)
                    onRiwayatClick = { navController.navigate("riwayat") },
                    onProfileClick = { navController.navigate("profile") },
                    // Fitur Admin
                    onAddProductClick = { navController.navigate("add_product") },
                    onEditProductClick = { id -> navController.navigate("edit_product/$id") }
                )
            }

            // 2. LOGIN SCREEN
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        // Setelah login sukses, kembali ke halaman sebelumnya (biasanya Home/Detail)
                        navController.popBackStack()
                    },
                    onRegisterClick = { navController.navigate("register") }
                )
            }

            // 3. REGISTER SCREEN
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = { navController.popBackStack() }, // Balik ke Login
                    onLoginClick = { navController.popBackStack() }
                )
            }

            // 4. DETAIL PRODUK
            composable(
                route = "detail/{produkId}",
                arguments = listOf(navArgument("produkId") { type = NavType.IntType })
            ) { backStackEntry ->
                val produkId = backStackEntry.arguments?.getInt("produkId") ?: 0
                DetailScreen(
                    produkId = produkId,
                    onBackClick = { navController.popBackStack() },
                    onBuyClick = { id ->
                        // ✅ CEK LOGIN DULU SEBELUM CHECKOUT
                        if (UserSession.currentUser != null) {
                            navController.navigate("checkout/$id")
                        } else {
                            navController.navigate("login")
                        }
                    },
                    onEditClick = { id -> navController.navigate("edit_product/$id") }
                )
            }

            // 5. CHECKOUT SCREEN
            composable("checkout/{produkId}") { backStackEntry ->
                val produkId = backStackEntry.arguments?.getString("produkId")?.toIntOrNull() ?: 0
                CheckoutScreen(
                    produkId = produkId,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        // Setelah bayar sukses, pergi ke Riwayat & hapus checkout dari stack
                        navController.navigate("riwayat") {
                            popUpTo("home")
                        }
                    }
                )
            }

            // 6. RIWAYAT SCREEN
            composable("riwayat") {
                // ✅ CEK LOGIN
                if (UserSession.currentUser != null) {
                    RiwayatScreen()
                } else {
                    // Jika belum login, redirect ke Login
                    LaunchedEffect(Unit) { navController.navigate("login") }
                    // Tampilkan loading sementara redirect
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            // 7. PROFILE SCREEN
            composable("profile") {
                if (UserSession.currentUser != null) {
                    // Tampilkan Profil User Asli
                    ProfileScreen(
                        onLogout = {
                            UserSession.currentUser = null
                            // Reset ke Home setelah logout
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                } else {
                    // Tampilan Pengganti untuk Guest (Belum Login)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(80.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Anda belum login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Silakan login untuk melihat profil.", color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { navController.navigate("login") }) {
                            Text("Login Sekarang")
                        }
                    }
                }
            }

            // 8. ADMIN: TAMBAH PRODUK
            composable("add_product") {
                AddProductScreen(onBackClick = { navController.popBackStack() })
            }

            // 9. ADMIN: EDIT PRODUK
            composable("edit_product/{produkId}") { backStackEntry ->
                val produkId = backStackEntry.arguments?.getString("produkId")?.toIntOrNull() ?: 0
                EditProductScreen(
                    produkId = produkId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}