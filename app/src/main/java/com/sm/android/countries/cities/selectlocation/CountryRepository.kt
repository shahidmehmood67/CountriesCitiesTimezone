package com.sm.android.countries.cities.selectlocation

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sm.android.countries.cities.countries.model.City
import com.sm.android.countries.cities.countries.model.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CountryRepository {
    suspend fun getCountriesData(context: Context): List<Country> = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("data/countries_cities.json").bufferedReader().use { it.readText() }
        Gson().fromJson(jsonString, object : TypeToken<List<Country>>() {}.type)
    }

    suspend fun findCityByTimezone(countries: List<Country>, timezone: String): City? = withContext(Dispatchers.IO) {
        val parts = timezone.split("/")
        if (parts.size < 2) return@withContext null // Invalid format

        val cityNameQuery = normalizeText(parts.last()) // Take the LAST part of the timezone

        // 1️⃣ First, find the country with matching timezone
        val matchedCountry = countries.find { country ->
            country.timezones.any {
                normalizeText(it.zoneName) == normalizeText(timezone)
            }
        }

        // If a country is found with matching timezone, search only its cities
        matchedCountry?.let { country ->
            // First, try simple direct match
            country.cities.find { city ->
                normalizeText(city.name) == cityNameQuery
            }?.let { return@withContext it }

            // If direct match fails, use complex similarity check
            country.cities.find { city ->
                val normalizedCityName = normalizeText(city.name)
                isSimilar(cityNameQuery, normalizedCityName)
            }?.let { return@withContext it }
        }


        // 2️⃣ If No City Found, then  Country
        matchedCountry?.let {country->
                return@withContext City(
                    id = -1, // No specific city ID
                    name = country.name, // Set country as city name
                    latitude = country.latitude,
                    longitude = country.longitude
                )
            }

        // If no match found in the timezone's country, return null
        return@withContext null
    }

    // Utility function for text normalization
    fun normalizeText(input: String): String {
        return input.lowercase()
            .replace("[^a-z0-9]".toRegex(), "")
    }

    // Advanced similarity checking function
    fun isSimilar(query: String, cityName: String): Boolean {
        // More complex similarity checking
        // Add your advanced similarity logic here
        return cityName.contains(query) ||
                query.length > 2 && (
                cityName.startsWith(query) ||
                        // Add more sophisticated similarity checks
                        levenshteinDistance(query, cityName) <= minOf(query.length, cityName.length) / 3
                )
    }

    // Levenshtein distance for more advanced similarity
    fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j

        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (s1[i-1] == s2[j-1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i-1][j] + 1,      // Deletion
                    dp[i][j-1] + 1,      // Insertion
                    dp[i-1][j-1] + cost  // Substitution
                )
            }
        }

        return dp[m][n]
    }
}

//Recheck again coutries time zones missings (will complete later)
// Antartica, Australia

//Recheck again for countries cities missings (will complete later)
// Albania, Australia