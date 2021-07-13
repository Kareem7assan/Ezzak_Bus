package com.aait.ezakbus.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.ezakbus.R
import com.aait.ezakbus.base.BaseFragment
import com.aait.ezakbus.listeners.Navigation_Listeners
import com.aait.ezakbus.models.NavigationModel
import com.aait.ezakbus.models.ResetModel
import com.aait.ezakbus.network.remote_db.RetroWeb
import com.aait.ezakbus.ui.activities.bus.BusMenuActivity
import com.aait.ezakbus.ui.activities.menu.MyChargeActivity
import com.aait.ezakbus.ui.activities.menu.MyRidesActivity
import com.aait.ezakbus.ui.activities.menu.ShareActivity
import com.aait.ezakbus.ui.activities.notifications.NotificationsActivity
import com.aait.ezakbus.ui.activities.settings.ContactUsActivity
import com.aait.ezakbus.ui.activities.settings.MySettingsActivity
import com.aait.ezakbus.ui.activities.splash.SplashActivity
import com.aait.ezakbus.ui.adapters.NavAdapter
import com.aait.ezakbus.ui.dialogs.ExitDialog
import com.aait.ezakbus.utils.Constant
import com.aait.ezakbus.utils.GlobalPreferences
import com.aait.ezakbus.utils.Util
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_fragment.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NavFragment : BaseFragment() {

    companion object{
        private var position: Int=-1
        private var balance=MutableLiveData<String>()
        private var notifNum=MutableLiveData<Int>()
    }

    private lateinit var adapter: NavAdapter
    private lateinit var models: ArrayList<NavigationModel>
    override val viewId: Int
        get() = R.layout.nav_fragment

    override fun init(view: View) {

    }

    private fun setRecycler() {
        nav_recycler.layoutManager= LinearLayoutManager(context!!)
         models = ArrayList()
        models.add(NavigationModel(R.string.your_journies, R.mipmap.calendar_with_clock_timetools))
        models.add(NavigationModel(R.string.bus_rides, R.mipmap.bussmall))
        models.add(NavigationModel(R.string.notifications, R.drawable.notifications))
        models.add(NavigationModel(R.string.my_wallet, R.mipmap.walle_filledmoney,mPrefs.user?.balance))
        models.add(NavigationModel(R.string.contact_us, R.drawable.contact))

        models.add(NavigationModel(R.string.free_trips, R.mipmap.giftbox))
        //models.add(NavigationModel(R.string.help, R.mipmap.round_error_symbol))
        models.add(NavigationModel(R.string.settings, R.mipmap.settings))


        val listeners = setActionListeners()
         adapter = NavAdapter(context, models, listeners,{
            position=it
        },position)
        nav_recycler.adapter=adapter

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
            setRecycler()
            balance.observe(this, androidx.lifecycle.Observer {
                models[3] = NavigationModel(R.string.my_wallet, R.mipmap.walle_filledmoney,it)
                adapter.notifyDataSetChanged()
                Log.e("balance",it.toString())
            })
        notifNum.observe(this, androidx.lifecycle.Observer {
            if (it>0) {
                models[2] = NavigationModel(R.string.notifications, R.drawable.notifications, notifNum=it)
                adapter.notifyDataSetChanged()


            }

            })
        setActions()

    }

    override fun onResume() {
        super.onResume()
        val user = GlobalPreferences(context!!)
        profile_lay.visibility=View.VISIBLE
        user_name.text=user.user!!.firstName
        phone.text=user.user!!.phone
        Picasso.get().load(user.user!!.avatar).placeholder(R.drawable.profile).into(avatar)
        sendrequestBalance()
        sendRequestNotifications()
    }

    private fun sendrequestBalance() {
        repo.getBalance(mPrefs.token!!).subscribeWithLoading({

        },{
            if (it.value=="1"){
                balance.postValue(it.data?.balance)
            }
        },{
            Log.e("error",it.message!!)
        },{

        })
    }
    private fun sendRequestNotifications() {
        repo.notificationsNum(mPrefs.token!!).subscribeWithLoading({

        },{
            if (it.value=="1"){
                notifNum.postValue(it.data?.numNotifications)
            }
        },{
            Log.e("error",it.message!!)
        },{

        })
    }
    private fun setActions() {
        close.onClick {
            position=-1
            activity!!.onBackPressed()
        }
        exit.onClick {
            val dialog = ExitDialog{
                sendRequestLogout()
            }
            dialog.show(activity!!.supportFragmentManager,"logout")

        }
        join_as_lay.onClick {
            Util.openUrl(context!!,"${Constant.BASE}/captainSignupForm")
        }
    }

    private fun sendRequestLogout() {
        RetroWeb.serviceApi.logOut(mPrefs.token!!).enqueue(object :
            Callback<ResetModel> {
            override fun onFailure(call: Call<ResetModel>, t: Throwable) {
                Log.e("error",t.message)
                toast(getString(R.string.error_connection))

            }

            override fun onResponse(call: Call<ResetModel>, response: Response<ResetModel>) {
                if (response.isSuccessful){
                    if (response.body()!!.value!="0"){
                        GlobalPreferences(context!!).logout()
                        var intent = Intent(activity, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        })
    }

    private fun setActionListeners(): Navigation_Listeners {
        return object : Navigation_Listeners {
            override fun onYourJournies() {
                var intent=Intent(activity,MyRidesActivity::class.java)
                startActivity(intent)

                }

            override fun onNotifications() {
                var intent=Intent(activity,NotificationsActivity::class.java)
                startActivity(intent)
            }

            override fun onContactUs() {
                var intent=Intent(activity,ContactUsActivity::class.java)
                startActivity(intent)
            }

            override fun onWallet() {
                val intent=Intent(activity,MyChargeActivity::class.java)
                startActivity(intent)
            }

            override fun onFreeTrips() {
                startActivity(Intent(activity!!,
                    ShareActivity::class.java))
            }

            override fun onJoin() {
                Util.openUrl(context!!,"${Constant.BASE}/captainSignupForm")
            }

            override fun onBus() {
                var intent=Intent(activity, BusMenuActivity::class.java)
                startActivity(intent)
            }

            override fun onSettings() {
                val intent = Intent(activity, MySettingsActivity::class.java)
                startActivity(intent)
            }

            override fun onHelp() {
                Util.openUrl(context!!,"${Constant.BASE}/captainSignupForm")
            }


        }
}
/*
    private fun sendRequestLogout() {

        RetroWeb.getClient().logOut(mPrefs.token!!).enqueue(object :
            Callback<ResetModel> {
            override fun onFailure(call: Call<ResetModel>, t: Throwable) {
                Log.e("error",t.message)
                toast(getString(R.string.error_connection))

            }

            override fun onResponse(call: Call<ResetModel>, response: Response<ResetModel>) {
                if (response.isSuccessful){
                    if (response.body()!!.value!="0"){
                        GlobalPreferences(context!!).logout()
                        var intent = Intent(activity, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        })

    }

    private fun sendRequest(complain: String, bottom: BottomSheetContact) {
        showProgressDialog()
        RetroWeb.getClient().complain(complain,mPrefs.token!!).enqueue(object : Callback<ResetModel> {
            override fun onFailure(call: Call<ResetModel>, t: Throwable) {
                hideProgressDialog()
                activity!!.toasty(getString(R.string.error_connection),2)
            }

            override fun onResponse(call: Call<ResetModel>, response: Response<ResetModel>) {
                hideProgressDialog()
                if (response.body()!!.value=="1"){
                    activity!!.toasty(response.body()!!.msg!!)
                    bottom.dismiss()
                }
            }
        })
    }*/
}
