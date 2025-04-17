package com.example.wavesoffood

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.wavesoffood.Models.AuthRepository
import com.example.wavesoffood.Models.AuthViewModel
import com.example.wavesoffood.Models.AuthViewModelFactory
import com.example.wavesoffood.Models.SharedManager
import com.example.wavesoffood.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var manager: SharedManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)


        val repository = AuthRepository()
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        manager = SharedManager(requireContext())
        viewModel.setUserLocation(
            manager.getCountry().toString(),
            manager.getStates().toString(),
            manager.getCity().toString()
        )
        googleSignInClient = viewModel.configuregooglesignin(requireActivity())


        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)  // ✅ Proper error handling

                viewModel.handleGoogleSignInResult(account)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Google Sign-In failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLottieDialog()
            } else {
                if (::dialog.isInitialized && dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }

        binding.loginbtn.setOnClickListener(View.OnClickListener {
            userlogin()
        })
        binding.googlebtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)

        }
        binding.gotosignuppage.setOnClickListener(View.OnClickListener {
            findNavController().navigate(
                com.example.wavesoffood.R.id.signUpFragment,
                null,
                navOptions {
                    popUpTo(com.example.wavesoffood.R.id.loginFragment) { inclusive = true }
                }
            )

        })

        loginResultObserve()
        credentialLogin()


    }

    private fun credentialLogin() {
        viewModel.crednetialresult.observe(viewLifecycleOwner) { result ->
            if (result.first) {
                Toast.makeText(context, "login Successful ", Toast.LENGTH_SHORT).show()

                if (::dialog.isInitialized && dialog.isShowing) {
                    dialog.dismiss()
                }

                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(context, "Google login failed {${result.second}", Toast.LENGTH_SHORT)
                    .show()

            }

        }
    }

    private fun loginResultObserve() {
        viewModel.loginresult.observe(viewLifecycleOwner) { result ->

            if (result.first) {

                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()

                if (::dialog.isInitialized && dialog.isShowing) {
                    dialog.dismiss()
                }
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                binding.email.error = result.second.toString()
            }


        }
    }

    private fun userlogin() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty()) {
            binding.email.error = "Email is required!"
            return@userlogin
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Invalid Email Format!"
            return@userlogin
        }
        if (password.isEmpty()) {
            binding.password.error = "Password is required!"
            return@userlogin
        }
        if (password.length < 6) {
            binding.password.error = "Password must be at least 6 characters!"
            return@userlogin
        }

        viewModel.loginuser(email, password)

    }


    fun showLottieDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialgoue_layout, null)
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }

}