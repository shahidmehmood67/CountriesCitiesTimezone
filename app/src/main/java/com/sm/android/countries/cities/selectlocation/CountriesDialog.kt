package com.sm.android.countries.cities.selectlocation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sm.android.countries.cities.R
import com.sm.android.countries.cities.databinding.DialogSelectCountriesBinding
import com.sm.android.countries.cities.selectlocation.models.CountryAdapterSection
import com.sm.android.countries.cities.selectlocation.models.ListItemCountry

class CountriesDialog : DialogFragment() {

    private lateinit var countryAdapter: CountryAdapterSection

    private val dialogLayoutBinding by lazy { DialogSelectCountriesBinding.inflate(layoutInflater) }

    private val viewModel: CitiesNewViewModel by activityViewModels()


    private var listener: DialogItemCallback? = null

    fun setOnDialogDismissListener(listener: DialogItemCallback) {
        this.listener = listener
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Transparent background
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // Keep status bar visible
//            statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark) // Status bar color
//            attributes.windowAnimations = R.style.DialogAnimation // Smooth animation
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NO_TITLE, R.style.FullscreenDialogTheme)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return dialogLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init(){
        with(dialogLayoutBinding){

            searchView.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.setCountrySearchQuery(query?.toString() ?: "")
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.setCountrySearchQuery(newText?.toString() ?: "")
                    return false
                }
            })

            countryAdapter = CountryAdapterSection { selected ->
                searchView.setQuery("", false)  // Clear text
                searchView.clearFocus()         // Remove keyboard and focus
                searchView.isIconified = true   // Collapse the search back (optional)
                listener?.itemClicked(selected)
                dismiss()
            }

            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = countryAdapter
            }

            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = countryAdapter
                addItemDecoration(StickyHeaderItemDecoration(
                    isHeader = { pos -> countryAdapter.currentList[pos] is ListItemCountry.Header },
                    getHeaderText = { pos ->
                        (countryAdapter.currentList[pos] as? ListItemCountry.Header)?.title ?: ""
                    }
                ))
            }

            viewModel.headercountries.observe(viewLifecycleOwner) { data ->
                if (data.isNotEmpty()){
                    countryAdapter.submitList(data)
                }
                progressBar.visibility = View.GONE
            }
        }
    }


}
