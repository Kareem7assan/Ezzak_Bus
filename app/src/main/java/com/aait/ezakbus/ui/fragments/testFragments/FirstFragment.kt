package com.aait.ezakbus.ui.fragments.testFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aait.ezakbus.R
import com.aait.ezakbus.base.BaseFragment
import com.aait.ezakbus.listeners.OnBack
import com.aait.ezakbus.models.categories_model.Car
import com.aait.ezakbus.models.expecting_time_model.NearCar
import com.aait.ezakbus.network.remote_db.SocketConnection
import com.aait.ezakbus.ui.activities.bus.BusLinesActivity
import com.aait.ezakbus.ui.activities.core.TrackingActivity
import com.aait.ezakbus.ui.activities.map.AddressDetailsActivity
import com.aait.ezakbus.ui.activities.map.SearchPlacesActivity
import com.aait.ezakbus.ui.adapters.RidesAdapter
import com.aait.ezakbus.ui.dialogs.BottomSheetCard
import com.aait.ezakbus.ui.dialogs.BottomSheetCars
import com.aait.ezakbus.ui.dialogs.DateBottomSheet
import com.aait.ezakbus.ui.dialogs.MyAlertDialog
import com.aait.ezakbus.ui.fragments.bottom_nav.MyWallet
import com.aait.ezakbus.ui.fragments.navigation.NavFragment
import com.aait.ezakbus.utils.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.from_to_card.*
import kotlinx.android.synthetic.main.prev_map.*
import kotlinx.android.synthetic.main.prev_map.nav_view
import kotlinx.android.synthetic.main.price_plan.*
import kotlinx.android.synthetic.main.to_from_card.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import java.lang.Math.log

class FirstFragment : BaseFragment(), OnMapReadyCallback,OnBack{
    companion object{
        private var toAddress: String?=""
        private var fromAddress: String?=""
        private  var selected_car: Car?=null
    }

    private val FROM_SAVE: Int = 3000
    private var tripTime: String?=""
    private var capId: Int?=0
    var markers:MutableMap<NearCar,Marker> = mutableMapOf()
    private lateinit var startPosition: LatLng
    private  var carMarker: MarkerOptions?=null
    private var nearCaptains: MutableList<NearCar> = mutableListOf()
    private var isToAddress: Boolean = false
    private var isFromAddress: Boolean = false
    private var isUserLocationLoaded: Boolean=false
    private var userLocation: Location? = null
    private val SEARCH_PLACE_TO: Int = 111
    private val SEARCH_PLACE: Int = 11
    private var userLat:Double = 24.774265
    private var userLong :Double = 46.738586
    private var fetchedData: Boolean=false

    private var useBalanceFirst: Boolean=false
    private  var txt_name: TextView? = null

    private var isFirstLoad: Boolean=true
    private var car_type_id: Int=0
    private var expect_time: Int=1
    private lateinit var mAdapter: RidesAdapter
    private  var cars: List<Car> = listOf()
    private var  detailsAddress:String=""
    private var promCode:String=""
    private var fromSkip: Boolean=false
    private var fromInit: Boolean=true
    private var srcMarker: Marker? = null
    private var distMarker: Marker? = null
    private lateinit var state:  Actions
    private lateinit var currentState: MutableLiveData<String>
    private lateinit var addressResultReceiver: LocationAddressResultReceiver
    private var zoom: Float=18f
    private var mMap: GoogleMap? = null
    private val REQUEST_CODE_CH2: Int = 100
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var date:String?=null
    private var time_:String?=null
    private var time_type:String?="now"

