package com.aait.ezakbus.models.line_points

import com.aait.ezakbus.models.city_liens_model.Days
import java.io.Serializable

data class Point(
    val address: String?,
    val currency: String?,
    val expected_time: String?,
    val id: Int?,
    val lat: String?,
    val long: String?,
    val minute: String?,
    val name: String?,
    val order: Int?,
    val price: String?,
    val days: List<Days>?=null
):Serializable