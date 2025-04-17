package com.example.wavesoffood

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.wavesoffood.databinding.FragmentStartFragementBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StartFragement.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartFragement : Fragment() {

    lateinit var binding: FragmentStartFragementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStartFragementBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.nextbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {


                findNavController().navigate(
                    com.example.wavesoffood.R.id.locationFragemnt,
                    null,
                    navOptions {
                        popUpTo(com.example.wavesoffood.R.id.startFragement) { inclusive = true }
                    }
                )
            }
        })


    }
}