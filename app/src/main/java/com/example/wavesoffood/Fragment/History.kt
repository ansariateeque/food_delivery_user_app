package com.example.wavesoffood.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapter.HistoryAdapter
import com.example.wavesoffood.MainViewModel
import com.example.wavesoffood.MyApplication
import com.example.wavesoffood.Orders
import com.example.wavesoffood.databinding.FragmentHistoryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [History.newInstance] factory method to
 * create an instance of this fragment.
 */
class History : Fragment() {
    lateinit var binding: FragmentHistoryBinding
    lateinit var arrayList: ArrayList<Orders>
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        viewModel =
            ViewModelProvider(
                requireActivity(),
                (requireActivity().application as MyApplication).viewModelFactory // ✅ Get Factory from Application class
            )[MainViewModel::class.java]

        viewModel.history()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var adapter = HistoryAdapter(emptyList<Orders>(), viewModel)
        binding.recycle.layoutManager = LinearLayoutManager(context)
        binding.recycle.adapter = adapter

        viewModel.historyList.observe(viewLifecycleOwner) {

            it?.let { list ->
                if (list.isNullOrEmpty()) {
                    binding.emptyCartText.visibility = View.VISIBLE
                    binding.recycle.visibility = View.GONE
                } else {
                    binding.emptyCartText.visibility = View.GONE
                    binding.recycle.visibility = View.VISIBLE
                    adapter.update(list)
                }
            }
        }

    }
}