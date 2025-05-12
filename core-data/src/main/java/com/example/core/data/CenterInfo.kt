package com.example.core.data

import com.google.android.gms.maps.model.LatLng

data class CenterInfo(
    val name: String,
    val status: String,
    val phone: String,
    val address: String,
    val location: LatLng
)

