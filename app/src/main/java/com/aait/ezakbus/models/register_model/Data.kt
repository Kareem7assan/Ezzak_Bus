package com.aait.ezakbus.models.register_model


import com.google.gson.annotations.SerializedName
data class UserModel(
    var active: String?,
    var avatar: String?,
    var balance: String?,
    var captain: String?,
    @SerializedName("phonekey")
    var phonekey: String?,
    var phone_registered:Boolean?=false,
    var time_zone: String?,
    var is_registered: Boolean,
    var code: String?,
    var country_id:String?,
    @SerializedName("device_id")
    var deviceId: String?,
    var email: String?,
    @SerializedName("name")
    var firstName: String?,
    var googlekey: String?,
    var id: Int?,
    @SerializedName("last_name")
    var lastName: String?,
    var registered_social:Boolean?=false,
    var phone: String?,
    var plan: String?=null,
    var token: String?
)

