package com.sm.android.countries.cities.countries

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sm.android.countries.cities.MainActivity
import com.sm.android.countries.cities.R
import com.sm.android.countries.cities.databinding.ActivityCitiesBinding
import com.sm.android.countries.cities.utils.FileLoader
import com.sm.android.countries.cities.utils.ProgressUtil
import com.sm.android.countries.cities.utils.prefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class CitiesActivity : AppCompatActivity() {
    private val binding: ActivityCitiesBinding by lazy {
        ActivityCitiesBinding.inflate(layoutInflater)
    }

    var cityList = ArrayList<LocationDetails>()
    var city: String? = null
    lateinit var cities: Array<String>
    private var countryPosition: Int? = null
    private var cName: String = ""
    private lateinit var adapter: CityListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        intent.extras?.apply {
            cName = getString("countryName", "")
            createList(cName)
        }
        adapter = CityListAdapter(this, cityList)
        countryPosition = intent.getIntExtra("countryPosition", 0)
        binding.rvCityList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvCityList.adapter = adapter
        binding.rvCityList.setItemViewCacheSize(cityList.size)


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.getFilter()!!.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.getFilter()!!.filter(newText)
                return false
            }
        })

        binding.imgCitiesBack.setOnClickListener {
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


    private fun createList(country: String) {
        cityList.clear()

        CoroutineScope(Dispatchers.Main).launch {
            val data: List<String> = FileLoader.loadFile("countries/$country.txt", this@CitiesActivity).split("\n")
            for (aData in data) {
                val city = aData.split(",")
                cityList.add(
                    LocationDetails(
                        city[2].trim().toDouble(),
                        city[3].trim().toDouble(),
                        city[1].trim(),
                        city[0].trim()
                    )
                )
            }
            withContext(Dispatchers.Main) {
                if (cityList.size > 0) {
                    adapter.notifyItemRangeChanged(0, cityList.size - 1)
                }
            }
        }
    }

    fun onItemClick(locationDetails: LocationDetails) {
        ProgressUtil.initDialog(
            this,
            getString(R.string.saving_location)
        )
        ProgressUtil.showLoadingDialog(true)

        prefManager.saveLocationDetails(locationDetails)

        Handler(Looper.getMainLooper()).postDelayed({
            ProgressUtil.showLoadingDialog(false)
            startActivity(Intent(applicationContext, MainActivity::class.java))

        }, 1000)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}