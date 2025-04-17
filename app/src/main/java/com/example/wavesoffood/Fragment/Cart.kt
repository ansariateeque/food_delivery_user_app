package com.example.wavesoffood.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapter.BottomPopularMenuAdapter
import com.example.wavesoffood.Adapter.CartAdapter
import com.example.wavesoffood.MainRepository
import com.example.wavesoffood.MainViewModel
import com.example.wavesoffood.MainViewModelFactory
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.MyApplication
import com.example.wavesoffood.PayOutOrder
import com.example.wavesoffood.R
import com.example.wavesoffood.RetrieveAddToCart
import com.example.wavesoffood.databinding.CartlayoutBinding
import com.example.wavesoffood.databinding.FragmentCartBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Cart.newInstance] factory method to
 * create an instance of this fragment.
 */
class Cart : Fragment() {

    lateinit var binding: FragmentCartBinding
    lateinit var viewModel: MainViewModel

    private val payoutActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val navigateToHome = result.data?.getBooleanExtra("NAVIGATE_TO_HOME", false) ?: false
            if (navigateToHome) {
                val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomnavi)
                bottomNavigationView.selectedItemId = R.id.home2  // ✅ HomeFragment open karega
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel =
            ViewModelProvider(
                requireActivity(),
                (requireActivity().application as MyApplication).viewModelFactory // ✅ Get Factory from Application class
            )[MainViewModel::class.java]
        viewModel.fetchaddtocart()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        var adapter = CartAdapter(mutableListOf(), requireContext(), viewModel) // ✅ Corrected
        binding.recycleview.adapter = adapter
        binding.recycleview.layoutManager = LinearLayoutManager(context)
        binding.recycleview.setHasFixedSize(true)


        viewModel.addtocartlist.observe(viewLifecycleOwner) { it: List<RetrieveAddToCart>? ->

            it?.let { list ->
                if (list.isNullOrEmpty()) {
                    binding.emptyCartText.visibility = View.VISIBLE
                    binding.recycleview.visibility = View.GONE
                    binding.orderingfrom.visibility=View.GONE
                }else {
                    binding.emptyCartText.visibility = View.GONE
                    binding.recycleview.visibility = View.VISIBLE
                    binding.orderingfrom.visibility=View.VISIBLE


                    val hotelName = list[0].nameofrestuarant
                    binding.hotelNameTextView.text=hotelName
                    adapter.updateList(list)
                }
            }
        }

        binding.proceed.setOnClickListener(View.OnClickListener
        {

            val cartItems = viewModel.addtocartlist.value
            if(!cartItems.isNullOrEmpty()){


                val intent = Intent(requireContext(), PayOutOrder::class.java)
                payoutActivityResult.launch(intent)
            }else {
                Toast.makeText(context, "Cart is empty. Please add items!", Toast.LENGTH_SHORT).show()
            }
        })

    }
}