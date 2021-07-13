package com.project.design.ui.purchase

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.design.data.model.Notifications
import com.project.design.data.model.Purchase
import com.project.design.data.model.User
import com.project.design.data.repository.PurchaseRepository
import com.project.design.utils.NetworkHelper
import com.project.design.utils.ResponseState
import kotlinx.coroutines.launch

class PurcahseViewModel @ViewModelInject constructor(
    val repository: PurchaseRepository,
    val networkHelper: NetworkHelper
) : ViewModel() {

    private var _dateWisePurchase: MutableLiveData<ResponseState<List<Purchase?>>> =
        MutableLiveData()
    val dateWisePurchase: LiveData<ResponseState<List<Purchase?>>> get() = _dateWisePurchase


    private var _purchaseAmountStatusLiveData: MutableLiveData<ResponseState<String>> =
        MutableLiveData()
    val purchaseAmountStatusLiveData: LiveData<ResponseState<String>> get() = _purchaseAmountStatusLiveData

    private var _purchaseLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val purchaseLiveData: LiveData<ResponseState<String>> get() = _purchaseLiveData

    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData

    private var _getPrevPurchaseLiveData: MutableLiveData<ResponseState<Purchase>> =
        MutableLiveData()
    val prevPurchaseLiveData: LiveData<ResponseState<Purchase>> get() = _getPrevPurchaseLiveData

    private var _allPrevPurchasesForUser: MutableLiveData<ResponseState<List<Purchase?>>> =
        MutableLiveData()
    val allPrevPurchasesForUser: LiveData<ResponseState<List<Purchase?>>> get() = _allPrevPurchasesForUser


    private var _allPrevPurchases: MutableLiveData<ResponseState<List<Purchase?>>> =
        MutableLiveData()
    val allPrevPurchases: LiveData<ResponseState<List<Purchase?>>> get() = _allPrevPurchases


    private var _sendCoinsLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val sendCoinsLiveData: LiveData<ResponseState<String>> get() = _sendCoinsLiveData


    fun getUserFromDataBase(uid: String) = viewModelScope.launch {
        _userLiveData.postValue(repository.getUser(uid).value)
    }

    fun deductCoinForPurchase(userId: String, amount: Int) = viewModelScope.launch {
        _purchaseAmountStatusLiveData.postValue(
            repository.deductPurchaseAmountFromUserAccount(
                userId,
                amount
            ).value
        )
    }

    fun addPurchase(userId: String, purchase: Purchase) = viewModelScope.launch {
        _purchaseLiveData.postValue(
            repository.createPurchaseInFireStore(
                userId,
                purchase
            ).value
        )
    }

    fun getPreviousPurchase(userId: String) = viewModelScope.launch {
        _getPrevPurchaseLiveData.postValue(repository.getPreviousPurchase(userId).value)
    }


    fun getAllPurchase(userId: String) = viewModelScope.launch {
        _allPrevPurchasesForUser.postValue(repository.getAllPreviousPurchases(userId).value)
    }

    fun getAllPurchase() = viewModelScope.launch {
        _allPrevPurchases.postValue(repository.getAllPurchases().value)
    }


    fun getDateWisePurchse(date: String) = viewModelScope.launch {
        _dateWisePurchase.postValue(repository.getAllPurchaseDateWise(date).value)
    }

    fun sendTodaysEarningToAllPurchases(
        amount: Int,
        date: String
    ) = viewModelScope.launch {

        _sendCoinsLiveData.postValue(repository.sendTodaysEarningToAllPurchases(amount, date).value)

    }


    fun sendNotificationToAllPurchaseUsers(notifications: Notifications) =viewModelScope.launch {
        repository.sendUserNotifications(notifications)
    }



}