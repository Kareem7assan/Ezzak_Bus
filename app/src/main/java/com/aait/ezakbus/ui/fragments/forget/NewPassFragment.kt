package com.aait.ezakbus.ui.fragments.forget

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.aait.ezakbus.R
import com.aait.ezakbus.base.BaseFragment
import com.aait.ezakbus.models.register_model.RegisterModel
import com.aait.ezakbus.network.remote_db.Resource
import com.aait.ezakbus.ui.activities.auth.LoginActivity
import com.aait.ezakbus.ui.view_model.RegisterViewModel
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.fragment_new_pass.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.koin.android.viewmodel.ext.android.viewModel

class NewPassFragment: BaseFragment() {
    private val viewModel: RegisterViewModel by viewModel()

    override val viewId: Int
        get() = R.layout.fragment_new_pass

    override fun init(view: View) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        continue_btn.onClick {
            viewModel.checkPass(etPass)!!.subscribe {

            if (it && viewModel.checkMatch(etPass,etConfPass,etPassLayConf)){
                sendRequest()
            }
         }
        }
        setState()

    }

    private fun sendRequest() {
        viewModel.resetPass(etPass.text.toString(),mPrefs.token!!)
    }


    private fun setState(){
        viewModel.states!!.observe(this, Observer {
            when{

                it?.status == Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    val model = it.data as RegisterModel
                    if (model.value=="1") {
                        mPrefs.storeUser(model.data!!)
                        mPrefs.token="Bearer "+model.data!!.token
                        goNext()
                    }
                    else{
                        activity!!.toasty(model.msg!!,2)
                    }
                }
                it?.status== Resource.Status.ERROR ->{
                    hideProgressDialog()
                    activity!!.toasty(it.message!!,2)
                }
                it?.status== Resource.Status.LOADING_FIRST ->{
                    showProgressDialog()
                }
            }
        })
    }

    private fun goNext() {
        val intent = Intent(activity!!, LoginActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }
}