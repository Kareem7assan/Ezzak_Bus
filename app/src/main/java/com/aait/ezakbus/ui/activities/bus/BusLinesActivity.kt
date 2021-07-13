package com.aait.ezakbus.ui.activities.bus

import android.animation.ObjectAnimator
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.cities_model.CitiesModel
import com.aait.ezakbus.models.city_liens_model.CityLinesModel
import com.aait.ezakbus.ui.adapters.CityAdapter
import com.aait.ezakbus.ui.adapters.LineAdapter
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.activity_bus_lines.*
import kotlinx.android.synthetic.main.toolbar_bus.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class BusLinesActivity : ParentActivity() {
    private lateinit var mAdapter: LineAdapter
    lateinit var mCityAdapter:CityAdapter
    override val layoutResource: Int
        get() = R.layout.activity_bus_lines

    override fun init() {
        setToolbar(getString(R.string.bus_lines))
        setRec()
        sendRequestCities()
        setActions()
    }

    private fun setRec() {
        mCityAdapter=CityAdapter {
            hideAnimation(it.id!!)

        }
        mAdapter=LineAdapter()
        rec_cities.layoutManager=LinearLayoutManager(applicationContext)
        rec_cities.adapter=mCityAdapter

        rec_lines.layoutManager=LinearLayoutManager(applicationContext)
        rec_lines.adapter=mAdapter

    }

    private fun sendRequestLines(id: Int) {
        repo.getCityLines(id.toString(), mPrefs?.token!!)
            .subscribeWithLoading({showProgressDialog()},
                {handleLines(it)},{handleError(it)},{})
    }

    private fun handleLines(cityLinesModel: CityLinesModel) {
        hideProgressDialog()
        if (cityLinesModel.value=="1"){
            mAdapter.swapData(cityLinesModel.data!!)
        }
    }

    private fun setActions() {
        tv_city.onClick {
            if (trip_card.visibility== View.INVISIBLE){
                showCitiesDialog()
            }
        }
    }

    private fun sendRequestCities() {
        repo.getCities(mPrefs?.user?.country_id?:"2").subscribeWithLoading({
            showProgressDialog()
        },{handleData(it)},{handleError(it)},{})
    }

    private fun handleError(it: Throwable) {
        hideProgressDialog()
        toasty(it.message!!,2)
    }

    private fun handleData(it: CitiesModel) {
        hideProgressDialog()
        if (it.value=="1"){
            mCityAdapter.swapData(it.data)
            if (it.data.isNotEmpty()) {
                tv_city.text = it.data[0].name
                sendRequestLines(it.data[0].id!!)
            }
        }
    }

    private fun showCitiesDialog() {
        showAnimation()
    }

    private fun showAnimation() {
        //show card cities
        ObjectAnimator.
            ofFloat(trip_card,"translationY",trip_card.height.toFloat(),0F).also {
            it.duration=500
            trip_card.visibility=View.VISIBLE
            it.start()
            it.doOnEnd {

            }
        }
    }

    private fun hideAnimation(id: Int) {
        //hide card cities
        ObjectAnimator.
            ofFloat(trip_card,"translationY",0F,trip_card.height.toFloat()).also {
            it.duration=500

            it.start()
            it.doOnEnd {
                trip_card.visibility=View.INVISIBLE
                sendRequestLines(id)
            }
        }
    }


}