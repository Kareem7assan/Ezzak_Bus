package com.aait.ezakbus.ui.activities.bus

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.bus_menu.BusMenuModel
import com.aait.ezakbus.ui.adapters.BusMenuAdapter
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.base_recycler.*
import kotlinx.android.synthetic.main.toolbar_bus.*

class BusMenuActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_bus_menu
    val mAdapter=BusMenuAdapter()

    override fun init() {
        spinner_lay.visibility= View.GONE
        setRec()
        sendRequest()
        onSwipe {
            sendRequest()
        }
    }

    private fun sendRequest() {
        repo.busMenu(mPrefs?.token!!).subscribeWithLoading({show_progress()},{handleData(it)},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        show_error()
        if (message != null) {
            toasty(message,2)
        }
    }

    private fun handleData(it: BusMenuModel) {
        hideProgressDialog()
        if (it.value=="1"){
            show_success_results()
            if (it.data?.orders?.isNotEmpty()!!){
                show_success_results()
                mAdapter.swapData(it.data.orders)
            }
            else{
                show_empty()
            }
        }
        else{
            show_error()
        }

    }

    private fun setRec() {
        recycler_base.layoutManager=LinearLayoutManager(this)
        recycler_base.adapter=mAdapter
    }

}