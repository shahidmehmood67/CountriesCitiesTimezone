package com.sm.android.countries.cities.countries

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.sm.android.countries.cities.databinding.ItemCountryListBinding


class CityListAdapter(
    private val context: Context,
    private val list: List<LocationDetails>,
    private var mDataFiltered: List<LocationDetails> = list

):  RecyclerView.Adapter<CityListAdapter.ViewHolder>() , Filterable {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCountryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.countryName.text= mDataFiltered[position].city
    }


    inner class ViewHolder(val binding: ItemCountryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                (context as CitiesActivity).onItemClick(mDataFiltered[position])
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mDataFiltered = results.values as List<LocationDetails>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var filteredResults: List<LocationDetails?>? = null
                filteredResults = if (constraint.isEmpty()) {
                    list
                } else {
                    getFilteredResults(constraint.toString().lowercase())
                }
                val results = FilterResults()
                results.values = filteredResults
                results.count = filteredResults.size
                return results
            }
        }
    }

    protected fun getFilteredResults(constraint: String?): List<LocationDetails?> {
        val results: MutableList<LocationDetails?> = ArrayList()
        for (item in list) {
            if (item.city != null && item.city.trim().lowercase()
                    .contains(constraint!!.lowercase())
            ) {
                results.add(item)
            }
        }
        return results
    }

    override fun getItemCount(): Int {
        return if (mDataFiltered != null) {
            mDataFiltered.size
        } else 0
    }
}