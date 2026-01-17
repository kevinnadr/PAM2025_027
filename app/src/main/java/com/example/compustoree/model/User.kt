package com.example.compustoree.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id_user")
    val id: Int = 0,

    @SerializedName("nama")
    val nama: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("password")
    val password: String? = "",

    @SerializedName("no_hp")
    val noHp: String?,

    @SerializedName("role")
    val role: String? = "user",

    // --- TAMBAHAN BARU (Biar tidak error di ProfileScreen) ---
    @SerializedName("foto_url")
    val fotoUrl: String? = null,

    @SerializedName("alamat")
    val alamat: String? = null
)