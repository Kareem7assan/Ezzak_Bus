package com.aait.ezakbus.models.wholde_places_model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SavedPlace(
    var address: String?,
    var distance: String?,
    var id: Int?,
    var lat: Double?,
    var long: Double?,
    var name: String?,
    @SerializedName("place_id")
    var placeId:String?,
    var infav: Boolean?=false
):Serializable