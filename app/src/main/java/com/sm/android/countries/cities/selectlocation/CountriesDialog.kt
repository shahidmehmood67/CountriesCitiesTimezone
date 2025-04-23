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
import com.sm.android.countries.cities.countries.model.Country
import com.sm.android.countries.cities.databinding.DialogSelectCountriesBinding

class CountriesDialog : DialogFragment() {

    private lateinit var adapter: CountryAdapter

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
            adapter = CountryAdapter { selected ->
                listener?.itemClicked(selected)
                dismiss()
            }

            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@CountriesDialog.adapter
            }

            viewModel.countries.observe(viewLifecycleOwner) { data->
                if (data.isNotEmpty()){
                    adapter.submitList(data)
                }
                progressBar.visibility = View.GONE
            }
        }
    }


}
