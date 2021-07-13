package com.project.design.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.design.data.model.ProductModel
import com.project.design.data.repository.HomeRepository
import com.project.design.utils.ResponseState
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(val homeRepository: HomeRepository) : ViewModel() {


    private var _withdrawalSizeLiveData: MutableLiveData<ResponseState<Int>> = MutableLiveData()
    val withdrawalSizeLiveData: LiveData<ResponseState<Int>> get() = _withdrawalSizeLiveData


    private var _getProducts: MutableLiveData<ResponseState<List<ProductModel?>>> =
        MutableLiveData()
    val getProducts: LiveData<ResponseState<List<ProductModel?>>> get() = _getProducts


    fun notifSize() = viewModelScope.launch {
        _withdrawalSizeLiveData.postValue(homeRepository.getWithdrawalNotificationsSize().value)
    }


    fun getProduct() = viewModelScope.launch {
        _getProducts.postValue(homeRepository.getProducts().value)
    }


}