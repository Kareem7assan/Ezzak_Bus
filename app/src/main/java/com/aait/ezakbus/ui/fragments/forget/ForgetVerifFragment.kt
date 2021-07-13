package com.aait.ezakbus.ui.fragments.forget

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.aait.ezakbus.R
import com.aait.ezakbus.base.BaseFragment
import com.aait.ezakbus.models.register_model.RegisterModel
import com.aait.ezakbus.network.remote_db.Resource
import com.aait.ezakbus.ui.view_model.RegisterViewModel
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.activity_forget.*
import kotlinx.android.synthetic.main.forget_verif.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.koin.android.viewmodel.ext.android.viewModel

class ForgetVerifFragment : BaseFragment() {
    private val viewModel: RegisterViewModel by viewModel()

    override val viewId: Int
        get() = R.layout.forget_verif

    override fun init(view: View) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setState()
        setStateResend()
        continue_btn.onClick {
            viewModel.checkPattern(firstPinView)!!.subscribe {
                if (it){
                    sendRequestVer()
                }
            }
        }

        resend_code.onClick {
            sendRequestResend()
        }

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
    private fun setStateResend(){
        viewModel.states_resend!!.observe(this, Observer {
            when{

                it?.status == Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    val model = it.data as RegisterModel
                    if (model.value=="1") {
                        mPrefs.storeUser(model.data!!)
                        mPrefs.token="Bearer "+model.data!!.token
                       // goNext()
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
        activity!!.v_pager.currentItem=2
    }

    private fun sendRequestVer() {
        viewModel.sendVerification(firstPinView.text.toString(),mPrefs.token!!)
    }
    private fun sendRequestResend(){
        viewModel.resendCode(mPrefs.token!!)
    }

}