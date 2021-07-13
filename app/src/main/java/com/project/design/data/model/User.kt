package com.project.design.data.model

import java.io.Serializable

data class User(
    val uid: String,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    var imageUrl: String?,
    var totalReferrals: Int = 0,
    var referredBy: String = "",
    var coins: Int = 0,
    var isAuthenticated: Boolean = false,
    var isNew: Boolean? = false,
    var isCreated: Boolean = false,
    var isKycVerified: Boolean = false,
    var isPanVerified: Boolean = false,
    var isPhotoVerified: Boolean = false,
    var isPersonalInfoVerified: Boolean = false,
    var isStudentInfoVerified: Boolean = false,
    var isBankAccountVerified: Boolean = false,
    var totalEarned: Int? = 0
) : Serializable {

    constructor() : this("", "", "", "", "") {

    }

}

