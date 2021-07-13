package com.project.design.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.design.data.model.User
import com.project.design.data.repository.SplashRepository
import com.project.design.utils.ResponseState


class SplashViewModel @ViewModelInject constructor(val authRepository: SplashRepository) :
    ViewModel() {


    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User?>> = MutableLiveData()
    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()


    val authenticatedUserLiveData: LiveData<ResponseState<User?>> get() = _authenticateUserLiveData
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData
    fun checkIfUserIsAuthenticated() {

        _authenticateUserLiveData = authRepository.checkIfUserIsAuthenticatedInFirebase()
    }

    fun setUid(uid: String) {
        _userLiveData = authRepository.addUserToLiveData(uid)
    }

}