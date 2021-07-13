package com.project.design.ui.activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.project.design.databinding.MainActivityBinding
import com.project.design.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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



}