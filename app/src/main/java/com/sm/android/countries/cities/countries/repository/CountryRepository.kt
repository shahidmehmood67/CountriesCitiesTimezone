package com.sm.android.countries.cities.countries.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sm.android.countries.cities.countries.model.City
import com.sm.android.countries.cities.countries.model.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.Locale

class CountryRepository {

    suspend fun getCountriesData(context: Context): List<Country> = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("data/countries_cities.json").bufferedReader().use { it.readText() }
        Gson().fromJson(jsonString, object : TypeToken<List<Country>>() {}.type)
    }

    suspend fun findCityByTimezone(countries: List<Country>, timezone: String): City? = withContext(Dispatchers.IO) {
        val parts = timezone.split("/")
        if (parts.size != 2) return@withContext null // Invalid format

        val region = parts[0] // Example: "Europe"
        val cityNameQuery = normalizeText(parts[1]) // Example: "Mariehamn"

        // 1️⃣ First, Try Finding the City Inside `cities[]`
        countries.forEach { country ->
            country.cities.find { normalizeText(it.name) == cityNameQuery }?.let { return@withContext it }
        }

        // 2️⃣ If No City Found, Search for Matching Country Timezone
        countries.forEach { country ->
            country.timezones.find { normalizeText(it.zoneName) == normalizeText(timezone) }?.let {
                return@withContext City(
                    id = -1, // No specific city ID
                    name = country.name, // Set country as city name
                    latitude = country.latitude,
                    longitude = country.longitude
                )
            }
        }

        return@withContext null // No match found
    }


    suspend fun findCity(countries: List<Country>, cityName: String): City? = withContext(Dispatchers.IO) {
        val normalizedQuery = normalizeText(cityName)

        // 1️⃣ First, Try Exact Match (Case-Insensitive)
        countries.forEach { country ->
            country.cities.find { normalizeText(it.name) == normalizedQuery }?.let { return@withContext it }
        }

        // 2️⃣ If Not Found, Try Approximate Matching
        countries.forEach { country ->
            country.cities.find { isSimilar(normalizedQuery, normalizeText(it.name)) }?.let { return@withContext it }
        }

        return@withContext null // No match found
    }

    private fun normalizeText(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}"), "") // Remove accents (é → e, ā → a, etc.)
            .lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9]"), "") // Remove special characters
    }

    private fun isSimilar(query: String, cityName: String): Boolean {
        if (cityName.contains(query)) return true

        val similarPatterns = listOf(
            "-" to "", "’" to "", "i" to "ī", "e" to "ē", "a" to "ā", "o" to "ō", "u" to "ū"
        )

        var modifiedCityName = cityName
        similarPatterns.forEach { (old, new) ->
            modifiedCityName = modifiedCityName.replace(old, new)
        }

        return modifiedCityName.contains(query)
    }
}