package com.aait.ezakbus.models.ticket_model

import com.google.gson.annotations.SerializedName

data class Client(
    val avatar: String?,
    val client_end_address: String?,
    val client_end_lat: String?,
    val client_end_long: String?,
    val client_id: Int?,
    val client_start_address: String?,
    val client_start_lat: String?,
    val client_start_long: String?,
    val client_status: String?,
    val currency: String?,
    val distance: Double?,
    val end_point: String?,
    val expected_period: String?,
    val id: Int?,
    val name: String?,
    val num_persons: Int?,
    val order_id: Int?,
    val phone: String?,
    val price: Int?,
    val start_point: String?,
    @SerializedName("user_number")
    val ticketNumber: String?
)