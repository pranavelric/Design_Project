package com.project.design.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.project.design.data.model.OrderModel
import com.project.design.data.model.ProductModel
import com.project.design.data.model.Purchase
import com.project.design.utils.Constants
import com.project.design.utils.ResponseState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainRepository @Inject constructor() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)

    private val withdrawalRef: CollectionReference = rootRef.collection(Constants.WITHDRAWALS)


    private val productRef: CollectionReference = rootRef.collection(Constants.PRODUCTS)


    suspend fun getWithdrawalNotificationsSize(): MutableLiveData<ResponseState<Int>> {
        val gameMutableLiveData: MutableLiveData<ResponseState<Int>> =
            MutableLiveData()


        withdrawalRef.whereEqualTo("status", "pending").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result != null && !task.result!!.isEmpty) {

                    //size = task.result!!.size()
                    gameMutableLiveData.value = ResponseState.Success(task.result!!.size())


                } else {
                    //size = 0
                    gameMutableLiveData.value = ResponseState.Success(0)
                }

            } else {

                gameMutableLiveData.value = task.exception?.message?.let { ResponseState.Error(it) }
            }
        }.await()
        return gameMutableLiveData
    }


    val productList: ArrayList<ProductModel?> = ArrayList()
    suspend fun getProducts(): MutableLiveData<ResponseState<List<ProductModel?>>> {
        val gameMutableLiveData: MutableLiveData<ResponseState<List<ProductModel?>>> =
            MutableLiveData()
        productList.clear()
        productRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null && !it.result!!.isEmpty) {

                    for (doc: DocumentSnapshot in it.result!!.documents) {
                        val obj = doc.toObject(ProductModel::class.java)
                        productList.add(obj)
                    }

                    gameMutableLiveData.value = ResponseState.Success(productList)

                } else {

                    gameMutableLiveData.value = ResponseState.Error("No products present")


                }
            } else {
                gameMutableLiveData.value =
                    ResponseState.Error("Some error occured: ${it.exception?.message}")

            }
        }.await()

        return gameMutableLiveData

    }


    suspend fun createUserDeposits(deposit:OrderModel): MutableLiveData<ResponseState<String>> {

        val newUserDepositMutableLiveData: MutableLiveData<ResponseState<String>> =
            MutableLiveData()


        val userDepositRef =   usersRef.document(deposit.userId!!).collection(Constants.PRODUCTS).document(deposit.id.toString())

                val globalDepositRef = rootRef.collection(Constants.ORDERS).document()



        rootRef.runBatch {batch->

        batch.set(userDepositRef,deposit)
        batch.set(globalDepositRef,deposit)
        }.addOnCompleteListener {
            if(it.isSuccessful){
               newUserDepositMutableLiveData.value = ResponseState.Success("Order places successfully")
            }
            else{
                newUserDepositMutableLiveData.value = ResponseState.Error("Some error occured:${it.exception?.message}")
            }
        }.await()



        return newUserDepositMutableLiveData

    }

}