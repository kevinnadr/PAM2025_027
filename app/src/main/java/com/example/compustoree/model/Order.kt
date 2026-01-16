package com.example.compustoree.model

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("jumlah") val jumlah: Int,
    @SerializedName("total_harga") val totalHarga: Double,
    @SerializedName("metode_pembayaran") val metodePembayaran: String,
    @SerializedName("alamat_pengiriman") val alamatPengiriman: String
)

data class OrderResponse(
    val message: String,
    val id: Int? = 0
)

data class RiwayatOrder(
    @SerializedName("id_transaksi") val id: Int,
    @SerializedName("nama_produk") val namaBarang: String?, // <--- Boleh Null
    @SerializedName("jumlah") val jumlah: Int?,
    @SerializedName("total_harga") val totalHarga: Double?, // <--- Boleh Null
    @SerializedName("status_pengiriman") val status: String?, // <--- Boleh Null
    @SerializedName("tanggal_transaksi") val tanggal: String?,

    // Data Pembeli (Khusus Admin)
    @SerializedName("nama_pembeli") val namaPembeli: String?, // <--- Boleh Null
    @SerializedName("alamat_pengiriman") val alamatKirim: String? // <--- Boleh Null
)