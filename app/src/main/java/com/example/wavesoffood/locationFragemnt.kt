package com.example.wavesoffood

import android.R
import android.content.Intent
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.transition.Visibility
import com.example.wavesoffood.Models.LocationRepository
import com.example.wavesoffood.Models.LocationViewModel
import com.example.wavesoffood.Models.LocationViewModelFactory
import com.example.wavesoffood.Models.RerofitInstance
import com.example.wavesoffood.Models.SharedManager
import com.example.wavesoffood.databinding.FragmentLocationFragemntBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [locationFragemnt.newInstance] factory method to
 * create an instance of this fragment.
 */
class locationFragemnt : Fragment() {
    lateinit var binding: FragmentLocationFragemntBinding
    private lateinit var viewModel: LocationViewModel
    private var modifiedCountryList = mutableListOf("Select Country")
    private var modifiedStateList = mutableListOf("Select States")
    private var modifiedCityList = mutableListOf("Select City")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var retrofitinstance = RerofitInstance.getInstance()
        var repository = LocationRepository(retrofitinstance)
        var locationViewModelFactory = LocationViewModelFactory(repository)
        viewModel = ViewModelProvider(
            requireActivity(),
            factory = locationViewModelFactory
        )[LocationViewModel::class.java]
        viewModel.fetchCountries()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLocationFragemntBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.countries.observe(viewLifecycleOwner) { countryList ->
            Log.d("country", countryList.toString())

            modifiedCountryList.clear()
            modifiedCountryList.add("Select Country")

            modifiedCountryList.addAll(countryList.map { it.name })

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                modifiedCountryList
            )
            binding.country.adapter = adapter
        }

        binding.country.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (position != 0) {
                    val selectedCountry = viewModel.countries.value?.get(position - 1)
                    selectedCountry?.let { viewModel.fetchStates(it.iso2.trim()) }
                    binding.states.visibility = View.VISIBLE
                }
            }
        }

        viewModel.states.observe(viewLifecycleOwner) { stateList ->
            Log.d("country", stateList.toString())
            modifiedStateList.clear()
            modifiedStateList.add("Select States")

            modifiedStateList.addAll(stateList.map { it.name })

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                modifiedStateList
            )
            binding.states.adapter = adapter
        }

        binding.states.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (position != 0) {
                    val selectedState = viewModel.states.value?.get(position - 1)
                    val selectedCountry =
                        viewModel.countries.value?.get(binding.country.selectedItemPosition - 1)
                    if (selectedCountry != null && selectedState != null) {
                        viewModel.fetchCities(selectedCountry.iso2, selectedState.iso2)
                        binding.city.visibility = View.VISIBLE
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        viewModel.cities.observe(viewLifecycleOwner) { cityList ->
            Log.d("country", cityList.toString())

            modifiedCityList.clear()
            modifiedCityList.add("Select City")
            modifiedCityList.addAll(cityList.map { it.name })


            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                modifiedCityList
            )
            binding.city.adapter = adapter
        }


        binding.donebtn.setOnClickListener(View.OnClickListener {
            if (binding.country.selectedItemPosition == 0) {
                Toast.makeText(context, "Please select country", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (binding.states.selectedItemPosition == 0) {
                Toast.makeText(context, "Please select states", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (binding.city.selectedItemPosition == 0) {
                Toast.makeText(context, "Please select city", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            val selectedCountry =
                viewModel.countries.value?.get(binding.country.selectedItemPosition - 1)?.name ?: ""
            val selectedState =
                viewModel.states.value?.get(binding.states.selectedItemPosition - 1)?.name ?: ""
            val selectedCity =
                viewModel.cities.value?.get(binding.city.selectedItemPosition - 1)?.name ?: ""


            val manager = SharedManager(requireContext())
            manager.saveCountry(selectedCountry)
            manager.saveStates(selectedState)
            manager.saveCity(selectedCity)

            findNavController().navigate(
                com.example.wavesoffood.R.id.loginFragment,
                null,
                navOptions {
                    popUpTo(com.example.wavesoffood.R.id.locationFragemnt) { inclusive = true }
                }
            )
        })
    }
}
