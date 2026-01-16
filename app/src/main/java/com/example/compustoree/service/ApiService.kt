package com.example.compustoree.service

import com.example.compustoree.model.OrderRequest
import com.example.compustoree.model.OrderResponse
import com.example.compustoree.model.Produk
import com.example.compustoree.model.RiwayatOrder
import com.example.compustoree.model.User
import retrofit2.http.*

interface ApiService {

    // --- USER ---
    @POST("users")
    suspend fun updateUser(@Query("email") email: String, @Body user: User): User

    @GET("users/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): User

    // --- PRODUK ---
    @GET("products")
    suspend fun getAllProducts(): List<Produk>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Produk

    @POST("products")
    suspend fun addProduct(@Body produk: Produk): Produk

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body produk: Produk): Any

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Any

    // --- TRANSAKSI ---
    @POST("orders")
    suspend fun createOrder(@Body order: OrderRequest): OrderResponse

    // Ambil Riwayat (User Biasa)
    @GET("orders")
    suspend fun getRiwayat(@Query("email") email: String?): List<RiwayatOrder>

    // Ambil SEMUA Riwayat (Admin) - Maps ke /api/transactions di server
    @GET("transactions")
    suspend fun getAllTransactions(): List<RiwayatOrder>

    // Update Status Transaksi
    @PUT("orders/{id}/status")
    suspend fun updateStatusOrder(
        @Path("id") id: Int,
        @Body body: Map<String, String> // Kirim JSON {"status": "..."}
    ): Any

    // Hapus Transaksi
    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int): Any
}