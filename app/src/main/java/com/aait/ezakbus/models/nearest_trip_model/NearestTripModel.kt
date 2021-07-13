package com.aait.ezakbus.models.nearest_trip_model


data class NearestTripModel(
    var `data`: List<Data>,
    var code: Int?,
    var key: String?,
    var value: String?,
    var msg:String
)