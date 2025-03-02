package com.sm.android.countries.cities.countries

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.ZoneId
import java.util.TimeZone

class CitiesViewModel(application: Application) : AndroidViewModel(application) {

    private val _countryMap = MutableLiveData<Map<String, CountryInfo>>()
    val countryMap: LiveData<Map<String, CountryInfo>> get() = _countryMap

    private val _cityName = MutableLiveData<CountryInfo?>()
    val cityName: LiveData<CountryInfo?> get() = _cityName

    init {
        loadAllCountries()
    }

    private fun loadAllCountries() {
        viewModelScope.launch(Dispatchers.IO) {
            val countryData = mutableMapOf<String, CountryInfo>()
            try {
                val assetManager = getApplication<Application>().assets
                val files = assetManager.list("countries") // Get all country files

                files?.forEach { fileName ->
                    val countryName = fileName.removeSuffix(".txt") // Extract country name
                    val cityList = mutableListOf<CityInfo>()

                    val inputStream = assetManager.open("countries/$fileName")
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    reader.forEachLine { line ->
                        val parts = line.split(",")
                        if (parts.size == 6) {
                            val cityInfo = CityInfo(
                                city = parts[1].trim(),
                                latitude = parts[2].toDouble(),
                                longitude = parts[3].toDouble(),
                                timezone = parts[4].trim()
                            )
                            cityList.add(cityInfo)
                        }
                    }
                    reader.close()

                    countryData[countryName] = CountryInfo(countryName, cityList)
                }
                _countryMap.postValue(countryData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchCityByName(cityName: String): CountryInfo? {
        return _countryMap.value?.entries
            ?.asSequence()
            ?.mapNotNull { (_, countryInfo) ->
                countryInfo.cities.find {
                    it.city.equals(cityName, ignoreCase = true)  ||
                    it.city.contains(cityName, ignoreCase = true)
                }
                    ?.let { matchedCity -> countryInfo.copy(cities = listOf(matchedCity)) }
            }
            ?.firstOrNull() // Returns null if no match found
            ?: run {
                Log.e("CitySearch", "City '$cityName' not found")
                null // Explicitly returning null
            }
    }

    fun getCitiesByCountry(countryName: String): List<CityInfo>? {
        return _countryMap.value?.get(countryName)?.cities
    }

    fun fetchCheckUpdatePreciseLocation(){
        val currentZone = searchCityByName(getCityFromTimeZone())
        if (_cityName == null || _cityName.value?.cities?.first()?.city != currentZone?.cities?.first()?.city) {
            if (countryMap.value?.isNotEmpty() == true) {
                _cityName.postValue(currentZone)
            } else {
                loadAllCountries()
            }
        }
    }

    fun getCityFromTimeZone(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId.systemDefault().id.substringAfterLast("/") // API 26+
        } else {
            TimeZone.getDefault().id.substringAfterLast("/") // Below API 26
        }.replace("_", " ") // Replace underscores with spaces
    }
}
