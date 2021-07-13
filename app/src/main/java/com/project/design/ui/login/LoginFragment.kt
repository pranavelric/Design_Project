package com.project.design.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.project.design.data.model.User
import com.project.design.ui.activities.MainActivity
import com.project.design.utils.Constants.USERS_BUNDLE_OBJ
import com.project.design.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import com.project.design.R
import com.project.design.utils.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    companion object {
        const val RC_SIGN_IN = 121

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentLoginBinding.inflate(inflater, container, false)
        getData()
        setClickListeners()
        return binding.root
    }

    private fun getData() {
    }

    private fun setClickListeners() {
        binding.phoneLog.setOnClickListener {
            binding.phoneLog.gone()
            binding.emailLog.gone()
            binding.googleSignInButton.gone()

            binding.layoutPhone.root.visible()


        }
        binding.emailLog.setOnClickListener {
            binding.phoneLog.gone()
            binding.emailLog.gone()
            binding.googleSignInButton.gone()
            binding.layoutLogin.root.visible()
        }
        binding.layoutLogin.cirLoginButton.setOnClickListener {
            if (binding.layoutLogin.editTextEmail.text.isNullOrBlank()) {
                binding.layoutLogin.editTextEmail.error = "Please enter an email."
            } else if (binding.layoutLogin.editTextPassword.text.isNullOrBlank()) {
                binding.layoutLogin.editTextPassword.error = "Please enter password."
            } else {
                signInUsingEmailPassword(
                    binding.layoutLogin.editTextEmail.text.toString(),
                    binding.layoutLogin.editTextPassword.text.toString()
                )
            }

        }

        binding.layoutPhone.cirPhoneLoginButton.setOnClickListener {
            if (binding.layoutPhone.editTextPhone.text.isNullOrBlank()) {
                binding.layoutPhone.editTextPhone.error = "Please enter your phone number"
            } else {
                signInUsingPhoneNumber(binding.layoutPhone.editTextPhone.text.toString())
                binding.layoutPhone.root.gone()
                binding.layoutOtp.root.visible()
            }
        }

//        binding.googleSignInButton.setOnClickListener {
//            signInUsingGoolge()
//        }
        binding.layoutLogin.textSignup.setOnClickListener {
            goToRegister()
        }
        binding.layoutPhone.textSignupPhone.setOnClickListener {
            goToRegister()
        }

    }


    private fun signInUsingPhoneNumber(phoneNumber: String) {

        binding.layoutOtp.phonenumberText.text = "+91-${phoneNumber}"

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    context?.toast_long("Invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    context?.toast_long("SMS quota for project has been exceeded")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                // Save verification ID and resending token so we can use them later
                val storedVerificationId = verificationId
                val resendToken = token
                binding.layoutOtp.continueOtp.setOnClickListener {
                    val code = binding.layoutOtp.pinView.text.toString()
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
        val auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity as MainActivity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        binding.progressBar.visible()
        loginViewModel.signInWithPhoneNumber(credential)
        loginViewModel.authenticateUserLiveData.observe(viewLifecycleOwner, { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    authenticatedUser.message?.let { context?.toast(it) }
                }
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    if (authenticatedUser.data != null)
                        if (authenticatedUser.data.isNew == true) {
                            createNewUser(authenticatedUser.data)
                        } else {
                            goToMainFragment(authenticatedUser.data)
                        }
                }
                is ResponseState.Loading -> {
                }
            }
        })

    }


    private fun signInUsingEmailPassword(email: String, password: String) {
        binding.progressBar.visible()
        loginViewModel.signInWithEmailPass(email, password)
        loginViewModel.authenticateUserLiveData.observe(viewLifecycleOwner, { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    authenticatedUser.message?.let { context?.toast(it) }
                }
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    if (authenticatedUser.data != null)
                        if (authenticatedUser.data.isNew == true) {
                            createNewUser(authenticatedUser.data)
                        } else {
                            goToMainFragment(authenticatedUser.data)
                        }
                }
                is ResponseState.Loading -> {
                }
            }
        })
    }






    private fun createNewUser(authenticatedUser: User?) {
        binding.progressBar.visible()
        if (authenticatedUser != null) {
            loginViewModel.createUser(authenticatedUser)
        } else {
            binding.progressBar.gone()
            context?.toast("Some error occured")
        }
        loginViewModel.createdUserLiveData.observe(viewLifecycleOwner, { user ->

            when (user) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    if (user.data != null) {
                        if (user.data.isCreated) {
                            context?.toast("${user.data.name}")
                        }
                        goToMainFragment(user.data)
                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    user.message?.let { context?.toast(it) }

                }
                is ResponseState.Loading -> {

                }
            }


        })
    }


    private fun goToMainFragment(authenticatedUser: User?) {

        binding.progressBar.visible()
        authenticatedUser?.uid?.let { loginViewModel.getUser(it) }
        loginViewModel.getUserLiveData.observe(viewLifecycleOwner, { userTask ->
            when (userTask) {
                is ResponseState.Success -> {

                    binding.progressBar.gone()
                    val bundle = Bundle().apply {
                        putSerializable(USERS_BUNDLE_OBJ, userTask.data!!)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment, bundle)

                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    context?.toast("Some Error occured")
                }
                is ResponseState.Loading -> {

                }
            }

        })


    }


    private fun goToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }


}