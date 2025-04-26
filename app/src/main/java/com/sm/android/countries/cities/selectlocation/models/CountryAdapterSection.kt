package com.sm.android.countries.cities.selectlocation.models

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sm.android.countries.cities.countries.model.City
import com.sm.android.countries.cities.countries.model.Country
import com.sm.android.countries.cities.databinding.ItemCityBinding

// 3. CityAdapter.kt
class CountryAdapterSection(
    private val onCityClick: (Country) -> Unit
) : ListAdapter<ListItemCountry, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CITY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItemCountry.Header -> VIEW_TYPE_HEADER
            is ListItemCountry.ListItem -> VIEW_TYPE_CITY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_CITY -> {
                val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CityViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItemCountry.Header -> (holder as HeaderViewHolder).bind(item)
            is ListItemCountry.ListItem -> (holder as CityViewHolder).bind(item.city)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItemCountry.Header) {
            binding.cityName.text = item.title
        }
    }

    inner class CityViewHolder(private val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: Country) {
            binding.cityName.text = city.name
            binding.root.setOnClickListener { onCityClick(city) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ListItemCountry>() {
        override fun areItemsTheSame(oldItem: ListItemCountry, newItem: ListItemCountry): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: ListItemCountry, newItem: ListItemCountry): Boolean {
            return oldItem == newItem
        }
    }
}
