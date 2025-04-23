package com.sm.android.countries.cities.selectlocation

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sm.android.countries.cities.countries.CityInfo
import com.sm.android.countries.cities.countries.CountryInfo
import com.sm.android.countries.cities.countries.model.City
import com.sm.android.countries.cities.countries.model.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.util.TimeZone

class CitiesNewViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CountryRepository()

    private val _countryMap = MutableLiveData<Map<String, CountryInfo>>()
    val countryMap: LiveData<Map<String, CountryInfo>> get() = _countryMap

    private val _cityName = MutableLiveData<CountryInfo?>()
    val cityName: LiveData<CountryInfo?> get() = _cityName


    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> = _countries

    private val _matchedCountry = MutableLiveData<Country?>()
    val matchedCountry: LiveData<Country?> = _matchedCountry

    private val _matchedCities = MutableLiveData<List<City>>()
    val matchedCities: LiveData<List<City>> = _matchedCities

    private val _matchedCitiesHeader = MutableLiveData<List<ListItem>>()
    val matchedCitiesHeader: LiveData<List<ListItem>> = _matchedCitiesHeader


    private val _selectedCity = MutableLiveData<City?>()
    val selectedCity: LiveData<City?> = _selectedCity

    init {
        fetchCountryByDeviceTimezone()
    }

    private fun fetchCountryData() {
        viewModelScope.launch {
            _countries.value = repository.getCountriesData(getApplication())
        }
    }

    private fun fetchCountryByDeviceTimezone() {
        viewModelScope.launch {
            val allCountries = repository.getCountriesData(getApplication())
            val deviceTimeZone = TimeZone.getDefault().id

            val matchedCountry = allCountries.find { country ->
                country.timezones.any { it.zoneName == deviceTimeZone }
            }

            _countries.value = allCountries

            setMatchedCountryData(matchedCountry)

        }
    }

    fun setMatchedCountryData(matchCountry: Country?) {
        if (matchCountry == null) {
            _matchedCountry.value = null
            _matchedCities.value = emptyList()
            return
        }

        _matchedCountry.value = matchCountry

        val cities = if (matchCountry.cities.isNullOrEmpty()) {
            listOf(
                City(
                    id = matchCountry.id,
                    name = matchCountry.name,
                    latitude = matchCountry.latitude,
                    longitude = matchCountry.longitude
                )
            )
        } else {
            matchCountry.cities
        }

        _matchedCitiesHeader.value =  buildSectionedCityList(cities)
        _matchedCities.value = cities
    }

    // 2. SectionBuilder.kt
    fun buildSectionedCityList(cities: List<City>): List<ListItem> {
        return cities
            .sortedBy { it.name }
            .groupBy { it.name.first().uppercaseChar() }
            .flatMap { (initial, group) ->
                listOf(ListItem.Header(initial.toString())) + group.map { ListItem.CityItem(it) }
            }
    }

    fun getCitiesByCountry(countryName: String): List<CityInfo>? {
        return _countryMap.value?.get(countryName)?.cities
    }


    suspend fun searchCity(countrytimezone: String): City? {
        return withContext(Dispatchers.IO) {
            _countries.value?.let { repository.findCityByTimezone(it, countrytimezone) }
        }
    }

    fun getCityFromTimeZone(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId.systemDefault().id.substringAfterLast("/") // API 26+
        } else {
            TimeZone.getDefault().id.substringAfterLast("/") // Below API 26
        }.replace("_", " ") // Replace underscores with spaces
    }

    fun getTimeZone(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId.systemDefault().id // API 26+
        } else {
            TimeZone.getDefault().id // Below API 26
        }.replace("_", " ") // Replace underscores with spaces
    }

}
