package com.sm.android.countries.cities.selectlocation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sm.android.countries.cities.countries.model.City
import com.sm.android.countries.cities.databinding.ItemCityBinding

class CityAdapter(
    private val onCityClick: (City) -> Unit
) : ListAdapter<City, CityAdapter.CityViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<City>() {
            override fun areItemsTheSame(oldItem: City, newItem: City) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: City, newItem: City) = oldItem == newItem
        }
    }

    inner class CityViewHolder(
        private val binding: ItemCityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(city: City) {
            binding.cityName.text = city.name
            binding.root.setOnClickListener {
                onCityClick(city)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
