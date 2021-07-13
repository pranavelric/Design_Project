package com.project.design.ui.order_history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.design.data.model.OrderModel
import com.project.design.data.repository.OrderRecordRepository
import com.project.design.utils.ResponseState
import kotlinx.coroutines.launch

class OrderHistoryViewModel @ViewModelInject constructor(val homeRepository: OrderRecordRepository) : ViewModel() {


    private var _getOrderProducts: MutableLiveData<ResponseState<List<OrderModel?>>> =
        MutableLiveData()
    val getOrderProducts: LiveData<ResponseState<List<OrderModel?>>> get() = _getOrderProducts


    fun getOrderProduct(userId:String) = viewModelScope.launch {
        _getOrderProducts.postValue(homeRepository.getOrderProducts(userId).value)
    }
}