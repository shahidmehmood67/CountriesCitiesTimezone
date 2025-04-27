package com.sm.android.countries.cities.selectlocation

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sm.android.countries.cities.countries.CityInfo
import com.sm.android.countries.cities.countries.CountryInfo
import com.sm.android.countries.cities.countries.model.City
import com.sm.android.countries.cities.countries.model.Country
import com.sm.android.countries.cities.selectlocation.models.ListItemCountry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val _headercountries = MutableLiveData<List<ListItemCountry>>()
    val headercountries: LiveData<List<ListItemCountry>> = _headercountries

    private var originalCountries: List<ListItemCountry> = emptyList()
    private var originalCities: List<ListItem> = emptyList()

    private val _matchedCountry = MutableLiveData<Country?>()
    val matchedCountry: LiveData<Country?> = _matchedCountry

    private val _matchedCities = MutableLiveData<List<City>>()
    val matchedCities: LiveData<List<City>> = _matchedCities

    private val _matchedCitiesHeader = MutableLiveData<List<ListItem>>()
    val matchedCitiesHeader: LiveData<List<ListItem>> = _matchedCitiesHeader


    private val _selectedCity = MutableLiveData<City?>()
    val selectedCity: LiveData<City?> = _selectedCity

    private val _searchCountryQuery = MutableStateFlow("")
    private val _searchCityQuery = MutableStateFlow("")

    init {
        observeCountrySearch()
        observeCitySearch()

        fetchCountryByDeviceTimezone()
    }

    private fun fetchCountryData() {
        viewModelScope.launch {
            _countries.value = repository.getCountriesData(getApplication())
        }
    }

    private fun fetchCountryByDeviceTimezone() {
        viewModelScope.launch {
            Log.e("CitiesNewViewModel", "fetchCountryByDeviceTimezone: line:(75)");
            val allCountries = repository.getCountriesData(getApplication())
            Log.e("CitiesNewViewModel", "fetchCountryByDeviceTimezone: line:(77)");

            val deviceTimeZone = TimeZone.getDefault().id

            val matchedCountry = allCountries.find { country ->
                country.timezones.any { it.zoneName == deviceTimeZone }
            }
            Log.e("CitiesNewViewModel", "fetchCountryByDeviceTimezone: line:(83)");

            setMatchedCountryData(matchedCountry)
            Log.e("CitiesNewViewModel", "fetchCountryByDeviceTimezone: line:(86)");
            val headerCountryList = buildSectionedCountriesList(allCountries)
            Log.e("CitiesNewViewModel", "fetchCountryByDeviceTimezone: line:(88)");
            originalCountries = headerCountryList
            _headercountries.value = headerCountryList
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
        }
        else {
            matchCountry.cities
        }
        Log.e("CitiesNewViewModel", "setMatchedCountryData: line:(115)");
        val headerCitiesList = buildSectionedCityList(cities)
        Log.e("CitiesNewViewModel", "setMatchedCountryData: line:(117)");
        originalCities = headerCitiesList
        _matchedCitiesHeader.value = headerCitiesList
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

    fun buildSectionedCountriesList(cities: List<Country>): List<ListItemCountry> {
        return cities
            .sortedBy { it.name }
            .groupBy { it.name.first().uppercaseChar() }
            .flatMap { (initial, group) ->
                listOf(ListItemCountry.Header(initial.toString())) + group.map { ListItemCountry.ListItem(it) }
            }
    }

    private fun observeCountrySearch() {
        viewModelScope.launch {
            _searchCountryQuery
                .debounce(300) // 300ms delay after user stops typing
                .distinctUntilChanged()
                .collect { query ->
                    filterCountries(query)
                }
        }
    }

    private fun observeCitySearch() {
        viewModelScope.launch {
            _searchCityQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    filterCities(query)
                }
        }
    }


    fun setCountrySearchQuery(query: String) {
        _searchCountryQuery.value = query
    }

    fun setCitySearchQuery(query: String) {
        _searchCityQuery.value = query
    }


    fun filterCountries(query: String?) {
        Log.e("CitiesNewViewModel", "filterCountries: line:(175)");
        val filteredList = if (query.isNullOrBlank()) {
            originalCountries
        } else {
            originalCountries
                .filterIsInstance<ListItemCountry.ListItem>()
                .filter { it.city.name.contains(query.trim(), ignoreCase = true) }
                .groupBy { it.city.name.first().uppercaseChar() }
                .flatMap { (initial, group) ->
                    listOf(ListItemCountry.Header(initial.toString())) + group
                }
        }
        Log.e("CitiesNewViewModel", "filterCountries: line:(187)");
        _headercountries.value = filteredList
    }

    fun filterCities(query: String?) {
        Log.e("CitiesNewViewModel", "filterCities: line:(190)");
        val filteredList = if (query.isNullOrBlank()) {
            originalCities
        } else {
            originalCities
                .filterIsInstance<ListItem.CityItem>()
                .filter { it.city.name.contains(query.trim(), ignoreCase = true) }
                .groupBy { it.city.name.first().uppercaseChar() }
                .flatMap { (initial, group) ->
                    listOf(ListItem.Header(initial.toString())) + group
                }
        }
        Log.e("CitiesNewViewModel", "filterCities: line:(202)");
        _matchedCitiesHeader.value = filteredList
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
