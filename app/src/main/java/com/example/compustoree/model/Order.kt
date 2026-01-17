package com.example.compustoree.model

import com.google.gson.annotations.SerializedName

// Model untuk mengirim data pesanan ke server (Checkout)
data class OrderRequest(
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("total_harga") val totalHarga: Double,
    @SerializedName("metode_pembayaran") val metodePembayaran: String,
    @SerializedName("metode_pengiriman") val metodePengiriman: String,
    @SerializedName("alamat_pengiriman") val alamatPengiriman: String,
    @SerializedName("items") val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @SerializedName("produk_id") val produkId: Int,
    @SerializedName("jumlah") val jumlah: Int,
    @SerializedName("harga") val harga: Double
)

// Model untuk menerima respon setelah checkout
data class OrderResponse(
    @SerializedName("message") val message: String,
    @SerializedName("id_transaksi") val idTransaksi: Int?
)