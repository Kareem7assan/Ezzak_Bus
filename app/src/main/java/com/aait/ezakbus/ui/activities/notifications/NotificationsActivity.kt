package com.aait.ezakbus.ui.activities.notifications

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.util.Pair
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.ui.adapters.NotificationAdapter
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.activity_notification_details.*
import kotlinx.android.synthetic.main.base_recycler.*

class NotificationsActivity : ParentActivity() {
    private lateinit var mAdapter: NotificationAdapter
    override val layoutResource: Int
        get() = R.layout.activity_notifications

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun init() {
        setToolbar(getString(R.string.notifications))
        setRecycler()
        sendRequest()
        onSwipe {
            sendRequest()
        }
    }

    private fun sendRequest() {
        repo.offers(mPrefs!!.token!!).subscribeWithLoading({
            show_progress()

        },{
            if (it.value=="1"){
                if (it.data.isEmpty()){
                    show_empty()
                }
                else{
                    show_success_results()
                    mAdapter.swapData(it.data)
                }
            }
            else{
             show_error()
            }
        },{
            toasty(getString(R.string.error_connection),2)
            show_error()
        },{

        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setRecycler() {
        recycler_base.layoutManager = LinearLayoutManager(this)
        mAdapter=NotificationAdapter(this,{
            id: Int, pos: Int ->
        },{
            val intent = Intent(applicationContext, NotificationDetailsActivity::class.java)
            intent.putExtra("trip",it)


            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                Pair.create(details, "img")

            )
            startActivity(intent,options.toBundle())
        })
        recycler_base.adapter=mAdapter
    }


}
