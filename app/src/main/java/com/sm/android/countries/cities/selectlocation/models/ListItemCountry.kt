package com.sm.android.countries.cities.selectlocation.models

import com.sm.android.countries.cities.countries.model.City
import com.sm.android.countries.cities.countries.model.Country

// 1. ListItem.kt
sealed class ListItemCountry {
    data class Header(val title: String) : ListItemCountry()
    data class ListItem(val city: Country) : ListItemCountry()
}