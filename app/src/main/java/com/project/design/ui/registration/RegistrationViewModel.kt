package com.project.design.ui.registration

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.design.data.model.User
import com.project.design.data.repository.AuthRepository
import com.project.design.utils.ResponseState


class RegistrationViewModel @ViewModelInject constructor(val authRepository: AuthRepository) :
    ViewModel() {

    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    private var _createdUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()

    val createdUserLiveData: LiveData<ResponseState<User>> get() = _createdUserLiveData
    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData


    fun registerUserWithEmailPass(username:String,email: String, pass: String,mobile:String) {
        _authenticateUserLiveData = authRepository.firebaseRegisterUserWithEmailPass(username,email, pass,mobile)
    }


    fun createUser(authenticatedUser: User) {
        _createdUserLiveData = authRepository.createUserInFireStoreIfNotExist(authenticatedUser)
    }


}