    override fun onResume() {
        super.onResume()
       startLocationUpdates()
       sendRequestExpectedTime()

    }
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        setPermissionsLocation {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            fusedLocationClient.requestLocationUpdates(
                LocationRequest(), locationCallback,
                null
            )
        }
    }
    override val viewId: Int
        get() = R.layout.prev_map

    override fun onStart() {
        super.onStart()
        connectSocket()
    }

    override fun init(view: View) {


        currentState = MutableLiveData()
        currentState.observe(activity!!, Observer {
            when(it){
                Actions.INIT.name->{
                    if (!fromInit) {
                        animate(-1, 0f)
                        changeLayoutText()
                        hideMarkerHidePoint()
                        changeCameraPosition()
                        removeStartMarker()
                        
                    }
                    cars_lay.visibility=View.VISIBLE
                    fromInit=true
                }
                Actions.START_POINT.name-> {
                        if (fromInit) {
                            showStartAction()
                            fromInit=false
                        }
                        changeLayoutWeight()
                        changeLayoutText()
                        enableZoom()

                }
                Actions.END_POINT.name  ->  {
                        showEndPointAction()
                        showMarkerBounds()
                        changeLayoutText()
                        showPriceCard()
                }
                Actions.SKIP.name->{
                    createStartPointMarker()
                    showMarkerBounds()
                    changeLayoutText()
                    hideMarkerHidePoint()
                    emptyDest()
                    showPriceCard()
                }
                Actions.END_POINT_BACK.name->{
                    distMarker?.remove()
                    state=Actions.START_POINT
                    currentState.value=state.name
                    showPriceCard()
                }
                Actions.START_POINT_BACK.name->{
                  //TODO
                    /*
                    * 1- change bounds
                    * 2-remove marker
                    * 3-show hide marker destination
                    * 4- change camera to position of marker
                    * */
                    //enable zoom
                    enableZoom()
                    changeCameraPosition()
                    removeDistMarker()
                    changeLayoutWeight()
                    changeLayoutText()
                }





                Actions.GO.name -> {
                    changeLayoutText()
                    goTrackingPage()}

            }
        })
    }


    private fun removeDistMarker() {
        distMarker.let {
            it?.remove()
            //3-show hide marker destination
            showMarkerHidePoint()
        }
    }

    private fun changeCameraPosition() {
        if (state==Actions.INIT) {
          changeCamera(srcMarker?.position?.latitude ?: distMarker!!.position!!.latitude,
              srcMarker?.position?.longitude ?: distMarker!!.position!!.longitude)
        }
        else {
            changeCamera(distMarker?.position?.latitude ?: srcMarker!!.position!!.latitude,
                distMarker?.position?.longitude ?: srcMarker!!.position!!.longitude)

        }
    }
    private fun changeCamera(lat:Double,lng:Double){
        val cu = CameraUpdateFactory.newLatLngZoom(
            LatLng(
                lat
                ,
                lng
            ), 17f
        )
        mMap!!.animateCamera(cu)
    }
    private fun removeStartMarker() {
        srcMarker?.remove()
        img_lay.visibility=View.VISIBLE
    }


    private fun emptyDest() {
        to_title_loc.text=""
        to_short_loc.text=""
        val p =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        p.weight = 1.0f
        to_title_loc.layoutParams=p
        p.weight = 2.0f
        to_short_loc.layoutParams = p
    }

    private fun showPriceCard() {
        price_card.visibility=View.VISIBLE
        car_card.visibility=View.GONE
        setPriceCard()
        sendRequestCars(true)

    }

    private fun setPriceCard() {
        car_name.text=selected_car?.name
        payment_lay.onClick {
                MyWallet(rest.text.toString(),useBalanceFirst) {
                        sendRequestChange(it)
                }.also {
                    it.show(fragmentManager!!,"frag")
                }
        }

        if (selected_car?.expectedPrice?.isEmpty()!! || selected_car?.expectedPrice=="0") {
            balance_hint.visibility=View.VISIBLE
            price_trip_shimmer.visibility=View.GONE
            balance.text=""
        }
        else{
            balance.text = selected_car?.expectedPrice+" "+selected_car?.currency
            balance_hint.visibility=View.GONE
            price_trip_shimmer.visibility=View.VISIBLE
        }
        Picasso.get().load(selected_car?.image).into(car_img)

        cars_price_lay.onClick {
            BottomSheetCars(cars) {
                selected_car=it
                car_name.text=it.name
                car_type_id=it.id!!
                if (it.expectedPrice!!.isEmpty() || it.expectedPrice=="0") {
                    balance_hint.visibility=View.VISIBLE
                    price_trip_shimmer.visibility=View.GONE
                    balance.text=""
                }
                else{
                    balance.text = it.expectedPrice+" "+it.currency
                    balance_hint.visibility=View.GONE
                    price_trip_shimmer.visibility=View.VISIBLE
                }
                sendRequestExpectedTime()
                val lat=srcMarker!!.position.latitude
                val lng=srcMarker!!.position.longitude
                srcMarker?.remove()
                createStartPointMarker(lat,lng)
                Picasso.get().load(it.image).into(car_img)

                car_type.text=it.name
                Picasso.get().load(it.image).into(car)

            }.also {
                it.show(fragmentManager!!,"show2")
            }

        }
    }

    private fun enableZoom() {
        mMap!!.uiSettings.setAllGesturesEnabled(true)
        mMap!!.uiSettings.isCompassEnabled=true
    }
    private fun showMarkerBounds() {
        val builder = LatLngBounds.Builder()
        if (distMarker==null) {
            builder.include(srcMarker!!.position)
            val bounds = builder.build()
            val padding = 0
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap!!.animateCamera(cu)
        }
       else {
            zoomToFitMarkers(listOf(srcMarker!!,distMarker!!))
        }
    }

        private fun midPoint(lat1:Double,long1:Double, lat2:Double,long2:Double):LatLng{
            return  LatLng((lat1+lat2)/2, (long1+long2)/2)
        }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setMap()
        setNav()
        setAddressTittle()
        cars_lay.visibility=View.VISIBLE
        setLocationChange()
        setActions()

    }

    private fun connectSocket() {
        SocketConnection
            .onListen("trackorder") {isConnected,data->
                if (isConnected){
                    Log.e("sig",isConnected.toString()+","+Gson().toJson(data))
                    val data = data[0] as String
                    val car = JSONObject(data)
                    val captainId = car.getInt("captain_id")
                    val lat = car.getDouble("lat")
                    val lng = car.getDouble("lng")
                    activity?.runOnUiThread {
                        Runnable {

                        }.run{
                            val map = nearCaptains.map {
                                Log.e("sig2",isConnected.toString()+","+Gson().toJson(data))
                                if (it.id == captainId) {
                                    Log.e("mark", markers.keys.toString() + "," + it.id.toString())
                                    markers.map {markers->
                                        if (markers.key.id == captainId){
                                            MapUtils.rotateMarker(
                                                markers.value,
                                                MapUtils.bearingBetweenLocations(
                                                    LatLng(markers.key.lat, markers.key.lng),
                                                    LatLng(lat, lng)
                                                )
                                            )
                                            MapUtils.animateMarker(
                                                mMap!!,
                                                markers.value,
                                                LatLng(lat, lng),
                                                false,
                                                0f
                                            )
                                        }

                                        markers.key.lat = lat
                                        markers.key.lng = lng

                                    }

                                }
                                it
                            }
                            nearCaptains.clear()
                            nearCaptains.addAll(map)
                            Log.e("sizzze",cars.size.toString()+","+markers.size)

                        }
                    }


                }
            }
    }
    private fun setActions() {
        setCalenderAction()
        setNextBtnAction()
        setSkipAction()
        setCalenderAction()
        setFilter()
        setDetails()
        setPromo()
        setLocationMap()
        show_menu()
        setActionBus()
        savePlaceAction()
    }

    private fun savePlaceAction() {
        from_heart.onClick {
            startActivityForResult(Intent(activity!!,AddressDetailsActivity::class.java).apply {
                putExtra("from_home",true)
                putExtra("lat",lastLocation?.latitude?:0.0)
                putExtra("lng",lastLocation?.longitude)
                putExtra("address",from_short_loc.text.toString())
                putExtra("name",from_title_loc.text.toString())
            },FROM_SAVE)
        }
    }

    private fun setActionBus() {
        bus_lay.onClick {
            startActivity(Intent(context, BusLinesActivity::class.java))
        }
    }

    fun show_menu() {
        menu_iv.onClick {
            if (drawer_layout != null) {
                if (mPrefs?.lang.equals("ar")) {
                    if (drawer_layout.isDrawerOpen(nav_view)) {
                        drawer_layout.closeDrawer(nav_view)
                    } else {
                        if (drawer_layout != null) {
                            drawer_layout.openDrawer(nav_view)

                        }
                    }
                }
                //left
                else {
                    if (drawer_layout.isDrawerOpen(nav_view)) {
                        drawer_layout.closeDrawer(nav_view)
                    } else {
                        drawer_layout.openDrawer(nav_view)
                    }
                }


            }
        }
    }

    private fun setNav() {
        if (drawer_layout != null) {
            if (drawer_layout.isDrawerOpen(Gravity.RIGHT)) {
                drawer_layout.closeDrawer(Gravity.RIGHT)
            } else if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
                drawer_layout.closeDrawer(Gravity.LEFT)
            }
            val fragmentManager = activity?.supportFragmentManager
            val fragment = NavFragment()
            fragmentManager?.beginTransaction()!!.replace(R.id.nav_view, fragment).commit()

        }
    }
    /*private fun setDialogListerer() {
        MessageService.state_.observe(this, Observer{
            when(MessageService.state_.value){
                "ConfirmfinishSimpleOrder"->sendReqestFinished(MessageService.orderId_.value!!)


            }
        })

    }
    */


    private fun setLocationMap() {
        from_lay.onClick {
            Log.e("states",currentState.value.toString())
            if (currentState.value == Actions.END_POINT.name || currentState.value == Actions.END_POINT_BACK.name) {
                 //replace marker

                val intent = Intent(activity!!, SearchPlacesActivity::class.java)
                intent.putExtra("lat", userLat)
                intent.putExtra("lng", userLong)
                intent.putExtra("from", true)
                startActivityForResult(intent, SEARCH_PLACE)

            }
            else if (currentState.value==Actions.INIT.name){
                val intent = Intent(activity!!, SearchPlacesActivity::class.java)
                intent.putExtra("lat", userLat)
                intent.putExtra("lng", userLong)
                intent.putExtra("from", true)
                startActivityForResult(intent, SEARCH_PLACE)
            }
            else {
                state=Actions.INIT
                currentState.value=state.name
            }
        }


        dist_loc.onClick {
             if (currentState.value==Actions.START_POINT.name || currentState.value==Actions.START_POINT_BACK.name
                 ||currentState.value==Actions.END_POINT.name || currentState.value==Actions.END_POINT_BACK.name
             ){
                val intent = Intent(activity!!, SearchPlacesActivity::class.java)
                intent.putExtra("lat", userLat)
                intent.putExtra("lng", userLong)
                intent.putExtra("from", false)
                startActivityForResult(intent, SEARCH_PLACE_TO)
            }
            else {
                 state=Actions.START_POINT
                 currentState.value=state.name
            }
        }

        balance_hint.onClick {
            val intent = Intent(activity!!, SearchPlacesActivity::class.java)
            intent.putExtra("lat", userLat)
            intent.putExtra("lng", userLong)
            intent.putExtra("from", false)
            startActivityForResult(intent, SEARCH_PLACE_TO)
        }
    }

    private fun setDetails() {
        details_lay_start.onClick {
            val bottomSheetCard = BottomSheetCard(getString(R.string.enter_details_address)){code,btn->
                detailsAddress=code
            }
            bottomSheetCard.show(fragmentManager!!,"asd")
        }
    }
    private fun setPromo() {
        promo_hint.onClick {
            val bottomSheetCard = BottomSheetCard(getString(R.string.enter_promo_code)){code,btn->
                promCode=code
            }
            bottomSheetCard.show(fragmentManager!!,"asd")
        }
    }

    private fun setFilter() {
        cars_lay.onClick {
           // isFirstLoad=true
            BottomSheetCars(cars) {selected_car_->
                car_type_id= selected_car_.id!!
                car_type.text=selected_car_.name
                selected_car=selected_car_

                Picasso.get().load(selected_car_.image).into(car)
                sendRequestExpectedTime()
            }.also {
                it.show(fragmentManager!!,"bottom")
            }
        }
    }


