package com.example.compustoree.view

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compustoree.model.UserSession

// Definisi Menu Bottom Bar
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Riwayat : BottomNavItem("riwayat", Icons.Default.DateRange, "Riwayat")
    object Profile : BottomNavItem("profile", Icons.Default.AccountCircle, "Profil")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Daftar halaman yang MENAMPILKAN Bottom Bar
    val bottomBarRoutes = listOf("home", "riwayat", "profile")

    // Cek rute saat ini untuk logika Bottom Bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    Scaffold(
        bottomBar = {
            // Tampilkan Bottom Bar hanya di halaman Home, Riwayat, Profile
            if (currentRoute in bottomBarRoutes) {
                NavigationBar {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Riwayat,
                        BottomNavItem.Profile
                    )

                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                // Cek Login Dulu untuk Riwayat & Profil
                                if (item.route == "riwayat" || item.route == "profile") {
                                    if (!UserSession.isLoggedIn()) {
                                        Toast.makeText(context, "Silakan Login dulu", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login")
                                        return@NavigationBarItem
                                    }
                                }

                                // Navigasi Standar
                                navController.navigate(item.route) {
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

        NavHost(
            navController = navController,
            startDestination = "splash", // Mulai dari Splash Screen
            modifier = Modifier.padding(innerPadding)
        ) {

            // ================== SPLASH SCREEN ==================
            composable("splash") {
                SplashScreen(onTimeout = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }

            // ================== 1. HOME SCREEN ==================
            composable("home") {
                HomeScreen(
                    onProdukClick = { produkId -> navController.navigate("detail/$produkId") },
                    onRiwayatClick = { navController.navigate("riwayat") },
                    onProfileClick = { navController.navigate("profile") },
                    onAddProductClick = { navController.navigate("add_product") } // Ke halaman tambah (Admin)
                )
            }

            // ================== 2. DETAIL SCREEN (UPDATE: Ada Edit) ==================
            composable(
                route = "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                DetailScreen(
                    produkId = id,
                    onBackClick = { navController.popBackStack() },
                    onBuyClick = { produkId ->
                        // Cek Login User Biasa
                        if (UserSession.isLoggedIn()) {
                            navController.navigate("checkout/$produkId")
                        } else {
                            Toast.makeText(context, "Login dulu untuk membeli!", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        }
                    },
                    onEditClick = { produkId ->
                        // Navigasi ke Halaman Edit (Admin)
                        navController.navigate("edit_product/$produkId")
                    }
                )
            }

            // ================== 3. LOGIN SCREEN ==================
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                        Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    },
                    onRegisterClick = { navController.navigate("register") }
                )
            }

            // ================== 4. REGISTER SCREEN ==================
            composable("register") {
                RegisterScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.popBackStack()
                        Toast.makeText(context, "Akun dibuat! Silakan Login.", Toast.LENGTH_LONG).show()
                    }
                )
            }

            // ================== 5. CHECKOUT SCREEN ==================
            composable(
                route = "checkout/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                CheckoutScreen(
                    produkId = id,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("home") { popUpTo("home") { inclusive = false } }
                        Toast.makeText(context, "Transaksi Berhasil! Cek Riwayat.", Toast.LENGTH_LONG).show()
                    }
                )
            }

            // ================== 6. RIWAYAT SCREEN ==================
            composable("riwayat") {
                RiwayatScreen()
            }

            // ================== 7. PROFILE SCREEN ==================
            composable("profile") {
                ProfileScreen(
                    onBackClick = {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                    },
                    onLogout = {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                        Toast.makeText(context, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // ================== 8. ADD PRODUCT (ADMIN) ==================
            composable("add_product") {
                AddProductScreen(
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        navController.popBackStack()
                        Toast.makeText(context, "Produk Berhasil Ditambah!", Toast.LENGTH_LONG).show()
                    }
                )
            }

            // ================== 9. EDIT PRODUCT (ADMIN - BARU) ==================
            composable(
                route = "edit_product/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                EditProductScreen(
                    produkId = id,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        navController.popBackStack() // Kembali ke Detail
                        Toast.makeText(context, "Produk Berhasil Diupdate!", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}