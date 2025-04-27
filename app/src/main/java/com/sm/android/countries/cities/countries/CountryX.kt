package com.sm.android.countries.cities.countries

import com.sm.android.countries.cities.countries.model.City
import com.sm.android.countries.cities.countries.model.TimeZone

import kotlinx.serialization.Serializable

@Serializable
data class CountryX(
    val id: Int,
    val name: String,
    val capital: String,
    val native: String?, // <- make it nullable
    val region: String,
    val timezones: List<TimeZoneX>,
    val latitude: String,
    val longitude: String,
    val emoji: String,
    val cities: List<CityX>
)



@Serializable
data class CityX(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String
)

@Serializable
data class TimeZoneX(
    val zoneName: String,
    val gmtOffset: Int,
    val gmtOffsetName: String,
    val abbreviation: String,
    val tzName: String
)
