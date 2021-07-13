package com.project.design.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.design.data.model.User
import com.project.design.utils.Constants.USERS
import com.project.design.utils.ResponseState
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(USERS)

    // Sign in using google
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                var isNewUser = authTask.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val phoneNumber = firebaseUser.phoneNumber
                    val profilePic = firebaseUser.photoUrl?.toString()
                    val user = User(uid = uid, name = name, email = email,phoneNumber = phoneNumber,profilePic)
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)

                }


            } else {

                authenticatedUserMutableLiveData.value = authTask.exception?.message?.let {
                    ResponseState.Error(
                        it
                    )
                }

            }


        }
        return authenticatedUserMutableLiveData
    }

    fun firebaseSignInWithEmailPass(
        email: String,
        pass: String
    ): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { authTask ->

            if (authTask.isSuccessful) {
                val isNewUser = authTask.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val phoneNumber = firebaseUser.phoneNumber
                    val profilePic = firebaseUser.photoUrl?.toString()
                    val user = User(uid = uid, name = name, email = email,phoneNumber = phoneNumber,profilePic)
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }
            } else {
                authenticatedUserMutableLiveData.value =
                    authTask.exception?.message?.let { ResponseState.Error(it) }

            }

        }

        return authenticatedUserMutableLiveData
    }

    fun firebaseSignInWithPhone(credential: PhoneAuthCredential): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isNewUser = task.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val phoneNumber = firebaseUser.phoneNumber
                    val profilePic = firebaseUser.photoUrl?.toString()
                    val user = User(uid = uid, name = name, email = email,phoneNumber = phoneNumber,profilePic)
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }

            } else {

                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    authenticatedUserMutableLiveData.value =
                        ResponseState.Error("The verification code entered is invalid")
                }
            }

        }
        return authenticatedUserMutableLiveData
    }

    fun createUserInFireStoreIfNotExist(authenticatedUser: User): MutableLiveData<ResponseState<User>> {


        val newUserMutableLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
        val uidRef: DocumentReference = usersRef.document(authenticatedUser.uid)

        uidRef.get().addOnCompleteListener { uidTask ->
            if (uidTask.isSuccessful) {

                val document: DocumentSnapshot? = uidTask.result

                if (document != null) {
                    if (!document.exists()) {

                        authenticatedUser.isCreated = true
                        authenticatedUser.isAuthenticated = true
                        authenticatedUser.isNew = false
                        authenticatedUser.coins =0
                        authenticatedUser.isBankAccountVerified = false
                        authenticatedUser.isKycVerified = false
                        authenticatedUser.isPanVerified = false
                        authenticatedUser.isPersonalInfoVerified = false
                        authenticatedUser.isPhotoVerified = false
                        authenticatedUser.isStudentInfoVerified= false
                        authenticatedUser.referredBy = ""
                        authenticatedUser.totalReferrals =0


                        uidRef.set(authenticatedUser)
                            .addOnCompleteListener { userCreationTask ->
                                if (userCreationTask.isSuccessful) {

                                    newUserMutableLiveData.value =
                                        ResponseState.Success(authenticatedUser)
                                } else {

                                    newUserMutableLiveData.value =
                                        userCreationTask.exception?.message?.let {
                                            ResponseState.Error(
                                                it
                                            )
                                        }

                                }

                            }

                    } else {
                        newUserMutableLiveData.value = ResponseState.Success(authenticatedUser)
                    }

                } else {

                    newUserMutableLiveData.value =
                        ResponseState.Error("Some error occured, Please try again later")

                }

            } else {
                newUserMutableLiveData.value = uidTask.exception?.message?.let {
                    ResponseState.Error(
                        it
                    )
                }


            }
        }

        return newUserMutableLiveData

    }


    // register
    fun firebaseRegisterUserWithEmailPass(
        username:String,
        email: String,
        pass: String,
        mobile: String
    ): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { authTask ->

            if (authTask.isSuccessful) {
                val isNewUser = authTask.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = username
                    val email = email
                    val phoneNumber = mobile
                    val profilePic = firebaseUser.photoUrl?.toString()
                    val user = User(uid = uid, name = name, email = email,phoneNumber = phoneNumber,profilePic)
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }
            } else {
                authenticatedUserMutableLiveData.value =
                    authTask.exception?.message?.let { ResponseState.Error(it) }
            }
        }
        return authenticatedUserMutableLiveData
    }




    fun getUser(uid: String): MutableLiveData<ResponseState<User>> {
        val userMutableLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()

        usersRef.document(uid).get().addOnCompleteListener { userTask: Task<DocumentSnapshot?> ->
            if (userTask.isSuccessful) {
                val document = userTask.result
                if (document!!.exists()) {
                    val user = document.toObject(User::class.java)
                    userMutableLiveData.value = ResponseState.Success(user!!)
                } else {
                    userMutableLiveData.value = ResponseState.Error("Some error occured!!")

                }
            } else {
                userMutableLiveData.value =
                    userTask.exception?.message?.let { ResponseState.Error(it) }


            }
        }
        return userMutableLiveData
    }



}