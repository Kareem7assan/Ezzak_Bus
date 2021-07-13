package com.aait.ezakbus.ui.activities.core

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.*
import androidx.annotation.NonNull
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.fcm.MessageService
import com.aait.ezakbus.models.captin_model.Data
import com.aait.ezakbus.models.client_later_model.Reason
import com.aait.ezakbus.network.remote_db.SocketConnection
import com.aait.ezakbus.ui.dialogs.RateDialog
import com.aait.ezakbus.utils.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.from_to_only.*
import kotlinx.android.synthetic.main.tracking_map.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONObject

class TrackingActivity : ParentActivity() , OnMapReadyCallback,
    GoogleMap.OnMapClickListener {

    override val layoutResource: Int
        get() = R.layout.tracking_map



    private var isAcceptedRide: Boolean = false
    private var captain_id: Int=0
    private var firstTime: Boolean = true
    private  var carMarker: MarkerOptions?=null
    private  var trip: Data? = null
    private var captainId: Int=0
    private  var startPosition: LatLng? = null
    private var marker: Marker? = null

    private var myLat=0.0
    private var myLng=0.0

    private var mOldlat: Double=0.0
    private var mOldlng: Double=0.0
    private var reasons: List<Reason> = mutableListOf()
    companion object{
        var isTrackingVisible: Boolean=false

    }
    private var orderId: Int=0

    private var mData: com.aait.ezakbus.models.order_details_model.Data?=null

    private var cap_phone: String?=""
    private var isEnableToBack: Boolean=false
    private var zoom=18f
    var address= MutableLiveData<String>()
    private val REQUEST_CODE_CH2: Int = 100
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var addressResultReceiver: LocationAddressResultReceiver

    private var mMap: GoogleMap?=null
    private var startLong:Double?=0.0
    private var startLat:Double?=0.0
    private var endLat:Double?=0.0
    private var endLng:Double?=0.0

    override fun init() {
        orderId = intent.getIntExtra("order_id",0)
        isAcceptedRide = intent.getBooleanExtra("isAcceptedRide",false)
        Log.e("order_id", orderId.toString()+","+isAcceptedRide)
        setMap()
        setLocationChange()
        if (orderId==0 || !isAcceptedRide){
           riple.visibility = View.VISIBLE
           content.visibility = View.VISIBLE
           riple.startRippleAnimation()
           coords.visibility=View.GONE
           bottom_sheet.visibility= View.GONE
           val layoutParams = scroll.layoutParams
            layoutParams.height= LinearLayoutCompat.LayoutParams.MATCH_PARENT
            scroll.layoutParams=layoutParams
        }
        else{
            coords.visibility=View.VISIBLE
            bottom_sheet.visibility= View.VISIBLE
            sendRequest(orderId.toString())
            val layoutParams = scroll.layoutParams
            layoutParams.height=850
            scroll.layoutParams=layoutParams
            riple.visibility = View.GONE
            content.visibility = View.GONE
        }

        setBehaviour()
        setActions()
    }

    private fun checkShowCaptain(){
        //zero or not accepted
        if (orderId==0) {
            riple.visibility = View.VISIBLE
            content.visibility = View.VISIBLE
            riple.startRippleAnimation()
            coords.visibility=View.GONE
            bottom_sheet.visibility= View.GONE
            val layoutParams = scroll.layoutParams
            layoutParams.height= LinearLayoutCompat.LayoutParams.MATCH_PARENT
            scroll.layoutParams=layoutParams

        }
        else{
            coords.visibility=View.VISIBLE
            bottom_sheet.visibility= View.VISIBLE
           // sendRequest(orderId.toString())
            val layoutParams = scroll.layoutParams
            layoutParams.height=850
            scroll.layoutParams=layoutParams
            riple.visibility = View.GONE
            content.visibility = View.GONE


        }
    }
    private fun setMap() {
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        //setToolbar(getString(R.string.my_trips))
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as (SupportMapFragment)
        mapFragment.getMapAsync(this)
        try {
            MapsInitializer.initialize(this)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override fun onResume() {
        super.onResume()
        startLocationUpdates()

    }


    private fun setLocationChange() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                myLat = locationResult.locations[0].latitude
                myLng = locationResult.locations[0].longitude
                if (firstTime) {
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(myLat, myLng), zoom))
                    firstTime=false
                }
            }

        }

    }



    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        setPermissionsLocation {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.requestLocationUpdates(
                LocationRequest(), locationCallback,
                null
            )
        }
    }



    private fun setActions() {

        from_title_loc.onClick {
            if (startLat!=0.0 && startLong!=0.0){
                forwardMap(startLat.toString(),startLong.toString())
            }
        }
        to_short_loc.onClick {
            if (endLat!=0.0 && endLng!=0.0){
                forwardMap(endLat.toString(),endLng.toString())
            }
        }
        cancel_trip.onClick {

            repo.cancelOrder(orderId.toString(),token = mPrefs?.token!!).subscribeWithLoading({
                showProgressDialog()
            },{
                hideProgressDialog()
                if (it.value=="1"){
                    toasty(it.msg.toString())
                    goHome()
                }
                else{
                    toasty(it.msg!!,2)
                }
            },{
                Log.e("error",it.message!!)
            },{
                hideProgressDialog()
            })
        }

        setPhone()


    }

    private fun goHome() {
        startActivity(Intent(this,MapPreviewActivity::class.java)).also {
            finish()
        }
    }

    private fun setPhone() {
        call_lay.onClick {
            setPermissionsPhone {
                Util.onCall(this@TrackingActivity,cap_phone)
            }
        }

        cancel_btn.onClick {
            showPopUpCat(orderId)
        }
    }

    private fun showPopUpCat(
        orderId: Int) {
        var reason: Reason?

        val myItems = reasons
        val dialog = MaterialDialog(this)
        dialog.title(R.string.cancel_reason)
        dialog.positiveButton(res = R.string.confirm)

        dialog.show {
            cornerRadius(16f)
            theme.applyStyle(R.style.AppTheme_Custom,true)
            listItemsSingleChoice(res = R.string.cancel_reason, items = myItems.map {
                it.reason.toString()
            })

            {dialog, indices, item ->
                val filter = reasons.filter {
                    item.toString() == it.reason
                }
                reason=filter[0]
                sendRequestCancel(reason,orderId)
            }
        }

    }



    private fun sendRequestCancel(
        reason: Reason? = null,
        orderId: Int)
    {
        addDisposable(repo.cancelOrder(orderId.toString(),reason?.id, mPrefs?.token!!)
            .subscribeWithLoading({
                showProgressDialog()
            },{
                if (it.value=="1"){
                    toasty(it.msg!!)
                }
                else{
                    toasty(it.msg!!,2)
                }
            }
                ,{
                    //  popup.dismiss()
                    hideProgressDialog()
                    Log.e("error",it.message)
                    toasty(it.message!!,2)
                },{
                    hideProgressDialog()
                    goHome()

                })
        )
    }




    private fun setChangeTrip() {
        MessageService.orderId_.observe(this, Observer {
            if (it>=orderId && orderId!=0) {
                orderId = it
                sendRequest(it.toString())
            }
        })

        MessageService.state_.observe(this, Observer{
            when(MessageService.state_.value){
                "finishSimpleOrder"->{
                    sendReqestFinished(orderId)
                }
                "ConfirmfinishSimpleOrder"->{
                    mData?.let {
                        showRatingDialog(it)
                    }

                }
                "startJourney"->{
                    cancel_lay.visibility= View.GONE
                    cancel_trip.visibility= View.GONE
                }
                "withdrawOrder"->{
                    cancel_lay.visibility= View.VISIBLE
                    cancel_trip.visibility= View.VISIBLE
                    val layoutParams = scroll.layoutParams
                    layoutParams.height= LinearLayoutCompat.LayoutParams.MATCH_PARENT
                    mMap?.clear()
                    isEnableToBack=false
                    SocketConnection.onClose()
                    marker=null
                    riple.startRippleAnimation()
                    riple.visibility = View.VISIBLE
                    content.visibility = View.VISIBLE
                    bottom_sheet.visibility= View.GONE
                    cancel_lay.visibility= View.VISIBLE
                    cancel_trip.visibility= View.VISIBLE
                }
                else->{
                    cancel_lay.visibility= View.VISIBLE
                    cancel_trip.visibility= View.VISIBLE
                }

            }
        })
    }





    private fun sendReqestFinished(orderId: Int) {
        repo.rideDetails(orderId,mPrefs?.token!!).subscribeWithLoading(
            {
                showProgressDialog()
            },{
                SocketConnection.onClose()
                if (it.value=="1"){
                    mData=it.data
                    mMap?.clear()
                    drawSrcDist()
                }
            },{
                SocketConnection.onClose()
                hideProgressDialog()
                toasty(getString(R.string.error_connection),2)
            },{
                cancel_trip.visibility= View.GONE
                cancel_lay.visibility= View.GONE
                hideProgressDialog()

            }
        )
    }



    private fun drawSrcDist() {
        addDisposable(repo.getRountes(
            "${mData!!.start_lat},${mData!!.start_long}",
            "${mData!!.end_lat},${mData!!.end_long}"
            ,mPrefs?.user!!.googlekey!!
        ).subscribeWithLoading({

        },{
            val routes = it.routes
            if(routes.isNotEmpty()) {
                val latLngList = MapUtils.getDirectionPolylines(routes)
                MapUtils.drawRouteOnMap(this,mMap!!,latLngList)
            }

        },{
            toasty(getString(R.string.error_connection),2)
            //Log.e("error",it.message)
        },{

        }))
    }


    private fun showRatingDialog(data: com.aait.ezakbus.models.order_details_model.Data) {
        RateDialog(data,{
            sendRequestRate(it)
        }) {
        //    goHome()
        }.also {
            it.show(supportFragmentManager,"rate")

        }

    }


    private fun sendRequestRate(rate: Float) {
        repo.rateUser(user_id=captain_id,rating = rate,token = mPrefs?.token!!)
            .subscribeWithLoading({
                showProgressDialog()
            },{

                if (it.value=="1"){
                    hideProgressDialog()

                }
                else{
                    toasty(it.msg!!,2)
                }
            },{
                // isEnableToBack=true
                // hideProgressDialog()
                Log.e("error",it.message!!)
                toasty(getString(R.string.error_connection),2)
            },{
                goHome()
            })
    }



    private fun sendRequest(orderId: String) {

        repo.captin_details(orderId.toInt(),mPrefs?.token!!).subscribeWithLoading({
            riple.visibility= View.GONE
            riple.stopRippleAnimation()
            content.visibility= View.GONE
            bottom_sheet.visibility= View.VISIBLE
        },{
            if (it.value=="1"){
                with(it.data){
                    cap_name.text=name
                    cap_rate.text=rate.toString()
                    cap_car_brand.text=carBrand
                    cap_car_plate.text=carNumber
                    cap_phone=phone
                    if (marker==null) {
                        setMarker(it.data)
                    }
                    trip_state.visibility= View.VISIBLE
                    checkShowCaptain()
                    when (it.data.captain_status) {
                        "captain_accept"->{
                            trip_state.text=getString(R.string.captain_accepted)

                        }

                        "captain_in_road"->{
                            trip_state.text=getString(R.string.captain_in_way)
                        }


                        "captain_arrived"->{
                            trip_state.text=getString(R.string.captain_arrived)
                        }


                        "start_journey"->{
                            trip_state.text=getString(R.string.ride_started)
                            cancel_trip.visibility= View.GONE
                            cancel_lay.visibility= View.GONE
                        }

                        "captain_finished"->{
                            trip_state.text=getString(R.string.ride_end)
                        }

                        else -> {
                            trip_state.visibility= View.GONE
                        }
                    }
                    Picasso.get().load(avatar!!).into(captain_img)
                    this@TrackingActivity.startLat = startLat
                    this@TrackingActivity.startLong = startLong
                    this@TrackingActivity.captain_id=captain_id
                    this@TrackingActivity.endLat = endLat
                    this@TrackingActivity.endLng = endLong
                    from_short_loc.text=startAddress

                    if (!endAddress.isNullOrBlank()){
                        to_short_loc.text=endAddress
                    }
                    payment_opt_lay.text=payment_type

                }
            }
        },{
            Log.e("error",it.message!!+",")
        },{
            setSocket()
            if (reasons.isEmpty()) {
                sendRequstReassons()
            }
        })
    }

    private fun setMarker(data: Data) {
        trip = data
        captainId = data.captain_id
        mOldlat=data.captain_lat!!
        mOldlng = data.captain_lng!!
        carMarker = MarkerOptions()
        startPosition = LatLng(mOldlat, mOldlng)
        carMarker!!.position(startPosition!!)
        carMarker!!.draggable(false)
        carMarker!!.flat(true)
        marker=mMap!!.addMarker(carMarker)
        marker!!.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car2))
        marker!!.setAnchor(0.5f,0.5f)
        mMap!!.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(startPosition!!)
                    .zoom(zoom)
                    .build()
            )
        )
        addTrack()

    }

    private fun addTrack() {
        val jsonObject = JSONObject()
        jsonObject.put("tracker_id", Gson().toJson(mPrefs?.user?.id))
        jsonObject.put("captain_id", Gson().toJson(captainId))
        Log.e("emmit", Gson().toJson(captainId)+","+ Gson().toJson(mPrefs?.user?.id))
        SocketConnection.addEvent("addtracker",jsonObject)

    }

    private fun sendRequstReassons() {
        repo.cancel(mPrefs?.token!!).subscribeWithLoading({

        },{
            if (it.value=="1") {
                reasons = it.data
            }
        },{
            Log.e("erorr",it.message)
        },{

        })
    }



    class LocationAddressResultReceiver internal constructor(val onResult :(
        title:String,
        result:String
    ) -> Unit,handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying")
            }
            if (resultCode == 1) {
                Log.e("address", "not found")
            }
            val title = resultData.getString("title")
            val currentAdd = resultData.getString("address")
            onResult(title!!, currentAdd!!)

        }

    }
    private fun setBehaviour() {
        var behavior = BottomSheetBehavior.from(bottom_sheet)

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) { // React to state change
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    ///fab.setVisibility(View.GONE)
                } else {
                    //fab.setVisibility(View.VISIBLE)
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) { // React to dragging events
                Log.e("onSlide", "onSlide")
            }
        })

        behavior.peekHeight = 600
        behavior.isHideable = false
    }


    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        MessageService.state_.removeObservers(this)
        MessageService.orderId_.removeObservers(this)
        SocketConnection.cancelEvent("trackorder")
        super.onDestroy()
    }

    override fun onStop() {
        isTrackingVisible=false
        super.onStop()
    }


    override fun onStart() {
        isTrackingVisible=true
        super.onStart()

    }

    private fun prepareMap() {
        mMap!!.uiSettings.isMapToolbarEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = false


        val isPermissionGranted = checkLocation(this)
        if (isGpsEnabled()) {
            if (!isPermissionGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_CODE_CH2
                )
            } else {
                getLocation()
            }
        } else {
            supportFragmentManager.showGpsAlert(this)
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mMap!!.apply {
            isMyLocationEnabled = true
            startLocationUpdates()

        }

    }


    override fun onMapReady(mMap_: GoogleMap?) {
        //  setMap()
       // setupAnim()
        mMap=mMap_
        prepareMap()

        setChangeTrip()
        mMap?.setOnCameraMoveListener {
            zoom=mMap?.cameraPosition!!.zoom
        }


    }



    private fun setSocket() {
        SocketConnection.mSocket.let {
            it!!.connect()
                .on("trackorder") { data ->
                    if (SocketConnection.mSocket!!.connected()) {
                        val data = data[0] as String
                        val car = JSONObject(data)
                        var captainId = car.getInt("captain_id")
                        Log.e("captain1_", captainId.toString())
                        if (car.getInt("captain_id")==this@TrackingActivity.captainId) {
                            Log.e("captain", captainId.toString())
                            val lat = car.getDouble("lat")
                            val lng = car.getDouble("lng")
                            startPosition = LatLng(lat, lng)
                            runOnUiThread {
                                Runnable {

                                }.run {
                                    MapUtils.rotateMarker(
                                        marker!!,
                                        MapUtils.bearingBetweenLocations(
                                            LatLng(mOldlat, mOldlng),
                                            LatLng(lat, lng)
                                        )
                                    )
                                    MapUtils.animateMarker(mMap!!, marker!!, LatLng(lat, lng), false, zoom)
                                    mOldlat = lat
                                    mOldlng = lng

                                }
                            }
                        }
                    }
                }
        }
    }
    override fun onMapClick(p0: LatLng?) {

    }




}
