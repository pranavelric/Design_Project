package com.project.design.data.repository


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.project.design.data.model.Notifications
import com.project.design.data.model.Purchase
import com.project.design.data.model.User
import com.project.design.utils.Constants
import com.project.design.utils.ResponseState
import com.project.design.utils.getTodaysDate
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PurchaseRepository @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val purchaseRef: CollectionReference = rootRef.collection(Constants.PURCHASE)
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)
//    private val purchaseByDateRef:CollectionRe

    suspend fun createPurchaseInFireStore(
        userId: String,
        purchase: Purchase
    ): MutableLiveData<ResponseState<String>> {

        val newUserMutableLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
        val uidRef: DocumentReference =
            purchaseRef.document(userId).collection(Constants.ALL_PURCHASES).document(purchase.id)


        uidRef.set(purchase)
            .addOnCompleteListener { userCreationTask ->
                if (userCreationTask.isSuccessful) {

                    newUserMutableLiveData.value =
                        ResponseState.Success("Purchase done successfully")


                } else {

                    newUserMutableLiveData.value =
                        userCreationTask.exception?.message?.let {
                            ResponseState.Error(
                                it
                            )
                        }

                }

            }.await()



        return newUserMutableLiveData

    }


    suspend fun deductPurchaseAmountFromUserAccount(
        uid: String,
        amount: Int
    ): MutableLiveData<ResponseState<String>> {
        val newUserMutableLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()

        val file: MutableMap<String, Any> = HashMap()
        file["coins"] = FieldValue.increment(-(amount.toDouble()))

        usersRef.document(uid).update(file).addOnCompleteListener {
            if (it.isSuccessful) {
                newUserMutableLiveData.value =
                    ResponseState.Success("Amount paid successfully")

            } else {
                newUserMutableLiveData.value =
                    it.exception?.message?.let { it1 -> ResponseState.Error(it1) }

            }
        }.await()
        return newUserMutableLiveData
    }

    suspend fun getUser(uid: String): MutableLiveData<ResponseState<User>> {
        val userMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()

        usersRef.document(uid).get()
            .addOnCompleteListener { userTask: Task<DocumentSnapshot?> ->
                if (userTask.isSuccessful) {
                    val document = userTask.result
                    if (document!!.exists()) {
                        val user = document.toObject(User::class.java)
                        userMutableLiveData.value = ResponseState.Success(user!!)
                    } else {
                        userMutableLiveData.value =
                            ResponseState.Error("Some error occured!!")

                    }
                } else {
                    userMutableLiveData.value =
                        userTask.exception?.message?.let { ResponseState.Error(it) }

                }
            }.await()
        return userMutableLiveData
    }


    suspend fun getPreviousPurchase(userId: String): MutableLiveData<ResponseState<Purchase>> {

        val gameMutableLiveData: MutableLiveData<ResponseState<Purchase>> =
            MutableLiveData()

        purchaseRef.document(userId).collection(Constants.ALL_PURCHASES)
            .orderBy("id", Query.Direction.DESCENDING).limit(1).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null)
                        if (!task.result?.isEmpty!!) {

                            val res = task.result!!.documents[0].toObject(Purchase::class.java)
                            gameMutableLiveData.value = ResponseState.Success(res!!)
                        } else {
                            gameMutableLiveData.value =
                                ResponseState.Error(Constants.NO_PREVIOUS_GAME)
                        }
                } else {
                    gameMutableLiveData.value =
                        task.exception?.message?.let { ResponseState.Error(it) }
                }

            }.await()
        return gameMutableLiveData
    }


    suspend fun getAllPreviousPurchases(userId: String): MutableLiveData<ResponseState<List<Purchase?>>> {

        val list = ArrayList<Purchase?>()
        val gameMutableLiveData: MutableLiveData<ResponseState<List<Purchase?>>> =
            MutableLiveData()

        purchaseRef.document(userId).collection(Constants.ALL_PURCHASES)
            .orderBy("id", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null)
                        if (!task.result?.isEmpty!!) {


                            for (doc: DocumentSnapshot in task.result!!.documents) {
                                val obj = doc.toObject(Purchase::class.java)
                                list.add(obj)
                            }

                            gameMutableLiveData.value = ResponseState.Success(list)

                        } else {
                            gameMutableLiveData.value =
                                ResponseState.Error(Constants.NO_PREVIOUS_GAME)
                        }
                } else {
                    gameMutableLiveData.value =
                        task.exception?.message?.let { ResponseState.Error(it) }
                }

            }.await()
        return gameMutableLiveData

    }


    suspend fun getAllPurchaseDateWise(date: String): MutableLiveData<ResponseState<List<Purchase?>>> {

        var job: Job? = null

        val gameMutableLiveData: MutableLiveData<ResponseState<List<Purchase?>>> =
            MutableLiveData()

        list.clear()
        purchaseRef.get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null) {
                        if (!task.result?.isEmpty!!) {


                            job = CoroutineScope(Dispatchers.IO).launch {

                                for (doc: DocumentSnapshot in task.result!!.documents) {
                                    //  CoroutineScope(Dispatchers.IO).launch {

                                    showDateWisePurchase(doc.id, date)


                                    //   }
                                }
                                //        Log.d("RRR", "temp: ${list}")

                            }


                            // Log.d("RRR", "after temp: ${list}")
                            gameMutableLiveData.value = ResponseState.Success(list)


                        }
                    } else {
                        gameMutableLiveData.value =
                            ResponseState.Error("No purchase made on this date")
                    }
                } else {
                    gameMutableLiveData.value = task.exception!!.message?.let {
                        ResponseState.Error(
                            it
                        )
                    }
                }

            }.await()

        job?.join()

        if (gameMutableLiveData.value is ResponseState.Success || (gameMutableLiveData.value == null))
            gameMutableLiveData.value = ResponseState.Success(list)
        return gameMutableLiveData
    }

    val list = ArrayList<Purchase?>()
    private suspend fun showDateWisePurchase(id: String, date: String) {

        Log.d("dateee", "showDateWisePurchase:${date} ${id}")
        purchaseRef.document(id).collection(Constants.ALL_PURCHASES)
            .whereEqualTo("purchaseDate", date).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null&& !task.result?.isEmpty!!)



                            for (doc: DocumentSnapshot in task.result!!.documents) {
                                val obj = doc.toObject(Purchase::class.java)

                                list.add(obj)
                            }


                        else {

                            Log.d("abc", "showDateWisePurchase empty ")
                        }
                } else {

                    Log.d("abc", "showDateWisePurchase: ${task.exception?.message}")
                }

            }.await()
    }


    val allPurchaselist = ArrayList<Purchase?>()
    suspend fun getAllPurchases(): MutableLiveData<ResponseState<List<Purchase?>>> {

        var job: Job? = null

        val gameMutableLiveData: MutableLiveData<ResponseState<List<Purchase?>>> =
            MutableLiveData()

        allPurchaselist.clear()
        purchaseRef.get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null) {
                        if (!task.result?.isEmpty!!) {


                            job = CoroutineScope(Dispatchers.IO).launch {

                                for (doc: DocumentSnapshot in task.result!!.documents) {
                                    //  CoroutineScope(Dispatchers.IO).launch {

                                    showAllPurchases(doc.id)


                                    //   }
                                }
                                //        Log.d("RRR", "temp: ${list}")

                            }


                            // Log.d("RRR", "after temp: ${list}")
                            gameMutableLiveData.value = ResponseState.Success(allPurchaselist)


                        }
                    } else {
                        gameMutableLiveData.value =
                            ResponseState.Error("No purchase made on this date")
                    }
                } else {
                    gameMutableLiveData.value = task.exception!!.message?.let {
                        ResponseState.Error(
                            it
                        )
                    }
                }

            }.await()

        job?.join()
        //Log.d("RRR", "getAllPurchaseDateWise:list is ${list} ")
        if (gameMutableLiveData.value is ResponseState.Success || (gameMutableLiveData.value == null))
            gameMutableLiveData.value = ResponseState.Success(allPurchaselist)
        return gameMutableLiveData
    }


    private suspend fun showAllPurchases(id: String) {


        purchaseRef.document(id).collection(Constants.ALL_PURCHASES)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null)
                        if (!task.result?.isEmpty!!) {


                            for (doc: DocumentSnapshot in task.result!!.documents) {
                                val obj = doc.toObject(Purchase::class.java)

                                allPurchaselist.add(obj)
                            }


                        } else {

                            Log.d("abc", "showDateWisePurchase empty ")
                        }
                } else {

                    Log.d("abc", "showDateWisePurchase: ${task.exception?.message}")
                }

            }.await()
    }


    suspend fun sendTodaysEarningToAllPurchases(
        amount: Int,
        date: String
    ): MutableLiveData<ResponseState<String>> {

        var job: Job? = null

        val gameMutableLiveData: MutableLiveData<ResponseState<String>> =
            MutableLiveData()


        purchaseRef.get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null) {
                        if (!task.result?.isEmpty!!) {


                            job = CoroutineScope(Dispatchers.IO).launch {

                                for (doc: DocumentSnapshot in task.result!!.documents) {


                                    sendCoinsToAllPurchases(amount, doc.id, date)


                                }


                            }


                            gameMutableLiveData.value =
                                ResponseState.Success("Coins send to users account successfully")


                        }
                    } else {
                        gameMutableLiveData.value =
                            ResponseState.Error("No purchase made on this date")
                    }
                } else {
                    gameMutableLiveData.value = task.exception!!.message?.let {
                        ResponseState.Error(
                            it
                        )
                    }
                }

            }.await()

        job?.join()


        if(list.isEmpty()){
            gameMutableLiveData.value = ResponseState.Error("No purchase made today")
        }
        else{
            gameMutableLiveData.value = ResponseState.Success("Coins send to users account successfully")
        }
        return gameMutableLiveData
    }


    private suspend fun sendCoinsToAllPurchases(amount: Int, id: String, date: String) {

        var job: Job? = null

        purchaseRef.document(id).collection(Constants.ALL_PURCHASES)
            .whereEqualTo("purchaseDate", date)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null)
                        if (!task.result?.isEmpty!!) {


                            job = CoroutineScope(Dispatchers.IO).launch {
                                for (doc: DocumentSnapshot in task.result!!.documents) {
                                    val obj = doc.toObject(Purchase::class.java)

                                    sendCoinsToAllPurchaseObj(amount, id, obj?.id, date)
                                }
                            }

                        } else {

                            Log.d("abc", "showDateWisePurchase empty ")
                        }
                } else {

                    Log.d("abc", "showDateWisePurchase: ${task.exception?.message}")
                }

            }.await()

        job?.join()

    }

    private suspend fun sendCoinsToAllPurchaseObj(
        amount: Int,
        id: String,
        pid: String?,
        date: String
    ) {

        var job: Job? = null

        val file: MutableMap<String, Any> = HashMap()
        file["today_earning"] = FieldValue.increment(amount.toDouble())


        purchaseRef.document(id).collection(Constants.ALL_PURCHASES).document(pid!!)
            .collection(Constants.TODAY_EARNING).document(getTodaysDate()).set(file, SetOptions.merge()).addOnCompleteListener {

                if (it.isSuccessful) {

                    job = CoroutineScope(Dispatchers.IO).launch {
                        updateCoinsAmountForUserAccount(id, amount)

                    }
                } else {

                }

            }


        job?.join()

    }


    suspend fun updateCoinsAmountForUserAccount(
        uid: String,
        amount: Int
    ): MutableLiveData<ResponseState<String>> {
        val newUserMutableLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()

        val file: MutableMap<String, Any> = HashMap()
        file["coins"] = FieldValue.increment((amount.toDouble()))
        file["totalEarned"] = FieldValue.increment(amount.toDouble())

        usersRef.document(uid).update(file).addOnCompleteListener {
            if (it.isSuccessful) {
                newUserMutableLiveData.value =
                    ResponseState.Success("Amount added successfully")

            } else {
                newUserMutableLiveData.value =
                    it.exception?.message?.let { it1 -> ResponseState.Error(it1) }

            }
        }.await()
        return newUserMutableLiveData
    }











    suspend fun sendUserNotifications(notifications: Notifications): MutableLiveData<ResponseState<String?>> {

        var job: Job? = null

        val gameMutableLiveData: MutableLiveData<ResponseState<String?>> =
            MutableLiveData()


        purchaseRef.get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null) {
                        if (!task.result?.isEmpty!!) {


                            job = CoroutineScope(Dispatchers.IO).launch {

                                for (doc: DocumentSnapshot in task.result!!.documents) {
                                    //  CoroutineScope(Dispatchers.IO).launch {

                                    sendNotfiToAllPurchasesUsers(doc.id,notifications)
                                    Log.d("RRR", "sendNotfiToAllPurchasesUsers:${doc.id} doc id -- ")

                                    //   }
                                }
                                //        Log.d("RRR", "temp: ${list}")

                            }


                            // Log.d("RRR", "after temp: ${list}")
                            gameMutableLiveData.value = ResponseState.Success("Notifications send!")


                        }
                    } else {
                        gameMutableLiveData.value =
                            ResponseState.Error("No purchase made on this date")
                    }
                } else {
                    gameMutableLiveData.value = task.exception!!.message?.let {
                        ResponseState.Error(
                            it
                        )
                    }
                }

            }.await()

        job?.join()

        if (gameMutableLiveData.value is ResponseState.Success || (gameMutableLiveData.value == null))
            gameMutableLiveData.value = ResponseState.Success("Notifications send to users")
        return gameMutableLiveData
    }


    private suspend fun sendNotfiToAllPurchasesUsers(id: String,notifications: Notifications) {


        Log.d("RRR", "sendNotfiToAllPurchasesUsers:${id} -- ")
       usersRef.document(id).collection(Constants.NOTIFICATIONS).document().set(notifications)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Log.d("RRR", "Notifications send sucessfully")
                } else {

                    Log.d("RRRR", "otifications send failed: ${task.exception?.message}")
                }

            }.await()
    }

















}