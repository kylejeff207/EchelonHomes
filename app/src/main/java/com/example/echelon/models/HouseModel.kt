package com.example.echelon.models

data class HouseModel(
    var id: String? = null,
    var houseLocation: String = "",
    var uploaderName: String = "",
    var phoneNumber: String = "",
    var housePrice: String = "",
    var imageUrl: String? = null,
    var uploaderId: String? = null
)
