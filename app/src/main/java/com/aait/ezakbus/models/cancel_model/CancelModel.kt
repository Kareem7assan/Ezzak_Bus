package com.aait.ezakbus.models.cancel_model

import com.aait.ezakbus.models.client_later_model.Reason

data class CancelModel(
    val code: Int,
    val `data`: List<Reason>,
    val msg: String,
    val key: String,
    val value: String
)