package com.project.design.data.model

import java.io.Serializable

data class Purchase(
    var id: String,
    var purchaseUid: String? = "",
    var userId: String = "",
    var todaysEarning: Int = 0,
    var purchaseDate: String? = "",
    var endDate: String? = "",
    var purchaseAmount: Int? = 100,
    var purchaseTime: String? = ""
) : Serializable {

    constructor() : this("") {

    }

}
