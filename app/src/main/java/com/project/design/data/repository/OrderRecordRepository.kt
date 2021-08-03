package com.project.design.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.project.design.data.model.OrderModel
import com.project.design.data.model.ProductModel
import com.project.design.data.model.Purchase
import com.project.design.utils.Constants
import com.project.design.utils.ResponseState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OrderRecordRepository @Inject constructor() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)

    private val withdrawalRef: CollectionReference = rootRef.collection(Constants.WITHDRAWALS)




    val productList: ArrayList<OrderModel?> = ArrayList()
    suspend fun getOrderProducts(userid:String): MutableLiveData<ResponseState<List<OrderModel?>>> {
        val gameMutableLiveData: MutableLiveData<ResponseState<List<OrderModel?>>> =
            MutableLiveData()
        productList.clear()
     usersRef.document(userid).collection(Constants.ORDERS).orderBy("date",Query.Direction.DESCENDING).get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null && !it.result!!.isEmpty) {

                    for (doc: DocumentSnapshot in it.result!!.documents) {
                        val obj = doc.toObject(OrderModel::class.java)
                        productList.add(obj)
                    }

                    gameMutableLiveData.value = ResponseState.Success(productList)

                } else {

                    gameMutableLiveData.value = ResponseState.Error("No orders present")


                }
            } else {
                gameMutableLiveData.value =
                    ResponseState.Error("Some error occured: ${it.exception?.message}")

            }
        }.await()

        return gameMutableLiveData

    }


}