package com.project.design.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.project.design.data.model.User
import com.project.design.data.repository.AuthRepository
import com.project.design.utils.ResponseState


class LoginViewModel @ViewModelInject constructor(val authRepository: AuthRepository) :
    ViewModel() {

    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    private var _createdUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()

    val createdUserLiveData: LiveData<ResponseState<User>> get() = _createdUserLiveData
    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData

    private var _getUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val getUserLiveData: LiveData<ResponseState<User>> get() = _getUserLiveData



    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        _authenticateUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun signInWithEmailPass(email: String, pass: String) {
        _authenticateUserLiveData = authRepository.firebaseSignInWithEmailPass(email, pass)
    }

    fun signInWithPhoneNumber(credential: PhoneAuthCredential) {
        _authenticateUserLiveData = authRepository.firebaseSignInWithPhone(credential)
    }

    fun createUser(authenticatedUser: User) {
        _createdUserLiveData = authRepository.createUserInFireStoreIfNotExist(authenticatedUser)
    }

    fun getUser(uid: String) {
        _getUserLiveData = authRepository.getUser(uid)
    }


}