package com.example.compustoree.model

import com.google.gson.annotations.SerializedName

data class Produk(
    @SerializedName("id_produk") val id: Int,
    // Tambahkan tanda tanya (?) agar aplikasi tidak crash jika data kosong
    @SerializedName("nama_produk") val nama: String?,
    @SerializedName("kategori") val kategori: String?,
    @SerializedName("merk") val merk: String?,
    @SerializedName("harga") val harga: Double?, // Harga juga bisa null
    @SerializedName("stok") val stok: Int?,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("gambar") val gambar: String?
)