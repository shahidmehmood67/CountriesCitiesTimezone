package com.sm.android.countries.cities.countries

data class LocationDetails(
    val latitude: Double,
    val longitude: Double,
    val city: String = "Macca",
    val country: String = "Saudi Arabia"
)