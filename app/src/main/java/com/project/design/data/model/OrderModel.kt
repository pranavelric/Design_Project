package com.project.design.data.model

import com.project.design.utils.getCurrentTime
import com.project.design.utils.getTodaysDate

data class OrderModel(
    var id:String?="",
    var productId:String?= "",
    var paidAmount:String?="",
    var time:String?= getCurrentTime(),
    var date:String?= getTodaysDate(),
    var userId:String? = "",
    var isDelivered:Boolean?=false,
    var prodImg:String?="",
)