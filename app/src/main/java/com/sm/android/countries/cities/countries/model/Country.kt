package com.sm.android.countries.cities.countries.model

data class Country(
    val id: Int,
    val name: String,
    val capital: String,
    val native: String,
    val region: String,
    val timezones: List<TimeZone>,
    val latitude: String,
    val longitude: String,
    val emoji: String,
    val cities: List<City>
)