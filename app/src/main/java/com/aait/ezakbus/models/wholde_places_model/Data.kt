package com.aait.ezakbus.models.wholde_places_model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("next_page_token")
    var nextPageToken: String?,
    @SerializedName("places")
    var nearestPlaces: List<Place>,
    @SerializedName("saved_places")
    var savedPlaces: List<SavedPlace>
)