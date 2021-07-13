package com.aait.ezakbus.models.time_distance_model

data class Data(
    val arrive_time: String?,
    val available_car_persons: Int?,
    val captain: Captain?,
    val car: Car?,
    val car_complete: String?,
    val currency: String?,
    val datetime: String?,
    val end_address: String?,
    val end_lat: String?,
    val end_long: String?,
    val end_point: String?,
    val expected_period: String?,
    val id: Int?,
    val num_persons: String?,
    val price: Int?,
    val start_address: String?,
    val start_lat: String?,
    val start_long: String?,
    val start_point: String?,
    val status: String?,
    val time_zone: String?,
    val time_zone_utc: String?,
    val traffic_line: TrafficLine?
)