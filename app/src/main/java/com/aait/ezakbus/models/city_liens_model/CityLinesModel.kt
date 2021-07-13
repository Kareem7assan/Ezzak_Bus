package com.aait.ezakbus.models.city_liens_model

import java.io.Serializable

data class CityLinesModel(
    val code: Int?,
    val `data`: List<Line>?,
    val key: String?,
    val value: String?
):Serializable