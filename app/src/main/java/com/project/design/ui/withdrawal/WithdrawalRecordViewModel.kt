package com.project.design.ui.withdrawal

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.design.data.model.Withdrawal
import com.project.design.utils.ResponseState
import kotlinx.coroutines.launch

class WithdrawalRecordViewModel @ViewModelInject constructor() :
    ViewModel() {

    private var _withdrawalRecordLiveData: MutableLiveData<ResponseState<List<Withdrawal?>>> =
        MutableLiveData()
    val withdrawalRecordLiveData: LiveData<ResponseState<List<Withdrawal?>>> get() = _withdrawalRecordLiveData

    private var _withdrawalDoneLiveData: MutableLiveData<ResponseState<String>> =
        MutableLiveData()
    val withdrawalDoneLiveData: LiveData<ResponseState<String>> get() = _withdrawalDoneLiveData


    fun getWithdrawalRecords() = viewModelScope.launch {
  //      _withdrawalRecordLiveData.postValue(repository.getWithdrawalRecords().value)
    }

    fun setPaymentDoneFor(withdrawal: Withdrawal) = viewModelScope.launch {

 //       _withdrawalDoneLiveData.postValue(repository.setPaymentDoneFor(withdrawal).value)
    }


}