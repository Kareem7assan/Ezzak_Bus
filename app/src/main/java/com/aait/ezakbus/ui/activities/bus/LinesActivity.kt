package com.aait.ezakbus.ui.activities.bus

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.city_liens_model.Line
import com.aait.ezakbus.models.line_branch_model.LineBranchesModel
import com.aait.ezakbus.ui.adapters.DayAdapter
import com.aait.ezakbus.ui.adapters.LineAdapterDetails
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.activity_lines.*
import kotlinx.android.synthetic.main.toolbar_bus.*

class LinesActivity : ParentActivity() {
    private lateinit var line: Line
    override val layoutResource: Int
        get() = R.layout.activity_lines
    private val mAdapter=LineAdapterDetails()
    private val mAdapterDays=DayAdapter{
        it.date?.let { it1 -> sendRequestLines(it1) }
    }

    override fun init() {
        setToolbar(getString(R.string.choose_lift))
        spinner_lay.visibility= View.GONE
        line=intent.getSerializableExtra("line") as Line
        src.text=line.city1
        dist.text=line.city2
        point_src.text= String.format(getString(R.string.point_sep),line.first_point)
        point_dist.text= String.format(getString(R.string.point_sep),line.last_point)
        setRecDays()
        setRecLines()
        sendRequestLines(line.days?.get(0)?.date.toString())
    }

    private fun sendRequestLines(day:String) {
        repo.trafficLineBranches(line.id.toString(), day
        ,mPrefs!!.token!!
        ).subscribeWithLoading({showProgressDialog()},{handleData(it)},
            {handleError(it)},{})
    }

    private fun handleData(data: LineBranchesModel) {
        hideProgressDialog()
        if (data.value=="1"){
            data.data?.let { mAdapter.swapData(it) }
        }
    }

    private fun handleError(error: Throwable) {
        hideProgressDialog()
        error.message?.let { toasty(it) }
        Log.e("error",error.message)
    }

    private fun setRecLines() {
        rec_lines.layoutManager=LinearLayoutManager(this)
        rec_lines.adapter=mAdapter
    }

    private fun setRecDays() {
        line.days?.let { mAdapterDays.swapData(it) }
        rec_days.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rec_days.adapter=mAdapterDays
    }

}