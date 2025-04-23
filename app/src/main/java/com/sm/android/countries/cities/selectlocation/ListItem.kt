package com.sm.android.countries.cities.selectlocation

import com.sm.android.countries.cities.countries.model.City

// 1. ListItem.kt
sealed class ListItem {
    data class Header(val title: String) : ListItem()
    data class CityItem(val city: City) : ListItem()
}