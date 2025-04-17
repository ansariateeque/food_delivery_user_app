package com.example.wavesoffood

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import retrofit2.Response

import com.example.wavesoffood.Fragment.CongratsBottomF
import com.example.wavesoffood.Models.Users
import com.example.wavesoffood.databinding.ActivityPayOutOrderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.apache.http.HttpException

class PayOutOrder : AppCompatActivity() {
    lateinit var binding: ActivityPayOutOrderBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var users: Users
    private lateinit var dialog: AlertDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutOrderBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)



        viewModel =
            ViewModelProvider(
                this,
                (application as MyApplication).viewModelFactory // ✅ Get Factory from Application class
            )[MainViewModel::class.java]

        viewModel.calculateTotalPrice()

        lifecycleScope.launch {
            viewModel.getuserDetail()

        }



        binding.placeorderbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                if (checkformality()) {


                    lifecycleScope.launch {
                        val hotelname = viewModel.gethotelname()
                        val hoteluserid = viewModel.gethotelUserId()
                        Log.e("userId", hoteluserid.toString())

                        val userFcmToken = viewModel.gethotelownertoken(hoteluserid)
                        val accessToken = FirebaseAuthHelper.getAccessToken(this@PayOutOrder)


                        if (accessToken != null) {
                            sendNotification(
                                accessToken,
                                userFcmToken,
                                "New Order",
                                "You got a new order in your restaurant!"
                            )
                        } else {
                            Log.e("FCM", "Access token is null")
                        }


                        if (hotelname != null) {
                            viewModel.placedOrder(hotelname)
                        }
                    }
                    var congratsBottomF = CongratsBottomF()
                    congratsBottomF.show(supportFragmentManager, "hi")

                } else {
                    Toast.makeText(
                        this@PayOutOrder,
                        "Please save your details first!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        })

        binding.imageView5.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        binding.text.setOnClickListener(View.OnClickListener {
            editable(true)
        })
        binding.clicktoedit.setOnClickListener {
            editable(true)
        }


        binding.save.setOnClickListener(View.OnClickListener {


            if (checkformality()) {

                viewModel.updateUserValue(
                    binding.name.text.toString(),
                    binding.address.text.toString(),
                    binding.email.text.toString(),
                    binding.phone.text.toString()
                )
            }


        })


        observe()


        viewModel.getuserDetails.observe(this) { it: Users? ->


            binding.name.setText(it?.name)
            binding.address.setText(it?.address)
            binding.email.setText(it?.email)
            binding.phone.setText(it?.phone)


        }

    }

    private fun observe() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveUserDetails.collect {
                    if (it.first) {
                        editable(false)
                        Toast.makeText(this@PayOutOrder, "${it.second}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PayOutOrder, "${it.second}", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }


        viewModel.totalPriceLiveData.observe(this) {

            val formattedPrice =
                if (it.rem(1) == 0.0) {
                    it.toInt().toString()  // Convert to Int if no decimals
                } else {
                    it.toString()  // Keep original if has decimals
                }
            binding.totalAmount.text = formattedPrice
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loading.collect { isLoading ->
                    if (isLoading) {
                        showLottieDialog()
                    } else {
                        dialog.dismiss()
                    }
                }

            }
        }

    }

    fun showLottieDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialgoue_layout, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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
        if (b) {
            binding.placeorderbtn.visibility = View.GONE
        } else {
            binding.placeorderbtn.visibility = View.VISIBLE

        }


    }


    private fun sendNotification(token: String, fcmToken: String, title: String, message: String) {
        val notificationData = NotificationData(title, message)
        val messageData = MessageData(fcmToken, notificationData)
        val notificationRequest = NotificationRequest(messageData)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<ResponseBody> =
                    RetrofitClient.fcmService.sendNotification("Bearer $token", notificationRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(
                            "FCM",
                            "✅ Notification Sent Successfully: ${response.body()?.string()}"
                        )
                    } else {
                        Log.e(
                            "FCM",
                            "❌ Error Sending Notification: ${response.errorBody()?.string()}"
                        )
                    }
                }
            } catch (e: HttpException) {
                Log.e("FCM", "❌ HTTP Exception: ${e.message}")
            } catch (e: Exception) {
                Log.e("FCM", "❌ Failed to Send Notification", e)
            }
        }

    }
}

