package com.sm.android.countries.cities.selectlocation

import com.sm.android.countries.cities.countries.model.Country


interface DialogItemCallback {
    fun itemClicked(item : Country)
}