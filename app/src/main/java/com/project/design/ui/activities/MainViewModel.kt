package com.project.design.ui.activities

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.design.data.model.OrderModel
import com.project.design.data.repository.MainRepository
import com.project.design.utils.ResponseState
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(val mainRepository: MainRepository) : ViewModel() {

    private var _createDepositLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val createDepositLiveData: LiveData<ResponseState<String>> get() = _createDepositLiveData


    fun createDeposit(deposit: OrderModel) = viewModelScope.launch {
        _createDepositLiveData.postValue(mainRepository.createUserDeposits(deposit).value)
    }


}