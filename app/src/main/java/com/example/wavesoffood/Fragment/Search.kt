package com.example.wavesoffood.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapter.SearchAdapter
import com.example.wavesoffood.MainViewModel
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.Models.SearchModel
import com.example.wavesoffood.MyApplication
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentSearchBinding
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [Search.newInstance] factory method to
 * create an instance of this fragment.
 */
class Search : Fragment() {
    lateinit var list: List<RetriveAddedItem>
    lateinit var binding: FragmentSearchBinding
    lateinit var adapter: SearchAdapter
    lateinit var viewModel: MainViewModel
    var selectedFilter = "Food Name" // Default filter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(), (requireActivity().application as MyApplication).viewModelFactory
        )[MainViewModel::class.java]

        list = emptyList()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchAdapter(emptyList(), requireContext())
        binding.recycleview.adapter = adapter
        binding.recycleview.layoutManager = LinearLayoutManager(context)
        binding.recycleview.setHasFixedSize(true)

        viewModel.bottommenulist.observe(viewLifecycleOwner) {
            list = it
            adapter.filterList(it)
        }

        val spinneradapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, listOf("search by","Food Name", "Hotel Name")) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                // Hamesha ek invisible text return karenge taaki sirf icon dikhe
                val view = super.getView(position, convertView, parent)
                view.findViewById<TextView>(android.R.id.text1)?.visibility = View.GONE
                return view
            }
        }
        binding.spinnerFilter.adapter = spinneradapter



        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {

                    selectedFilter = parent?.getItemAtPosition(position).toString()
                    updateSearchHint()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    filter(p0, true)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    filter(p0, false)
                }
                return false
            }


        })


    }

    private fun updateSearchHint() {
        if (selectedFilter == "Food Name") {
            binding.searchView.queryHint = "Search by Food Name"
        } else {
            binding.searchView.queryHint = "Search by Hotel Name"
        }
    }

    fun filter(text: String, toshow: Boolean) {
        // creating a new array list to filter our data.
        val filteredlist = mutableListOf<RetriveAddedItem>()

        // running a for loop to compare elements.
        for (item in list) {
            // checking if the entered string matched with any item of our recycler view.
            if (selectedFilter == "Food Name"){
                if (!item.name.isNullOrEmpty() && item.name.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredlist.add(item)
                }
            }else{
                if (!item.nameofrestuarant.isNullOrEmpty() && item.nameofrestuarant.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredlist.add(item)
                }
            }

        }
        filterit(filteredlist, toshow)
    }

    private fun filterit(filteredlist: MutableList<RetriveAddedItem>, toshow: Boolean) {
        if (filteredlist.isEmpty() && toshow) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist)
        }

    }

}


