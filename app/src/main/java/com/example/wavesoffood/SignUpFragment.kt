package com.example.wavesoffood

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.wavesoffood.Models.AuthRepository
import com.example.wavesoffood.Models.AuthViewModel
import com.example.wavesoffood.Models.AuthViewModelFactory
import com.example.wavesoffood.Models.SharedManager
import com.example.wavesoffood.databinding.FragmentSignUpBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var manager: SharedManager
    private lateinit var googleSignInClient: GoogleSignInClient

    private var callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val facebookLoginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            callbackManager.onActivityResult(result.resultCode, result.resultCode, result.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)

        val repository = AuthRepository()
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        googleSignInClient = viewModel.configuregooglesignin(requireActivity())

        return binding.root
    }

    @Deprecated("Deprecated in Java")
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

        dynmicHashKey()


        manager = SharedManager(requireContext())
        viewModel.setUserLocation(
            manager.getCountry().toString(),
            manager.getStates().toString(),
            manager.getCity().toString()
        )





        binding.gotologinpage.setOnClickListener(View.OnClickListener {
            dynmicHashKey()

            findNavController().navigate(
                com.example.wavesoffood.R.id.loginFragment,
                null,
                navOptions {
                    popUpTo(com.example.wavesoffood.R.id.signUpFragment) { inclusive = true }
                }
            )

        })
        binding.googlebtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)

        }
        binding.signupbtn.setOnClickListener(View.OnClickListener {
            registerUser()
        })

        viewModel.crednetialresult.observe(viewLifecycleOwner) { result ->
            if (result.first) {
                Toast.makeText(context, "login Successful ", Toast.LENGTH_SHORT).show()

                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(context, "login failed {${result.second}", Toast.LENGTH_SHORT)
                    .show()

            }

        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->

            if (result.first) {

                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()

                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                binding.email.error = result.second.toString()
            }


        }


    }

    private fun dynmicHashKey() {
        try {
            val info = requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    fun registerUser() {
        val name = binding.name.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()


        if (name.isEmpty()) {
            binding.name.error = "Name is required!"
            return@registerUser
        }
        if (email.isEmpty()) {
            binding.email.error = "Email is required!"
            return@registerUser
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Invalid Email Format!"
            return@registerUser
        }
        if (password.isEmpty()) {
            binding.password.error = "Password is required!"
            return@registerUser
        }
        if (password.length < 6) {
            binding.password.error = "Password must be at least 6 characters!"
            return@registerUser
        }

        viewModel.registerUser(name, email, password)


    }
}