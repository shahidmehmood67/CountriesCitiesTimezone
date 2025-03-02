package com.sm.android.countries.cities.countries

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sm.android.countries.cities.R
import com.sm.android.countries.cities.databinding.ActivityCountriesBinding
import java.io.IOException

class CountriesActivity : AppCompatActivity() {
    private val binding: ActivityCountriesBinding by lazy {
        ActivityCountriesBinding.inflate(layoutInflater)
    }

    var countries = ArrayList<String>()
    var countryList = ArrayList<String>()
    var country: String? = null
    var adapter: CountryListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvCountryList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = CountryListAdapter(applicationContext, getAllCountries(), object : OnCountryClickListener {
            override fun locationPosition(countryName: String, countryPosition: Int) {
                val intent = Intent(this@CountriesActivity, CitiesActivity::class.java)
                intent.putExtra("countryPosition", countryPosition)
                intent.putExtra("countryName", countryName)
                startActivity(intent)
            }
        })
        binding.rvCountryList.setItemViewCacheSize(getAllCountries().size)

        binding.rvCountryList.adapter = adapter

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter!!.getFilter()!!.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter!!.filter!!.filter(newText)
                return false
            }
        })

        binding.imgCountriesBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        handleBackPress()
    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }

        })
    }

    private fun getAllCountries(): ArrayList<String> {
        countryList.clear()
        try {
            val values = assets.list("countries")!!
            countries = arrayListOf()

            for (i in values.indices) {

                val index = values[i].indexOf(".")
                val countryName = values[i].subSequence(0, index)
                // countries[i] = values[i].toString()
                val ourCountry = values[i]
                for (str in ourCountry) {
                    country += str
                }
                countryList.add(countryName as String)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return countryList
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}