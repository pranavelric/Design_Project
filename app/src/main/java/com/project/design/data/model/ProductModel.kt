package com.project.design.data.model

import java.io.Serializable

data class ProductModel(
    var id: String? = "",
    var price: Int? = 0,
    var inStock: Boolean? = false,
    var name: String? = "",
    var desc: String? = "",
    var image:String?="",


): Serializable {



}
