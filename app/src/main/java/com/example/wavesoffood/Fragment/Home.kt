package com.example.wavesoffood.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.wavesoffood.Adapter.BottomPopularMenuAdapter
import com.example.wavesoffood.MainRepository
import com.example.wavesoffood.MainViewModel
import com.example.wavesoffood.MainViewModelFactory
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.MyApplication
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {

    lateinit var binding: FragmentHomeBinding
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
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner_06))
        imageList.add(SlideModel(R.drawable.banner1))
        imageList.add(SlideModel(R.drawable.banner_05))
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)

        var adapter = BottomPopularMenuAdapter(emptyList<RetriveAddedItem>(), requireContext())
        binding.recycleview.adapter = adapter
        binding.recycleview.layoutManager = LinearLayoutManager(context)
        binding.recycleview.setHasFixedSize(true)

        viewModel.bottommenulist.observe(viewLifecycleOwner) { it: List<RetriveAddedItem>? ->

            it?.let { list ->
                val shuffledList = list.shuffled().take(10) // 🔹 Shuffle the list
                adapter.updateList(shuffledList) // 🔹 Update adapter with shuffled list
                Log.d("observe", shuffledList.toString())
                }
        }

        binding.viewmenu.setOnClickListener(View.OnClickListener {
          var bottomSheetDialogFragment=BottomMenuF()
            bottomSheetDialogFragment.show(parentFragmentManager,"test")
        })


    }
}