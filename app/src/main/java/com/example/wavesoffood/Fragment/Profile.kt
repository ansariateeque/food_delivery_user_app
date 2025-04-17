package com.example.wavesoffood.Fragment

import android.app.AlertDialog
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.wavesoffood.MainViewModel
import com.example.wavesoffood.Models.Users
import com.example.wavesoffood.MyApplication
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var binding: FragmentProfileBinding
    lateinit var viewModel: MainViewModel
    lateinit var dialog: AlertDialog
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
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getuserDetail()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loading.collect { isLoading ->
                    if (isLoading) {
                        showLottieDialog()
                    } else {
                        if (::dialog.isInitialized && dialog.isShowing) {
                            dialog.dismiss()

                        }

                    }
                }

            }
        }

        binding.text.setOnClickListener(View.OnClickListener {
            editable(true)

        })
        binding.clicktoedit.setOnClickListener {
            editable(true)

        }

        viewModel.getuserDetails.observe(viewLifecycleOwner) { it: Users? ->
            it?.let {

                binding.name.setText(it.name)
                binding.address.setText(it.address)
                binding.email.setText(it.email)
                binding.phone.setText(it.phone)

            }


        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveUserDetails.collect {
                    if (it.first) {
                        editable(false)
                        Toast.makeText(requireContext(), "${it.second}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "${it.second}", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }

        binding.saveinformation.setOnClickListener {

            if (checkformality()) {
                viewModel.updateUserValue(
                    binding.name.text.toString(),
                    binding.address.text.toString(),
                    binding.email.text.toString(),
                    binding.phone.text.toString()
                )
            }
        }
        lifecycleScope.launch {
            viewModel.logoutState.collect {
                if (it) {
                    requireActivity().finish()
                }
            }

        }
        binding.loguotbtn.setOnClickListener {
            viewModel.logout(requireContext())
        }

    }

    private fun checkformality(): Boolean {


        if (binding.name.text.toString().isEmpty()) {
            binding.name.error = "please fill the name"
            return false
        }

        if (binding.address.text.toString().isEmpty()) {
            binding.address.error = "please fill the name"
            return false

        }

        if (binding.email.text.toString().isEmpty()) {
            binding.email.error = "please fill the email"
            return false

        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString())
                .matches()
        ) {
            binding.email.error = "Invalid Email Format"
            return false
        }

        if (binding.phone.text.toString().isEmpty()) {
            binding.phone.error = "please fill the phone number"
            return false

        }

        if (binding.phone.text.toString().trim().length != 10 ||
            !binding.phone.text.toString().trim().all { it.isDigit() }
        ) {
            binding.phone.error = "Invalid Number Format"
            return false

        } else {
            binding.phone.error = null // ✅ Sahi hone par error hatao

        }

        return true

    }

    private fun editable(b: Boolean) {
        binding.name.isEnabled = b
        binding.name.requestFocus()
        binding.address.isEnabled = b
        binding.email.isEnabled = b
        binding.phone.isEnabled = b
        binding.saveinformation.isEnabled = b
    }

    fun showLottieDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialgoue_layout, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

}