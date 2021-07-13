package com.aait.ezakbus.models.bids_model


data class Data(
    var bids: List<Bid>,
    var order: Order?
)