package com.aait.ezakbus.ui.activities.settings
import android.content.Intent
import android.util.Log
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.ui.activities.menu.LanguageActivity
import com.aait.ezakbus.utils.Util
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick

class SettingsActivity : ParentActivity() {
    private var isNotify: Boolean = true
    private var lang: String = Util.language
    override val layoutResource: Int
        get() = R.layout.activity_settings

    override fun init() {
        setToolbar(getString(R.string.settings))

        lang_lay.onClick {
            val intent = Intent(this@SettingsActivity, LanguageActivity::class.java)
            intent.putExtra("lang",lang)
            startActivity(intent)
        }
        switch_.onCheckedChange { buttonView, isChecked ->
                sendRequest(isChecked)
        }

        sendRequestDevice()
    }

    private fun sendRequestDevice() {
        addDisposable(repo.settings(mPrefs!!.token!!).subscribeWithLoading({
            showProgressDialog()
        },{
            if (it.value=="1"){
                isNotify=it.data!!.ordersNotify!!.toBoolean()

                    switch_.isChecked=isNotify

            }
            else{
                toasty(it.msg!!,2)
            }
        },{
            hideProgressDialog()
            toasty(getString(R.string.error_connection),2)
            Log.e("error",it.message!!)
        },{
            hideProgressDialog()

        }))
    }

    private fun sendRequest(checked: Boolean) {
        addDisposable(repo.updateNotif(checked.toString(),mPrefs!!.token!!)
            .subscribeWithLoading({
              //  showProgressDialog()
            },{
                if (it.value=="1"){
                    toasty(it.data!!)
                }
                else{
                    toasty(it.msg!!,2)
                }
            },
                {
                hideProgressDialog()
                toasty(getString(R.string.error_connection),2)
               // Log.e("error",it.message!!)
            },{
               hideProgressDialog()
            })
        )
    }




}