private fun zoomToFitMarkers(markers:List<Marker>) {
		val b =  LatLngBounds.Builder()
    for ( m in markers) {
			b.include(m.position)
		}
		val bounds = b.build()
            val padding = ((activity!!.windowManager.defaultDisplay.width * 10) / 100); // offset from edges of the map

		val cu = CameraUpdateFactory
				.newLatLngBounds(bounds, padding)

		mMap?.animateCamera(cu)

	}

    private  fun getMapRadius():Int {
    val latlng = mMap?.projection!!.visibleRegion.latLngBounds.center
        val latLng1 = mMap!!.projection.visibleRegion.farRight
        val loc=Location("")
        loc.latitude=latlng.latitude
        loc.longitude=latlng.longitude
        val loc2=Location("")
        loc2.latitude=latLng1.latitude
        loc2.longitude=latLng1.longitude
        val distanceTo = loc.distanceTo(loc2)
        return distanceTo.toInt()
}

 private fun getZoomLevel(radius:Double):Int{
            var scale = radius / 5000f
            return ( (16 - log(scale) / log(2.0))).toInt()
}

    private fun sendRequestChange(isChecked:Boolean){
        repo.useBalance(isChecked,mPrefs.token!!).subscribeWithLoading({
        },{
            if (it.value=="1"){
                useBalanceFirst= it.data?.useBalanceFirst?.toBoolean()!!
            }
            else{
                activity!!.toasty(it.msg!!,2)
            }
        },{
            //   hideProgressDialog()
            activity!!.toasty(getString(R.string.error_connection),2)
            Log.e("error",it.message!!)
        },{
            // hideProgressDialog()
        })
    }
    private fun setSkipAction() {
        skip_btn.onClick {
            state=Actions.SKIP
            fromSkip=true
            distMarker=null
            currentState.value = state.name
        }
    }

    private fun setCalenderAction() {
        calendar.onClick {
            DateBottomSheet{ date: String, time: String, s2: String? ->
            this@FirstFragment.date=date
            this@FirstFragment.time_=time
            this@FirstFragment.time_type="later"
            }.show(fragmentManager!!,"")
        }

    }
    fun replaceArabicNumbers( original:String?):String {
        var res=original!!.replace("١","1")
            .replace("٢","2")
            .replace("٣","3")
            .replace("٤","4")
            .replace("٥","5")
            .replace("٦","6")
            .replace("٧","7")
            .replace("٨","8")
            .replace("٩","9")
            .replace("٠","0")

        return res
    }

    private fun setNextBtnAction() {
        next_btn.onClick {
            Log.e("current_state",state.name)
            if (state==Actions.START_POINT &&to_title_loc.text.isEmpty()) {
                /*
                TODO *go to address*
                */
                toast(getString(R.string.empty_address))
            }
            else {
                when (state) {
                    Actions.INIT -> state = Actions.START_POINT
                    Actions.START_POINT -> state = Actions.END_POINT
                    Actions.SKIP -> state = Actions.GO
                    Actions.START_POINT_BACK->state=Actions.END_POINT
                    Actions.END_POINT -> state = Actions.GO
                    Actions.GO -> state = Actions.END_POINT
                    else -> Log.e("state", state.name)
                }
                currentState.value = state.name
                Log.e("current_state",state.name)
            }
        }
    }


    private fun changeLayoutText(){
        if (state==Actions.START_POINT || state==Actions.START_POINT_BACK){
            tv_toolbar.visibility=View.VISIBLE
            tv_toolbar.text = getString(R.string.detect_destination)
            next_btn.text = getString(R.string.detect_destination)
            skip_btn.visibility = View.VISIBLE
            calendar.visibility = View.GONE
            price_card.visibility=View.GONE
            cars_lay.visibility=View.GONE
            car_card.visibility=View.VISIBLE

    }
        else if (state==Actions.END_POINT || state==Actions.SKIP){
            skip_btn.visibility = View.GONE
            car_card.visibility=View.GONE
            calendar.visibility = View.GONE
            tv_toolbar.visibility=View.GONE
            cars_lay.visibility=View.GONE
            next_btn.text = getString(R.string.yalla)
        }
        else if (state==Actions.INIT){
            tv_toolbar.visibility=View.VISIBLE
            tv_toolbar.text = getString(R.string.detect_start_point)
            next_btn.text=getString(R.string.next)
            cars_lay.visibility=View.VISIBLE
            calendar.visibility=View.VISIBLE
            skip_btn.visibility=View.GONE
        }
        else if (state==Actions.GO){
            distMarker?.remove()
            cars_lay.visibility=View.GONE
        }
    }

    private fun goTrackingPage() {
        if (date!=null){
            date=replaceArabicNumbers(date)
            Log.e("date",date.toString()+","+time_.toString())
        }


        repo.createTrip(
            selected_car?.expectedPrice?:"",
            tripTime!!,selected_car?.priceId.toString(),from_short_loc.text.toString(),srcMarker!!.position.latitude.toString(),srcMarker!!.position.longitude.toString(),
            to_short_loc?.text?.toString(),distMarker?.position?.latitude?.toString(),distMarker?.position?.longitude?.toString(),car_type_id.toString(),"cash",
            time_type,date,time_,promCode,detailsAddress,mPrefs.token!!
            )
            .subscribeWithLoading({
                showProgressDialog()
            },{
                hideProgressDialog()
                if (it.value=="1"){
                    //current
                    if (date==null){
                        activity?.startActivity(Intent(activity,TrackingActivity::class.java).putExtra("order_id",it.data?.order_id!!))
                            .also {
                                activity?.finish()
                            }

                        var intent=Intent(activity,TrackingActivity::class.java)
                        intent.putExtra("order_id",it.data?.order_id!!)
                        intent.putExtra("isAcceptedRide",false)
                        activity?.startActivity(intent)
                        activity?.finish()
                    }
                    else{
                        activity!!.toasty(getString(R.string.sent_journey_waiting_accepting))
                }
                }
                else{
                    activity?.toasty(it.msg,2)
                }

            },{
                hideProgressDialog()
                activity?.toasty(getString(R.string.error_connection),2)
            },{
                hideProgressDialog()

            })


    }


    private fun showEndPointAction() {
        showDestinationMarker()
        hideMarkerHidePoint()

    }

    private fun showStartAction() {
        animate(1,0f)
        animateDistPoint()
        createStartPointMarker()
        img_lay.visibility=View.INVISIBLE
        dist.visibility=View.VISIBLE
        dist.animate().translationYBy(50f).start()

    }

    private fun animateDistPoint() {
        val myAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_anim)
        end_point.startAnimation(myAnim)
     /*   Handler().postDelayed({
                end_point.stop
            }
            ,3000)*/

    }


    private fun setLocationChange() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                 userLat = locationResult.locations[0].latitude
                 userLong = locationResult.locations[0].longitude
                userLocation=locationResult.locations[0]
                if (!isUserLocationLoaded){
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(userLat, userLong), zoom))
                //obtain address

                startIntentService()
                isUserLocationLoaded=true
            }

            }

        }

    }

    private fun setAddressTittle() {
        //class to handle response of Address
        addressResultReceiver = LocationAddressResultReceiver({title_,address->
            when (state) {

                Actions.INIT -> {
                        from_title_loc.text=title_
                        from_short_loc.text=address
                }
                Actions.START_POINT -> {
                    to_title_loc.text=title_
                    to_short_loc.text=address
                   /* if (isFromAddress){
                        isFromAddress=false
                    }*/
                }
                Actions.START_POINT_BACK -> {
                    if (!fromSkip) {
                        to_title_loc.text = title_
                        to_short_loc.text = address
                        /*if (isFromAddress){
                            isFromAddress=false
                        }
                        if (isToAddress){
                            isToAddress=false
                        }*/
                    }
                    else{
                        to_title_loc.text = ""
                        to_short_loc.text = ""
                        fromSkip=false
                    }
                }
                Actions.END_POINT -> {
                    to_title_loc.text = title_
                    to_short_loc.text = address
                }


                else -> {
                  /*  if (isFromAddress) {
                        from_title_loc.text=title_
                        from_short_loc.text=address
                        isFromAddress=false
                    }
                    else if (isToAddress){
                        to_title_loc.text = title_
                        to_short_loc.text = address
                        isToAddress=false
                    }*/
                    }
            }

        },Handler())
    }



    private fun changeLayoutWeight() {
        val p =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        p.weight = 1.0f
        to_title_loc.layoutParams=p
        to_short_loc.layoutParams = p
    }

    private fun showDestinationMarker(){
        img_lay.visibility=View.INVISIBLE
        dist.visibility=View.INVISIBLE
        point.visibility=View.INVISIBLE
        createDistPointMarker()
    }

    private fun createStartPointMarker() {
      mMap?.clear()
      srcMarker=mMap!!.addMarker(
            MarkerOptions().position(LatLng(lastLocation!!.latitude,lastLocation!!.longitude)).icon(
                BitmapDescriptorFactory.fromBitmap(
                    createCustomMarker(activity!!,expect_time.toString())
                )
            )
        ).also {
          it.title = ""
      }


    }
    private fun createStartPointMarker(lat: Double,lng: Double) {
      srcMarker=mMap!!.addMarker(
            MarkerOptions().position(LatLng(lat,lng)).icon(
                BitmapDescriptorFactory.fromBitmap(
                    createCustomMarker(activity!!,expect_time.toString())
                )
            )
        ).also {
          it.title = ""
      }

        if (nearCaptains.isNotEmpty()){
            nearCaptains.forEach {
                createMarker(it.lat,it.lng,it)
            }
        }


    }

    private fun createDistPointMarker() {
            distMarker = mMap!!.addMarker(
                MarkerOptions().position(
                    LatLng(
                        lastLocation!!.latitude,
                        lastLocation!!.longitude
                    )
                ).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarkerDest(activity!!, expect_time.toString())
                    )
                )
            ).also {
                it.title = ""
            }
    }

    private fun setCameraListener(){
        mMap!!.setOnCameraIdleListener {
                    val location = Location("")
                    location.latitude = mMap!!.cameraPosition.target.latitude
                    location . longitude = mMap !!. cameraPosition . target . longitude
                    lastLocation = location
                    if (Actions.START_POINT == state || Actions.START_POINT_BACK == state) {
                        showMarkerHidePoint()
                        }
                    if (Actions.INIT == state) {
                        sendRequestExpectedTime()
                    }
                    Log.e("title_state",isFromAddress.toString()+","+isToAddress)
                     /*   if (!isFromAddress&&!isToAddress) {
                            startIntentService()
                        }*/
            when{
                !isFromAddress && !isToAddress && state!=Actions.END_POINT && state!=Actions.SKIP-> startIntentService()
                isFromAddress->isFromAddress=false
                isToAddress->isToAddress=false
            }

        }
    }

    private fun showMarkerHidePoint() {
        dist.visibility=View.VISIBLE
        point.visibility=View.INVISIBLE
    }
    private fun hideMarkerHidePoint() {
        dist.visibility=View.INVISIBLE
        point.visibility=View.INVISIBLE
    }

    private fun createCustomMarker(context: Context, name: String?): Bitmap {
        val marker: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.start_custom_marker_layout,
                null
            )
        txt_name = marker.findViewById<View>(R.id.period_txt) as TextView
        txt_name?.text=name
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = LayoutParams(52, LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap
    }

    fun createCustomMarkerDest(context: Context,name: String?): Bitmap {
        val marker: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.end_custom_marker_layout,
                null
            )
        val txt_name = marker.findViewById<View>(R.id.period_txt) as TextView
        txt_name.text=name
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = LayoutParams(52, LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap
    }


    private var lastLocation: Location? = null


    private fun startIntentService() {
        val intent = Intent(activity, LocationAddress::class.java).apply {
            putExtra("add_receiver", addressResultReceiver)
            putExtra("add_location", lastLocation)
        }
        activity!!.startService(intent)
    }

    private fun animate(direction: Int, alpha: Float) {
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val flingX = FlingAnimation(dist_loc, DynamicAnimation.TRANSLATION_Y)
            .setStartVelocity(direction * 280f)
            .addEndListener { animation, canceled, value, velocity ->
                if (direction>0){
                    point_seprator.visibility=View.VISIBLE
                    from_heart.visibility=View.GONE
                    details_lay_start.visibility=View.VISIBLE
                }
                else{
                    point_seprator.visibility=View.INVISIBLE
                    from_heart.visibility=View.VISIBLE
                    details_lay_start.visibility=View.GONE
                }
            }.addUpdateListener { animation, value, velocity ->
                if (direction > 0) {
                    point_seprator.visibility = View.VISIBLE
                }
            }
            .setFriction(0.5f)
        flingX.start()
    }

    override fun onMapReady(gMap: GoogleMap?) {
        mMap=gMap
        //sendRequestExpectedTime()

        setInitState()
        prepareMap()
        mMap?.setOnCameraMoveStartedListener {reason->
               when (reason) {
                    //The user gestured on the map
                    GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                        if (state==Actions.START_POINT||state==Actions.START_POINT_BACK){
                            point.visibility=View.VISIBLE
                            dist.visibility=View.INVISIBLE

                        }
                }
            }
        }

        setCameraListener()
}

    private fun setInitState() {

        state=Actions.INIT
        fromInit=true
    }


    private fun sendRequestExpectedTime() {
        repo.getExpectedTime(lastLocation?.latitude.toString(),lastLocation?.longitude.toString(),car_type_id,mPrefs.token!!).subscribeWithLoading(
            {
                load_prog.visibility=View.VISIBLE
                showHideAllShimmer(true)
                period.visibility=View.GONE
            },{
                if (it.value=="1"){
                    load_prog.visibility=View.GONE
                    period.visibility=View.VISIBLE
                    expect_time=it.data?.time!!
                    shimmer_time.visibility=View.GONE
                    period_txt_.text=expect_time.toString()
                    time.visibility=View.VISIBLE
                    time.text=expect_time.toString()+" "+getString(R.string.mins)
                    if (nearCaptains.isEmpty()){
                        nearCaptains= it.data?.captains as MutableList<NearCar>
                        nearCaptains.forEach {
                            createMarker(it.lat,it.lng,it)
                        }

                    }



                }
            },{
                if (context!=null) {
                    load_prog.visibility = View.GONE
                    period.visibility = View.VISIBLE
                    Log.e("error", it.message)
                }
            },{
                load_prog.visibility=View.GONE
                period.visibility=View.VISIBLE
                showHideAllShimmer(false)
                if (isFirstLoad) {
                    sendRequestCars()
                }
            }
        )
    }

    private fun createMarker(
        lat: Double,
        lng: Double,
        car: NearCar)
    {
        addTracker(car.id)
        //create marker
        carMarker = MarkerOptions()
        Log.e("latLng",lat.toString()+","+lng)
        startPosition = LatLng(lat, lng)
        carMarker!!.position(startPosition)
        carMarker!!.draggable(false)
        carMarker!!.flat(true)
        carMarker!!.anchor(0.5f,0.5f)
        val marker=mMap?.addMarker(carMarker)
        marker?.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car2))
        markers[car] = marker!!
        Log.e("markers",markers.size.toString()+" ,")
    }

    private fun addTracker(trackerId:Int) {
        val jsonObject = JSONObject()
        jsonObject.put("tracker_id",Gson().toJson(mPrefs!!.user?.id))
        jsonObject.put("captain_id",Gson().toJson(trackerId))
        Log.e("tracker","added")
        SocketConnection.addEvent("addtracker",jsonObject)
    }


    private fun sendRequestCars(from_card:Boolean?=false) {
        repo.getCarTypes(srcMarker?.position?.latitude.toString(),
            srcMarker?.position?.longitude.toString(),
            distMarker?.position?.latitude?.toString(),
            distMarker?.position?.longitude?.toString(),
            mPrefs.token!!).subscribeWithLoading({
            showHideAllShimmer(true)
        },{
            if (it.value=="1"){
                Log.e("cars","cars")

                rest.text=it.data!!.balance
                useBalanceFirst=it.data!!.use_balance_first
                cars=it.data!!.cars
                tripTime=it.data!!.time
                if (from_card!!){
                    selected_car=cars.find {
                        it.id == selected_car?.id
                    }
                    setPriceCard()
                }
                else{
                    period_txt_.text=expect_time.toString()
                    cars = it.data?.cars!!
                    car_type.text=cars[0].name
                    car_type_id= cars[0].id!!
                    selected_car= it.data?.cars?.get(0)
                    Picasso.get().load(cars[0].image).into(car)
                }
            }
            else{
                activity!!.toasty(it.msg.toString(),2)
            }
        },{
            Log.e("error",it.message)
            activity!!.toasty(getString(R.string.error_connection),2)
        },{

            isFirstLoad=false
            showHideAllShimmer(false)
        }
        )
    }
    private fun showHideAllShimmer( isShow:Boolean){
        if (isShow){
            shimmer_time.visibility=View.VISIBLE
            if (isFirstLoad) {
                shimmer_car.visibility = View.VISIBLE
                car_lay.visibility = View.GONE
                shimmer_car.startShimmer()
            }
            time.visibility=View.GONE
            shimmer_time.visibility=View.VISIBLE
            shimmer_time.startShimmer()
            price_trip_shimmer.startShimmer()
            balance_shimmer.visibility=View.VISIBLE
            balance_shimmer.startShimmer()
            rest.visibility=View.GONE
        }
        else{
            shimmer_time.stopShimmer()
            shimmer_car.stopShimmer()
            price_trip_shimmer.stopShimmer()
            price_trip_shimmer.visibility=View.GONE
            shimmer_time.visibility=View.GONE
            shimmer_car.visibility=View.GONE
            car_lay.visibility=View.VISIBLE
            time.visibility=View.VISIBLE
            balance_shimmer.visibility=View.GONE
            balance_shimmer.stopShimmer()
            rest.visibility=View.VISIBLE
        }
    }

    private fun setMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    private fun prepareMap() {
        mMap!!.uiSettings.isMapToolbarEnabled=true
        mMap!!.uiSettings.isMyLocationButtonEnabled=false

        val isPermissionGranted = checkLocation(activity!!)
        if (activity!!.isGpsEnabled()) {
            if (!isPermissionGranted) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    REQUEST_CODE_CH2
                )
            }
            else {
                getLocation()
            }
        }
        else {
          //  activity!!.supportFragmentManager.showGpsAlert(activity!!)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(userLat, userLong), zoom))
            val dialog = MyAlertDialog{
                if (it==1){
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),9000)
                }
            }
            dialog.show(activity!!.supportFragmentManager,"logout")
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mMap!!.apply {
            isMyLocationEnabled=true
            startLocationUpdates()
        }

    }


      class LocationAddressResultReceiver internal constructor(val onResult:(title:String,result:String)->Unit,handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying")
            }
            if (resultCode == 1) {
                Log.e("address","not found")
                }
                val title = resultData.getString("title")
                val currentAdd = resultData.getString("address")
                onResult(title!!,currentAdd!!)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when{
            requestCode==9000 ->{
                getLocation()
            }
            requestCode==SEARCH_PLACE && resultCode==SearchPlacesActivity.RESULT_SEARCH -> {
                val from_lat = data!!.getDoubleExtra("lat", 0.toDouble())
                val from_lng = data.getDoubleExtra("lng", 0.toDouble())
                fromAddress= data.getStringExtra("address")
                val name= data.getStringExtra("name")

                lastLocation=Location("").also {
                    it.latitude=from_lat
                    it.longitude=from_lng

                }
                from_title_loc.text=name
                from_short_loc.text= fromAddress
                isFromAddress=true
                if (currentState.value == Actions.END_POINT.name || currentState.value == Actions.END_POINT_BACK.name) {
                    //replace marker
                    Log.e("replace","1")
                    srcMarker?.remove()
                    createStartPointMarker(from_lat,from_lng)
                    showMarkerBounds()
                    showPriceCard()

                }
                else{
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(from_lat, from_lng), zoom))


                }
            }
            requestCode==FROM_SAVE && resultCode==Activity.RESULT_OK-> {

            }
            requestCode==SEARCH_PLACE_TO && resultCode==SearchPlacesActivity.RESULT_SEARCH-> {
                val to_lat = data!!.getDoubleExtra("lat", 0.toDouble())
                val to_lng = data.getDoubleExtra("lng", 0.toDouble())
                val name = data.getStringExtra("name")
                toAddress= data.getStringExtra("address")

                lastLocation=Location("").also {
                    it.latitude=to_lat
                    it.longitude=to_lng

                }
                to_title_loc.text=name
                to_short_loc.text= toAddress
                isToAddress=true
                if (currentState.value==Actions.END_POINT.name || currentState.value==Actions.END_POINT_BACK.name||currentState.value==Actions.SKIP.name) {
                    //replace marker
                    distMarker?.remove()
                    createDistPointMarker()
                    showMarkerBounds()
                    showPriceCard()

                }

                else{
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(to_lat, to_lng), zoom))


                }


            }
        }
    }

    override fun onBackPress(): Boolean {

        state = when(currentState.value){
            Actions.GO.name-> Actions.START_POINT_BACK
            Actions.END_POINT.name-> Actions.START_POINT_BACK
            Actions.SKIP.name-> Actions.START_POINT_BACK
            Actions.START_POINT_BACK.name-> Actions.INIT
            Actions.END_POINT_BACK.name-> Actions.START_POINT
            Actions.START_POINT.name-> Actions.INIT
            else->return false
        }
        currentState.value=state.name
        return true
    }


}


