package com.example.wavesoffood.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapter.BottomPopularMenuAdapter
import com.example.wavesoffood.MainRepository
import com.example.wavesoffood.MainViewModel
import com.example.wavesoffood.MainViewModelFactory
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.MyApplication
import com.example.wavesoffood.databinding.FragmentBottomMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomMenuF : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomMenuBinding
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel =
            ViewModelProvider(
                requireActivity(),
                (requireActivity().application as MyApplication).viewModelFactory // ✅ Get Factory from Application class
            )[MainViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentBottomMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener(View.OnClickListener {
            dismiss()
        })
        var adapter = BottomPopularMenuAdapter(emptyList<RetriveAddedItem>(), requireContext())
        binding.recycleview.adapter = adapter
        binding.recycleview.layoutManager = LinearLayoutManager(context)
        binding.recycleview.setHasFixedSize(true)

        viewModel.bottommenulist.observe(viewLifecycleOwner) { it: List<RetriveAddedItem>? ->

            it?.let { it1 -> adapter.updateList(it1) }
            Log.d("observe", it.toString())
        }


    }
}