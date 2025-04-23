package com.sm.android.countries.cities.selectlocation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sm.android.countries.cities.countries.model.Country
import com.sm.android.countries.cities.databinding.ItemCityBinding

class CountryAdapter(
    private val onClick: (Country) -> Unit
) : ListAdapter<Country, CountryAdapter.CountryViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Country>() {
            override fun areItemsTheSame(oldItem: Country, newItem: Country) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Country, newItem: Country) = oldItem == newItem
        }
    }

    inner class CountryViewHolder(val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(country: Country) {
            binding.cityName.text = country.name
            binding.root.setOnClickListener { onClick(country) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
