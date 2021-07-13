package com.aait.ezakbus.ui.activities.notifications

import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.notification_model.Data
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_notification_details.*

class NotificationDetailsActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_notification_details

    override fun init() {
        var data=intent.getSerializableExtra("trip") as (Data)
        Picasso.get().load(data.img).into(img_disp)
        title_tv.text = data.title
        details.text = data.desc
        date.text = data.date

    }


}
