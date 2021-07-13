package com.aait.ezakbus.ui.activities.settings

import android.content.Intent
import android.util.Log
import android.view.View
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.ResetModel
import com.aait.ezakbus.network.remote_db.RetroWeb
import com.aait.ezakbus.ui.activities.splash.SplashActivity
import com.aait.ezakbus.ui.dialogs.ExitDialog
import com.aait.ezakbus.utils.Constant
import com.aait.ezakbus.utils.GlobalPreferences
import com.aait.ezakbus.utils.Util
import com.aait.ezakbus.utils.toasty
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import kotlinx.android.synthetic.main.activity_my_settings.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySettingsActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_my_settings

    override fun onResume() {
        super.onResume()
        setData()
    }
    override fun init() {
        setToolbar(getString(R.string.edit))
        setActions()
        mPrefs!!.user!!.registered_social?.let {
            if (it){
                linPass.visibility=View.GONE
            }
        }
    }

    private fun setData() {
        mPrefs?.user!!.apply {
            name_tv.text=firstName
            phone_tv.text=phonekey+""+phone
            if (email!!.isNotBlank()) {
                mail_tv.text = email
            }

        }

    }

    private fun setActions() {
        linName.onClick {
            goHome("name")
        }
        linPhone.onClick {
          //  goHome("phone")
            startActivity(Intent(applicationContext,ChangePhoneActivity::class.java))
        }
        linMail.onClick {
            goHome("mail")
        }
        linPass.onClick {
            goHome("pass")
        }
        lang_lay.onClick {
            showLang(arrayListOf("English","العربية"))
        }
        rate_lay.onClick {
            goRate()
        }
        log_lay.onClick {
            logOut()
        }
    }

    private fun logOut() {
        log_lay.onClick {
        val dialog = ExitDialog{
            sendRequestLogout()
        }
        dialog.show(supportFragmentManager,"logout")
        }

    }

    private fun sendRequestLogout() {
        RetroWeb.serviceApi.logOut(mPrefs!!.token!!).enqueue(object :
            Callback<ResetModel> {
            override fun onFailure(call: Call<ResetModel>, t: Throwable) {
                Log.e("error",t.message)
                toasty(getString(R.string.error_connection),2)

            }

            override fun onResponse(call: Call<ResetModel>, response: Response<ResetModel>) {
                if (response.isSuccessful){
                    if (response.body()!!.value!="0"){
                        GlobalPreferences(applicationContext).logout()
                        val intent = Intent(applicationContext, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        })
    }

    private fun showLang(langs:ArrayList<String>) {

            val dialog = MaterialDialog(this)
            dialog.title(R.string.app_language)

            dialog.positiveButton(res = R.string.confirm)

            dialog.show {
                cornerRadius(16f)
                theme.applyStyle(R.style.AppTheme_Custom,true)
                listItemsSingleChoice(res = R.string.language_settings, items = langs)
                {dialog, indices, item ->

                  if (item.toString()=="English"){
                      updateLang("en")

                  }
                   else{
                      updateLang("ar")
                  }

               }


            }


    }

    private fun updateLang(lang: String) {
        mPrefs!!.storeLang(lang)
        Util.changeLang(lang, applicationContext)
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun goRate() {
        Util.openUrl(this,Constant.APP_URL)
    }

    private fun showDate() {

    }

    private fun showGenderPopUp() {

    }

    fun goHome(key:String){
        val intent=Intent(applicationContext,EditSettingsActivity::class.java)
        intent.putExtra("key",key)
        startActivity(intent)
    }
}
