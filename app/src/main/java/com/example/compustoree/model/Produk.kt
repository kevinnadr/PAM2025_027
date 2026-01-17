package com.example.compustoree.model

import com.google.gson.annotations.SerializedName

data class Produk(
    // ✅ Sesuaikan dengan kolom 'id_produk' di database
    @SerializedName("id_produk")
    val id: Int,

    // ✅ Sesuaikan dengan kolom 'nama_produk' di database (INI PERBAIKAN UTAMANYA)
    @SerializedName("nama_produk")
    val nama: String?,

    @SerializedName("kategori")
    val kategori: String?,

    @SerializedName("merk")
    val merk: String?,

    @SerializedName("harga")
    val harga: Double?,

    @SerializedName("stok")
    val stok: Int?,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("gambar")
    val gambar: String? = null
)