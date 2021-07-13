package com.aait.ezakbus.models.ticket_model

data class Data(
    val available_car_persons: Int?,
    val captain: Captain?,
    val car: Car?,
    val car_complete: String?,
    val cartype: String?,
    val client: Client?,
    val country_id: String?,
    val currency: String?,
    val current_order_persons: Int?,
    val end_address: String?,
    val end_lat: String?,
    val end_long: String?,
    val end_point: String?,
    val id: Int?,
    val max_car_persons: Int?,
    val order_type: String?,
    val payment_type: String?,
    val start_address: String?,
    val start_lat: String?,
    val start_long: String?,
    val start_point: String?,
    val status: String?,
    val time_zone: String?,
    val time_zone_utc: String?,
    val traffic_line: String?,
    val traffic_line_id: Int?,
    val traffic_line_price: String?
)