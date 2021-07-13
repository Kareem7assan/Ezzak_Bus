package com.aait.ezakbus.ui.activities.bus

import android.content.Intent
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.line_points.Point
import kotlinx.android.synthetic.main.activity_bus_reserved.*
import kotlinx.android.synthetic.main.item_adapter_details_no_seperator.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class BookingMessageActivity : ParentActivity() {
    private var arrival_time: String? = null
    private var startPos: Point? = null
    private var endPos: Point? = null
    private var mId: String? = ""
    override val layoutResource: Int
        get() = R.layout.activity_bus_reserved

    override fun init() {
        arrival_time= intent.getStringExtra("arrive_time")
        startPos=intent.getSerializableExtra("src_pos") as Point
        endPos=intent.getSerializableExtra("dist_pos") as Point
        mId=intent.getStringExtra("order_id")
        setTripData()
        setActions()
    }

    private fun setActions() {
        prev_btn.onClick {
            startActivity(Intent(applicationContext
            ,BookingDetailsActivity::class.java
            ).apply {
                putExtra("order_id",mId)
            })
            finish()
        }

    }

    private fun setTripData() {
        reach_tv.text=getString(R.string.reach)+" "+arrival_time
        src_city.text=startPos?.name+","+startPos?.address
        tv_duration.text=endPos?.expected_time+" "+getString(R.string.mins)
        dist_city.text=endPos?.name+","+endPos?.address
        tv_price.text=endPos?.price+" "+getString(R.string.rs)
    }

}