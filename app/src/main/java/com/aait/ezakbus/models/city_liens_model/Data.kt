package com.aait.ezakbus.models.city_liens_model

import java.io.Serializable

data class Line(
    val city1: String?,
    val city2: String?,
    val first_point: String?,
    val last_point: String?,
    val expected_time: String?,
    val currency: String?,
    val days: List<Days>?,
    val id: Int?,
    val name: String?,
    val price: String?
):Serializable