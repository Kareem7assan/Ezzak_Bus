package com.aait.ezakbus.ui.activities.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.ui.activities.bus.BusLinesActivity
import com.aait.ezakbus.ui.activities.splash.SplashActivity
import com.aait.ezakbus.ui.fragments.testFragments.FirstFragment
import kotlinx.android.synthetic.main.activity_main_map.*
import kotlinx.android.synthetic.main.prev_map.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MapPreviewActivity : ParentActivity() {
    private var revealX: Int=0
    private var revealY: Int=0
    private var orderId: Int = 0
    override val layoutResource: Int
        get() = R.layout.activity_main_map
    companion object{
        val EXTRA_CIRCULAR_REVEAL_Y: String?=""
        val EXTRA_CIRCULAR_REVEAL_X: String?=""
        var orderIdLater = MutableLiveData<Int>()
    }

    override fun init() {
        addFragment()
        //setActions()
       // setAnim()
       // setupAnim()
        setupAnim()
        SplashActivity.id.observe(this, Observer{
            if(it>0){

                checkNavigationRide(it)
            }

        }
        )

        //from fcm
        if (intent.getIntExtra("order_id",0)>0) {

            checkNavigationRide()
        }
      }

    private fun setActions() {
        bus_lay.onClick {
            startActivity(Intent(applicationContext, BusLinesActivity::class.java))
        }
    }

    private fun setupAnim() {
        rootLayout.visibility = View.INVISIBLE
        val viewTreeObserver: ViewTreeObserver = rootLayout.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onGlobalLayout() {
                    circularRevealActivity()
                    rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private fun circularRevealActivity() {
            val cx = this.resources.displayMetrics.widthPixels/2.0
            val cy = this.resources.displayMetrics.heightPixels/2.0
            val finalRadius: Int = Math.max(rootLayout.width, rootLayout.height)
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout,
                cx.toInt(),
                cy.toInt(), 0f,
                finalRadius.toFloat()
            )
            circularReveal.duration = 3000
        rootLayout.visibility = View.VISIBLE
            circularReveal.start()
        }

        private fun getDips(dps: Int): Int {
            val resources: Resources = resources
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps.toFloat(),
                resources.displayMetrics
            ).toInt()
        }



        private fun setAnim() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)
            ) {
                rootLayout.visibility = View.INVISIBLE
                revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0)
                revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)
                val viewTreeObserver: ViewTreeObserver = rootLayout.viewTreeObserver
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            revealActivity(revealX, revealY)
                            rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })
                }
            } else {
                rootLayout.visibility = View.VISIBLE
            }
        }

        protected fun revealActivity(x: Int, y: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val finalRadius =
                    (Math.max(rootLayout.width, rootLayout.height) * 1.1).toFloat()
                // create the animator for this view (the start radius is zero)
                val circularReveal =
                    ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 20f, finalRadius)
                circularReveal.duration = 400
                circularReveal.interpolator = AccelerateInterpolator()
                // make the view visible and start the animation
                rootLayout.visibility = View.VISIBLE
                circularReveal.start()
            } else {
                finish()
            }
        }

        protected fun unRevealActivity() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                finish()
            } else {
                val finalRadius =
                    (Math.max(rootLayout.width, rootLayout.height) * 1.1) as Float
                val circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0f
                )
                circularReveal.duration = 400
                circularReveal.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        rootLayout.visibility = View.INVISIBLE
                        finish()
                    }
                })
                circularReveal.start()
            }
        }


        private fun checkNavigationRide(order_Id:Int?=null) {

        val isAcceptedRide=SplashActivity.ride_accepted
        if (intent.getIntExtra("order_id",0)>0 || order_Id!=null){
            if (this.supportFragmentManager.findFragmentByTag("track")?.isVisible == false || this.supportFragmentManager.findFragmentByTag("track")==null) {
                orderId = order_Id ?: intent.getIntExtra("order_id", -1)
                goTracking(isAcceptedRide)

            }
        }
        else{
            Log.e("visible_track",this.supportFragmentManager.findFragmentByTag("track")?.isVisible.toString())
            Log.e("visible_id",intent.getIntExtra("order_id",0).toString())
        }
    }


    private fun goTracking(isAcceptedRide:Boolean=false) {
        var intent=Intent(this,TrackingActivity::class.java).putExtra("order_id",orderId)
            .putExtra("isAcceptedRide",isAcceptedRide)
        startActivity(intent)
        finish()
    }

    private fun addFragment() {
        SplashActivity.id.removeObservers(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FirstFragment())
            .addToBackStack("home")
            .commit()


    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.container)
        (fragment as? com.aait.ezakbus.listeners.OnBack)?.onBackPress()?.let {
           // super.onBackPressed()

            if (!it){
                super.onBackPressed()
            }
            Log.e("isTrue",it.toString())
        }
        if (!fragment?.isVisible!!){
            super.onBackPressed()
        }

    }



}
