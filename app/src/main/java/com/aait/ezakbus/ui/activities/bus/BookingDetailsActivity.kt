package com.aait.ezakbus.ui.activities.bus

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.ticket_model.TicketModel
import com.aait.ezakbus.utils.MapUtils.drawRouteOnMap
import com.aait.ezakbus.utils.MapUtils.getDirectionPolylines
import com.aait.ezakbus.utils.Util
import com.aait.ezakbus.utils.toasty
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_booking.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class BookingDetailsActivity : ParentActivity(), OnMapReadyCallback {
    private var dist_address: String?=""
    private var src_address: String?=""
    private var from_lat: String? = null
    private var from_lng: String? = null
    private var to_lat: String? = null
    private var to_lng: String? = null
    private var captain_phone: String=""
    private var ticketNum: String=""
    override val layoutResource: Int
        get() = R.layout.activity_booking

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var userLat:MutableLiveData<Double> = MutableLiveData()
    private var userLong :MutableLiveData<Double> = MutableLiveData()
    private var isFirst=true
    private var orderId="0"
    private var mMap: GoogleMap? = null

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }
    override fun onMapReady(gMap: GoogleMap?) {
        mMap=gMap


    }
    override fun init() {
        orderId=intent.getStringExtra("order_id")?:"0"
        setLocationChange()
        userLong.observe(this, Observer {
            if (it>0.0 && isFirst) {
                isFirst=false
                sendRequest()
            }
        })
        setMap()

        setActions()

    }

    private fun setMap() {
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as (SupportMapFragment)
        mapFragment.getMapAsync(this)
        try {
            MapsInitializer.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setActions() {
        call_captain_lay.onClick {
            setPermissionsPhone {
                Util.onCall(this@BookingDetailsActivity,
                    captain_phone)
            }
        }
        num_lay.onClick {
            startActivity(Intent(applicationContext,TicketActivity::class.java).apply {
                putExtra("ticket",ticketNum)
            })
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        setPermissionsLocation {
            if (it) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.requestLocationUpdates(
                    LocationRequest(), locationCallback,
                    null
                )
            }
        }
    }
    private fun setLocationChange() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                userLat.postValue( locationResult.locations[0].latitude)
                userLong.postValue(locationResult.locations[0].longitude)




            }

        }

    }
    private fun sendRequest() {
        repo.getTicket(orderId,userLat.value.toString(),userLong.value.toString()
        ,mPrefs?.token!!
        ).subscribeWithLoading({showProgressDialog()},{handleData(it)},
            { it.message?.let { it1 -> handleError(it1) } },{drawSrcDist()})
    }

    private fun handleError(message: String) {
        hideProgressDialog()
        toasty(message,2)

    }

    private fun handleData(data: TicketModel) {
        hideProgressDialog()
        if (data.value=="1") {
            with(data.data!!) {
                tv_name_trip.text = client?.start_point+","+client?.client_start_address+"-"+client?.end_point+","+client?.client_end_address
                tv_dist_time.text= client?.expected_period+" ("+client?.distance.toString()+" ${getString(R.string.km)})"+getString(R.string.from_location)
                Picasso.get().load(captain?.avatar)
                    .into(captain_img)
                cap_name.text=captain?.name
                cap_car_brand.text=car?.car_brand
                cap_car_plate.text=car?.car_number
                cap_rate.text=captain?.rate.toString()
                tv_seats.text= client?.num_persons.toString()
                tv_total_price.text=client?.price.toString()+" "+client?.currency
                ticketNum=client?.ticketNumber.toString()
                 captain_phone=captain?.phone.toString()
                from_lat=client?.client_start_lat
                from_lng=client?.client_start_long
                to_lat=client?.client_end_lat
                to_lng=client?.client_end_long
                src_address=client!!.client_start_address
                dist_address=client.client_end_address
            }
        }
    }
    private fun drawSrcDist() {
        addDisposable(repo.getRountes(
            "$from_lat,$from_lng",
            "$to_lat,$to_lng"
            ,mPrefs!!.user!!.googlekey!!
        ).subscribeWithLoading({

        },{
            val routes = it.routes
            if(routes.isNotEmpty()) {
                val latLngList = getDirectionPolylines(routes)
                drawRouteOnMap(applicationContext,mMap!!, latLngList)
            }
            else{
                drawMarkers()
            }
        },{
            //  toasty(getString(R.string.error_connection),2)
            //Log.e("error",it.message)
        },{

        }))
    }
    private fun drawMarkers() {
        val latLng = LatLng(from_lat!!.toDouble(), from_lng!!.toDouble())
        val srcMarker= mMap!!.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(generateIcon(src_address!!)))
                .title(src_address))
        Log.e("from_marker", "$from_lat,$from_lng")

        //marker of target Location
        val latLng_ = LatLng(to_lat!!.toDouble(), to_lng!!.toDouble())
        val distMarker= mMap!!.addMarker(
            MarkerOptions()
                .position(latLng_)
                .icon(BitmapDescriptorFactory.fromBitmap(generateIcon(dist_address!!)))
                .title(dist_address))
        Log.e("dist_marker", "$to_lat,$to_lng")

        zoomToFitMarkers(listOf(srcMarker,distMarker))


    }
    private fun zoomToFitMarkers(markers:List<Marker>) {
        val b =  LatLngBounds.Builder()
        for ( m in markers) {
            b.include(m.position)
        }
        val bounds = b.build()
        mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));


    }
    fun generateIcon(src:String): Bitmap? {
        val text =  TextView(applicationContext)
        text.setTextColor(ColorStateList.valueOf(Color.WHITE))
        text.text = src
        val generator =  IconGenerator(applicationContext)
        generator.setBackground(ContextCompat.getDrawable(applicationContext,R.drawable.marker_green))
        generator.setContentView(text)
        val icon = generator.makeIcon()
        return icon

    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}