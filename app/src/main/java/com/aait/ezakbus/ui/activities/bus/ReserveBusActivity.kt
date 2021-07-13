package com.aait.ezakbus.ui.activities.bus

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnResume
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.ResetModel
import com.aait.ezakbus.models.line_points.LinePointsModel
import com.aait.ezakbus.models.line_points.Point
import com.aait.ezakbus.models.route_model.Routes
import com.aait.ezakbus.models.time_distance_model.TimeDistanceModel
import com.aait.ezakbus.ui.adapters.Horizental_Step_View
import com.aait.ezakbus.ui.dialogs.BottomSheetCard
import com.aait.ezakbus.ui.fragments.bottom_nav.MyWallet
import com.aait.ezakbus.utils.toasty
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.activity_reserve_bus.*
import kotlinx.android.synthetic.main.item_adapter_details_no_seperator.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class ReserveBusActivity : ParentActivity(), OnMapReadyCallback {
    private var tripPrice: Int = 0
    private lateinit var walletSheet: MyWallet
    private var order_id: String?=""
    private var useBalanceFirst: Boolean = false
    private var arrival_time: String?=""
    private  var endPos: Point? = null
    private  var startPos: Point? = null
    private  var mPoints: List<Point> = mutableListOf()
    private var dist_address: String? = ""
    private var src_address: String? = ""
    private var from_lat: Double=0.0
    private var from_lng: Double=0.0
    private var to_lat: Double=0.0
    private var to_lng: Double=0.0
    private var mMap: GoogleMap? = null
    private var lineId: String? = null
    private var paymentMethod="cash"

    override val layoutResource: Int
        get() = R.layout.activity_reserve_bus

    override fun onMapReady(gMap: GoogleMap?) {
        mMap=gMap
        sendReuestLine()

    }


    val mAdapter=Horizental_Step_View{start_selected_pos: Int, end_selected_pos: Int, action: Horizental_Step_View.Action ->
        if (action==Horizental_Step_View.Action.START_SELECTED){
            btn_go.visibility= View.VISIBLE
            btn_go.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(applicationContext,R.color.black))
            btn_go.text=getString(R.string.detect_end_location)


        }
        else if (action==Horizental_Step_View.Action.END_SELECTED){
            btn_go.visibility= View.VISIBLE
            btn_go.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(applicationContext,R.color.colorAccent))
            btn_go.text=getString(R.string.cont)
            endPos=mPoints[end_selected_pos]
            startPos=mPoints[start_selected_pos]
        }
        else{
            btn_go.visibility= View.INVISIBLE
            btn_go.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(applicationContext,R.color.black))
            btn_go.text=getString(R.string.detect_end_location)
        }
    }
    override fun init() {
        //setToolbar(getString(R.string.bus_lines))
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

        lineId=intent.getStringExtra("line_id")
        arrival_time=intent.getStringExtra("arrival_time")
        order_id=intent.getStringExtra("order_id")
        setRec()
        setActions()

    }

    private fun setActions() {
        promo_lay.onClick {
            val bottomSheetCard = BottomSheetCard(getString(R.string.enter_promo_code)){code,btn->
                sendRequestPromo(code)
            }
            bottomSheetCard.show(supportFragmentManager,"asd")
        }
        btn_go.onClick {
            if (btn_go.text==getString(R.string.cont)){
                showPriceAnim()
                //setTripData()
                sendRequest()
            }
        }
        btn_yalla.onClick {
            if (btn_go.text==getString(R.string.cont)){
                sendRequestOrder()
            }
        }
        payment_lay.onClick {
            selectPaymentMethod()
        }
        minus_iv.onClick {
            if (tv_seats.text.toString().toInt()>0) {
                val numSeats = tv_seats.text.toString().toInt() - 1
                tv_seats.text = (numSeats).toString()
                tv_total.text = (numSeats * tripPrice).toString()+" "+getString(R.string.rs)
            }

        }
        plus_iv.onClick {
            val numSeats=tv_seats.text.toString().toInt()+1
            tv_seats.text=(numSeats).toString()
            tv_total.text=(numSeats*tripPrice).toString()+" "+getString(R.string.rs)
        }
    }

    private fun sendRequest() {
        repo.calculateTimeDistance(order_id!!,startPos?.id.toString(),endPos?.id.toString(),tv_seats.text.toString(),mPrefs?.token!!)
            .subscribeWithLoading({showProgressDialog()},{handleTrip(it)},{handleError(it)},{})
    }

    private fun handleTrip(resp: TimeDistanceModel) {
        hideProgressDialog()
        if (resp.value == "1") {
            with(resp.data!![0]) {
                reach_tv.text = getString(R.string.reach) + " " +arrive_time
                        src_city.text = "$start_point,$start_address"
                tv_duration.text = expected_period
                dist_city.text = "$end_point,$end_address"
                tripPrice= price?.div((tv_seats.text.toString().toInt()))!!
                tv_price.text = (price?.div((tv_seats.text.toString().toInt()))).toString() + " " + getString(R.string.rs)
                tv_total.text = price.toString() + " " + getString(R.string.rs)
            }
        }
    }

    private fun sendRequestOrder() {
        repo.orderBus(order_id.toString(),startPos?.id.toString(),
            endPos?.id.toString(),
            paymentMethod,
            tv_seats.text.toString(),
            mPrefs?.token!!
            ).subscribeWithLoading({showProgressDialog()},{handleDataOrder(it)},{handleError(it)},{})
    }

    private fun handleDataOrder(it: ResetModel) {
        hideProgressDialog()
        if (it.value=="1"){
            it.msg?.let { it1 -> toasty(it1) }
            startActivity(Intent(applicationContext,BookingMessageActivity::class.java).apply {
                putExtra("arrive_time",arrival_time)
                putExtra("src_pos",startPos)
                putExtra("order_id",order_id)
                putExtra("dist_pos",endPos)
            })
            finish()
        }
        else{
            it.msg?.let { it1 -> toasty(it1,2) }
        }
    }

    private fun sendRequestPromo(code: String) {
        repo.addPromoCode(code, mPrefs?.token!!).subscribeWithLoading({showProgressDialog()},
            {  handleCode(it)  },{handleError(it)},{})
    }

    private fun handleCode(data: ResetModel) {
        hideProgressDialog()
        if (data.value=="1"){
            toasty(data.data!!,2)
        }
        else{
             toasty(data.msg!!,2)
        }
    }

    private fun setTripData() {
        reach_tv.text=getString(R.string.reach)+" "+arrival_time
        src_city.text=startPos?.name+","+startPos?.address
        tv_duration.text=endPos?.expected_time+" "+getString(R.string.mins)
        dist_city.text=endPos?.name+","+endPos?.address
        tv_price.text=endPos?.price+" "+getString(R.string.rs)
        tv_total.text=endPos?.price+" "+getString(R.string.rs)
    }

    @SuppressLint("NewApi")
    private fun showPriceAnim() {
        //hide trip card , show price card
        ObjectAnimator.ofFloat(trip_card,"translationY",0F,trip_card.height.toFloat()).also {
                    it.duration=500
                    it.start()
                    it.doOnResume {
                        val parms=mapView.view!!.layoutParams

                        parms.height=FrameLayout.LayoutParams.MATCH_PARENT
                        mapView.view!!.layoutParams = parms
                    }
                    it.doOnEnd {
                        trip_card.visibility=View.INVISIBLE
                        price_card.visibility = View.VISIBLE
                        ObjectAnimator.ofFloat(price_card, "translationY", price_card.height.toFloat(), 0F).also { obj->
                            //optional change the height of map
                            val parms=mapView.view!!.layoutParams
                            parms.height=600
                            mapView.view!!.layoutParams = parms
                            obj.duration = 500
                            obj.start()
                        }
                    }
    }
    }



    private fun setRec() {
        rec_points.layoutManager=LinearLayoutManager(this)
        rec_points.adapter=mAdapter
    }

    private fun sendReuestLine() {
        repo.getPoints(lineId!!,mPrefs?.token!!).subscribeWithLoading({showProgressDialog()},{handleData(it)},{handleError(it)},{})
    }

    private fun handleError(error: Throwable) {
        hideProgressDialog()
        error.message?.let { toasty(it,2) }
        Log.e("error",error.message)
    }

    @SuppressLint("SetTextI18n")
    private fun handleData(it: LinePointsModel) {
        hideProgressDialog()
        if (it.value=="1"){
            val days=it.data?.get(0)?.days
            val days_ = days!!.map {
                if (days.last() != it) {
                    it.name + ","
                } else {
                    it.name
                }
            }
            days_.map { tv_days.append(it) }
            if (it.data.isNotEmpty()) {
                mPoints=it.data
                mAdapter.swapData(it.data)
                tv_name_trip.text = it.data[0].name + "," + it.data[0].address + "\n" +
                        it.data.last().name + "," + it.data.last().address

                it.data[0].apply {
                    from_lat=lat!!.toDouble()
                    from_lng= long!!.toDouble()
                    src_address=address
                }
                it.data.last().apply {
                    to_lat=lat!!.toDouble()
                    to_lng=long!!.toDouble()
                    dist_address=address
                }

                drawSrcDist()
            }
        }
    }


    private fun zoomToFitMarkers(markers:List<Marker>) {
        val b =  LatLngBounds.Builder()
        for ( m in markers) {
            b.include(m.position)
        }
        val bounds = b.build()
        mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));


    }

    fun selectPaymentMethod(){
       walletSheet= MyWallet(mPrefs?.user?.balance.toString(),useBalanceFirst) {
            useBalanceFirst=it
            paymentMethod = if (it) {
                "use_balance_first"
            } else{
                "cash"
            }
           walletSheet.dismiss()
        }.also {
            it.show(supportFragmentManager,"frag")
        }
    }

    private fun drawRouteOnMap(map: GoogleMap, positions: List<LatLng>) {
        val options = PolylineOptions().width(5f).color(ContextCompat.getColor(applicationContext,R.color.colorAccent)).geodesic(true)
        options.addAll(positions)
        val polyline = map.addPolyline(options)
        //marker of my location

        drawMarkers()
    }

    private fun sendRequestChange(isChecked:Boolean){
        repo.useBalance(isChecked,mPrefs?.token!!).subscribeWithLoading({
        },{
            if (it.value=="1"){

            }
            else{
                toasty(it.msg!!,2)
            }
        },{
            //   hideProgressDialog()
            toasty(getString(R.string.error_connection),2)
            Log.e("error",it.message!!)
        },{
            // hideProgressDialog()
        })
    }
    private fun drawMarkers() {
        val latLng = LatLng(from_lat, from_lng)
        val srcMarker= mMap!!.addMarker(MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromBitmap(generateIcon(src_address!!)))
            .title(src_address))
        Log.e("from_marker", "$from_lat,$from_lng")

        //marker of target Location
            val latLng_ = LatLng(to_lat, to_lng)
            val distMarker= mMap!!.addMarker(MarkerOptions()
                .position(latLng_)
                .icon(BitmapDescriptorFactory.fromBitmap(generateIcon(dist_address!!)))
                .title(dist_address))
            Log.e("dist_marker", "$to_lat,$to_lng")

        zoomToFitMarkers(listOf(srcMarker,distMarker))


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

    private fun getDirectionPolylines(routes: MutableList<Routes>): List<LatLng> {
        val directionList = ArrayList<LatLng>()
        for (route in routes) {
            val legs = route.legs
            for (leg in legs) {
                val steps = leg.steps
                for (step in steps) {
                    val polyline = step.polyline
                    val points = polyline.points
                    val singlePolyline = decodePoly(points)
                    for (direction in singlePolyline) {
                        directionList.add(direction)
                    }
                }
            }
        }
        return directionList
    }
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                    lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
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
                drawRouteOnMap(mMap!!, latLngList)
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
    @SuppressLint("NewApi")
    override fun onBackPressed() {
        if (price_card.visibility == View.VISIBLE) {
            ObjectAnimator.ofFloat( price_card,"translationY",0F,price_card.height.toFloat()).also {
                it.duration=500
                it.start()

                it.doOnResume {
                    val parms=mapView.view!!.layoutParams
                    parms.height=LinearLayout.LayoutParams.MATCH_PARENT
                    mapView.view!!.layoutParams = parms
                }
                it.doOnEnd {
                    val parms=mapView.view!!.layoutParams
                    parms!!.height=400
                    mapView.view!!.layoutParams = parms
                    trip_card.visibility=View.VISIBLE
                    price_card.visibility = View.INVISIBLE
                    ObjectAnimator.ofFloat(trip_card, "translationY", trip_card.height.toFloat(), 0F).also { obj->
                        obj.duration = 500
                        obj.start()
                    }
                }
            }
        } else {
            super.onBackPressed()
        }
    }
}