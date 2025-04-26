package com.sm.android.countries.cities.selectlocation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sm.android.countries.cities.countries.model.Country
import com.sm.android.countries.cities.databinding.ActivitySelectLocationCityBinding

class SelectLocationCityActivity : AppCompatActivity() {

    private val binding: ActivitySelectLocationCityBinding by lazy { ActivitySelectLocationCityBinding.inflate(layoutInflater) }
    private val vmCities: CitiesNewViewModel by viewModels()

    lateinit var cityAdapter : CityAdapterSection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        setRCV()
        observeData()

        binding.apply {
            btnChangeCountry.setOnClickListener {
                val dialog = CountriesDialog()
                dialog.setOnDialogDismissListener(object : DialogItemCallback {
                    override fun itemClicked(item: Country) {
                    // Use the selected country
                    Toast.makeText(this@SelectLocationCityActivity, "Selected: ${item.name}", Toast.LENGTH_SHORT).show()
                    vmCities.setMatchedCountryData(item) // example action
                    }
                })
                dialog.show(supportFragmentManager, "CountriesDialog")
            }

            searchView.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    vmCities.setCitySearchQuery(query?.toString() ?: "")
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    vmCities.setCitySearchQuery(newText?.toString() ?: "")
                    return false
                }
            })

        }
    }
    fun setRCV() {
        cityAdapter = CityAdapterSection { city ->
            Toast.makeText(this, "Clicked: ${city.name}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SelectLocationCityActivity)
            adapter = cityAdapter
            addItemDecoration(StickyHeaderItemDecoration(
                isHeader = { pos -> cityAdapter.currentList[pos] is ListItem.Header },
                getHeaderText = { pos ->
                    (cityAdapter.currentList[pos] as? ListItem.Header)?.title ?: ""
                }
            ))
        }
    }

    fun observeData() {
        vmCities.matchedCitiesHeader.observe(this) { cities ->
            if (cities.isNotEmpty()){
                cityAdapter.submitList(cities)
            }
                binding.progressBar.visibility = View.GONE
        }
    }
}

