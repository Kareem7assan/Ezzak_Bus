package com.aait.ezakbus.ui.activities.bus

import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import kotlinx.android.synthetic.main.activity_ticket.*

class TicketActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_ticket

    override fun init() {
        var ticket=intent.getStringExtra("ticket")
        tv_ticket.text=ticket
    }

}