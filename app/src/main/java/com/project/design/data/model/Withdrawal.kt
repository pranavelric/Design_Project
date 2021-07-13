package com.project.design.data.model

import java.io.Serializable

data class Withdrawal(
    var id: String,
    var time:String?="",
    var amount:String?="",
    var charges:String?="",
    var date:String?="",
    var payId:String?="",
    var userId:String?="",
    var status:String?="",
    var upiCode:String?="",
) : Serializable {

    constructor() : this("") {

    }

}
