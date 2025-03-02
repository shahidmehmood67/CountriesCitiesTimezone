package com.sm.android.countries.cities.countries

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.sm.android.countries.cities.databinding.ItemCountryListBinding

class CountryListAdapter(
    private val context: Context,
    private val list: ArrayList<String>,
    var mListner: OnCountryClickListener
) :
    RecyclerView.Adapter<CountryListAdapter.ViewHolder>() , Filterable {
    private var mDataFiltered: ArrayList<String> = list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemCountryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mPosition = position
        holder.binding.countryName.text= mDataFiltered[position]

        holder.itemView.setOnClickListener{
            mListner.locationPosition(mDataFiltered[position], mPosition)
        }
    }

    inner class ViewHolder(val binding: ItemCountryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                //(context as CountriesActivity).onClickItem(adapterPosition)
            }
        }
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mDataFiltered = results.values as ArrayList<String>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var filteredResults: ArrayList<String>? = null
                if (constraint.length == 0) {
                    filteredResults = list
                } else {
                    filteredResults = getFilteredResults(constraint.toString().lowercase())
                }
                val results = FilterResults()
                results.values = filteredResults
                results.count = filteredResults!!.size
                return results
            }
        }
    }

    protected fun getFilteredResults(constraint: String?): ArrayList<String> {
        val results: ArrayList<String> = ArrayList()
        for (item in list) {
            if (item != null && item.trim().lowercase()
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
