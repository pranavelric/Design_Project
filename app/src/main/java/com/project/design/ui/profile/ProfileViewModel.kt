package com.project.design.ui.profile

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.design.data.model.User
import com.project.design.data.repository.ProfileRepository
import com.project.design.utils.Constants
import com.project.design.utils.ResponseState


class ProfileViewModel @ViewModelInject constructor(val profileRepository: ProfileRepository) :
    ViewModel() {

    private var _uploadLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val uploadLiveData: LiveData<ResponseState<String>> get() = _uploadLiveData

    private var _updateLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val updateLiveData: LiveData<ResponseState<String>> get() = _updateLiveData


    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData


    fun uploadImageInFirebaseStorage(uid: String, uri: Uri) {
        _uploadLiveData = profileRepository.uploadImageToFirebaseStorage(uid, uri)
    }

    fun updateUserInfo(updateType: String, updateValue: String) {
        when (updateType) {
            Constants.USERNAME -> {
                _updateLiveData = profileRepository.updateUserUserName(updateValue)
            }
            Constants.USER_EMAIL -> {
                _updateLiveData = profileRepository.updateUserEmail(updateValue)
            }
            Constants.USER_PASS -> {
                _updateLiveData = profileRepository.updateUserPassword(updateValue)
            }
            Constants.USER_PHONE_NUMBER -> {
                _updateLiveData = profileRepository.updateUserPhoneNumber(updateValue)
            }
            else -> {
                _updateLiveData.postValue(ResponseState.Error("Invalid update type"))
            }
        }
    }

    fun getUserFromDataBase(uid: String) {
        _userLiveData = profileRepository.getUser(uid)
    }


}