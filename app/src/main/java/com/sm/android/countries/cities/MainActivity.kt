package com.sm.android.countries.cities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.viewModels
import com.google.gson.Gson
import com.sm.android.countries.cities.countries.repository.CitiesViewModel
import com.sm.android.countries.cities.countries.CountriesActivity
import com.sm.android.countries.cities.countries.LocationDetails
import com.sm.android.countries.cities.databinding.ActivityMainBinding
import com.sm.android.countries.cities.utils.prefManager
import org.json.JSONObject
import java.net.URL
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val vmCities: CitiesViewModel by viewModels()
    private var onpaused  = false

    private var lastKnownTimeZone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()

            startActivity(Intent(this, CountriesActivity::class.java))
        }

        Gson().fromJson(prefManager.getLocationDetails(), LocationDetails::class.java
        )?.let {
            binding.textviewFirst.text = "${it.city}, ${it.country}"
        }

        initfunc()

        initobserver()

    }

    private fun initobserver() {
        vmCities.countryMap.observe(this) { countryMap ->
            Toast.makeText(this, "Loaded ${countryMap.size} countries", Toast.LENGTH_LONG).show()
            vmCities.fetchCheckUpdatePreciseLocation()
        }

        vmCities.countries.observe(this) { countrieslist ->
            Toast.makeText(this, "Loaded ${countrieslist.size} countries", Toast.LENGTH_LONG).show()
            vmCities.fetchCheckUpdatePreciseLocationTwo()
        }

        vmCities.cityName.observe(this) { countryinfo ->
            countryinfo?.let {
                val country = countryinfo.countryName
                val city = countryinfo.cities.first().city
                val lat = countryinfo.cities.first().latitude
                val long = countryinfo.cities.first().longitude
                val citydetail = String.format("country: %s\n city: %s\n lat:%s long:%s", country, city, lat, long)
                binding.tvLocationZone.text = citydetail
                Toast.makeText(this, "country: $country city: $city ", Toast.LENGTH_LONG).show()
            } ?: run {
                binding.tvLocationZone.text = "city zone not found"
            }
        }

        vmCities.selectedCity.observe(this) { selectedCity ->
            selectedCity?.let {
//                val country = countryinfo.countryName
                val city = it.name
                val lat = it.latitude
                val long = it.longitude
                val citydetail = String.format("city: %s\n lat:%s long:%s", city, lat, long)
                binding.tvLocationZoneNew.text = citydetail
                Toast.makeText(this, " city: $city ", Toast.LENGTH_LONG).show()
            } ?: run {
                binding.tvLocationZoneNew.text = "city zone not found"
            }
        }
    }

    private fun initfunc() {
        binding.tvgetSimCountry.text = getSimCountry()
        binding.tvgetCityFromIP.text = getCityFromIP()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
            println("User ZonedDateTime: ${zonedDateTime.zone}") // Example: "Asia/Kolkata"
            binding.tvZonedDateTime.text = zonedDateTime.zone.toString()
        }else{ }

        val timeZone = getCountryFromTimezone()
        println("User Timezone: $timeZone") // Example: "America/New_York"

        val timeZoneDisplay = getDisplayNameFromTimezone()
        println("User TimezoneDisplay: $timeZoneDisplay")

        binding.tvTimeZone.text = timeZone
        binding.tvDisplay.text = timeZoneDisplay

        val timeZoneutc = TimeZone.getDefault()
        val formattedOffset = formatOffset(timeZoneutc.rawOffset)
        println("Formatted Offset: $formattedOffset") // Example: "UTC +5:30" for Asia/Kolkata
        binding.tvUtc.text = formattedOffset
    }

    fun getSimCountry(): String? {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkCountryIso?.uppercase() // Not city, but country
    }
    fun getCityFromIP(): String? {
        return try {
            val response = URL("http://ip-api.com/json").readText()
            val json = JSONObject(response)
            json.getString("city")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun getCountryFromTimezone(): String {
        val timeZone = TimeZone.getDefault()
        return timeZone.id // Example: "Asia/Kolkata"
    }
    fun getDisplayNameFromTimezone(): String {
        val timeZone = TimeZone.getDefault()
        return timeZone.displayName
    }
    fun formatOffset(offsetMillis: Int): String {
        val hours = offsetMillis / (1000 * 60 * 60)
        val minutes = (offsetMillis / (1000 * 60)) % 60
        return "UTC ${if (hours >= 0) "+" else ""}$hours:${if (minutes == 0) "00" else minutes}"
    }

    override fun onResume() {
        super.onResume()
        initfunc()
        if (onpaused){
            onpaused = false
            vmCities.fetchCheckUpdatePreciseLocation()
            vmCities.fetchCheckUpdatePreciseLocationTwo()
        }
    }

    override fun onPause() {
        super.onPause()
        onpaused = true
    }


}