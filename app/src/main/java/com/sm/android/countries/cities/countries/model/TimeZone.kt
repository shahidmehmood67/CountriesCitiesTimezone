package com.sm.android.countries.cities.countries.model

data class TimeZone(
    val zoneName: String,
    val gmtOffset: Int,
    val gmtOffsetName: String,
    val abbreviation: String,
    val tzName: String
)