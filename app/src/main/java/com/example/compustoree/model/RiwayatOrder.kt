package com.example.compustoree.model

import com.google.gson.annotations.SerializedName

data class RiwayatOrder(
    @SerializedName("id_transaksi")
    val idTransaksi: Int,

    @SerializedName("status_pengiriman")
    val statusPengiriman: String?,

    @SerializedName("tanggal_transaksi")
    val tanggalTransaksi: String?,

    @SerializedName("total_harga")
    val totalHarga: Double?,

    @SerializedName("jumlah")
    val jumlah: Int?,

    // --- FIELD BARU UNTUK UI BARU ---
    @SerializedName("nama_produk")
    val namaProduk: String?,

    @SerializedName("gambar")
    val gambarProduk: String?,

    @SerializedName("nama_pembeli")
    val namaPembeli: String?,

    @SerializedName("alamat_pengiriman")
    val alamatPengiriman: String?
)