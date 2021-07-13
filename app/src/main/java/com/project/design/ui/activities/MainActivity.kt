package com.project.design.ui.activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI

import com.google.firebase.auth.FirebaseAuth
import com.thecode.aestheticdialogs.*
import com.project.design.R
import com.project.design.data.model.OrderModel
import com.project.design.databinding.MainActivityBinding
import com.project.design.utils.*
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.SmoothBottomBar.Companion.d2p
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding: MainActivityBinding


    @Inject
    lateinit var networkConnection: NetworkConnection

    @Inject
    lateinit var networkHelper: NetworkHelper

    lateinit var dialog: AestheticDialog.Builder

    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    @Inject
    lateinit var sharedPrefrences: MySharedPrefrences

    val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private val permList: Array<String> = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    lateinit var firebaseAuth: FirebaseAuth


    companion object {
        private const val PERMISSION_CODE = 121
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkConnection.observe(this) {
            if (it) {

                hideNetworkDialog()


            } else {

                showNetworkDialog()


            }

        }



        if (!checkForPermission(READ_EXTERNAL_STORAGE)) {
            askPermission(permList)
        }
        if (!checkForPermission(WRITE_EXTERNAL_STORAGE)) {
            askPermission(permList)
        }


        firebaseAuth = FirebaseAuth.getInstance()

        observeData()

    }

    private fun observeData() {
        mainViewModel.createDepositLiveData.observe(this, { dt ->
            when (dt) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    dt.data?.let { binding.root.snackbar(it) }

                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    dt.message?.let { binding.root.snackbar(it) }

                }
                is ResponseState.Loading -> {
                }

            }

        })
    }


    override fun onStart() {
        super.onStart()
        setFullScreenForNotch()
        setFullScreenWithBtmNav()

    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }


    private fun checkForPermission(permission: String): Boolean {

        return ActivityCompat.checkSelfPermission(
            this@MainActivity,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()) {
            val bool = grantResults[0] == PackageManager.PERMISSION_GRANTED
            onPermissionResult(bool)
            val bool1 = grantResults[1] == PackageManager.PERMISSION_GRANTED
            onPermissionResult(bool1)
        }

    }

    private fun onPermissionResult(granted: Boolean) {
        if (!granted) {
            askPermission(permList)
        }
    }

    private fun askPermission(permList: Array<String>) {
        ActivityCompat.requestPermissions(this@MainActivity, permList, PERMISSION_CODE)
    }


    private fun hideNetworkDialog() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
    }

    private fun showNetworkDialog() {
        dialog = AestheticDialog.Builder(this, DialogStyle.EMOTION, DialogType.ERROR)
            .setTitle("No internet")
            .setMessage("Please check your internet connection")
            .setCancelable(false)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            })
        dialog.show()

    }


    var mOrderAmount: String = ""
    lateinit var deposit: OrderModel
    var productId: String = ""

    fun startPayment(orderAmount: String, mproductId: String) {


        mOrderAmount = orderAmount
        productId = mproductId
        val checkOut = Checkout()
        try {

            val jsonObj = JSONObject()
            jsonObj.put("name", "Design Project")
            jsonObj.put("description", "Buy product")
            jsonObj.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png")
            jsonObj.put("currency", "INR")

            val total = orderAmount.toInt() * 100
            jsonObj.put("amount", total)

            val prefill = JSONObject()
            prefill.put("email", "")
            prefill.put("contact", "")

            jsonObj.put("prefill", prefill)


            checkOut.open(this, jsonObj)


        } catch (e: Exception) {

            toast("Error in payment ${e.message}")

        }

    }


    override fun onPaymentSuccess(paymentID: String?, p1: PaymentData?) {


        try {

            // add coins to users
            // add data to deposit collection
            deposit = OrderModel()
            deposit.date = getTodaysDate()
            deposit.time = getCurrentTime()
            deposit.id = p1?.paymentId.toString()
            deposit.paidAmount = mOrderAmount
            deposit.productId = productId
            deposit.userId = firebaseAuth.currentUser.uid
            deposit.productId = productId


            binding.progressBar.visible()
            mainViewModel.createDeposit(deposit)

        } catch (e: Exception) {


            Log.d("RRR", "onPaymentSuccess:${e.message} ")
        }

        toast("Payment done successfully")
    }

    override fun onPaymentError(code: Int, response: String?, p2: PaymentData?) {

        when (code) {
            Checkout.NETWORK_ERROR -> {
                binding.root.snackbar("Payment failed due to a network error.")
            }
            Checkout.INVALID_OPTIONS -> {
                binding.root.snackbar("Payment failed ")
            }
            Checkout.PAYMENT_CANCELED -> {
                binding.root.snackbar("Payment cancelled.")

            }
            Checkout.TLS_ERROR -> {
                binding.root.snackbar("The device does not support TLS v1.1 or TLS v1.2.")
            }

        }

    }


}