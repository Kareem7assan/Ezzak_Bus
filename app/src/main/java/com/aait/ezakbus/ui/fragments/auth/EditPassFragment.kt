package com.aait.ezakbus.ui.fragments.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.aait.ezakbus.R
import com.aait.ezakbus.base.BaseFragment
import com.aait.ezakbus.models.register_model.RegisterModel
import com.aait.ezakbus.network.remote_db.Resource
import com.aait.ezakbus.ui.view_model.RegisterViewModel
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.fragment_pass.continue_btn
import kotlinx.android.synthetic.main.fragment_pass_edit.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.koin.android.viewmodel.ext.android.viewModel

class EditPassFragment : BaseFragment() {
    private val viewModel: RegisterViewModel by viewModel()
    override val viewId: Int
        get() = R.layout.fragment_pass_edit

    override fun init(view: View) {
        context!!.hideKeyboard(view)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //setState()
        checkPass()
        continue_btn.onClick {
            goNext()
        }
    }

    private fun goNext() {
       repo.changePass(etOldPass.text.toString(),etNewPass.text.toString(),mPrefs.token!!)
           .subscribeWithLoading({
               showProgressDialog()
           }
               ,{
                   if (it.value=="1"){
                       activity!!.toasty(it.msg!!)
                   }
                   else{
                       activity!!.toasty(it.msg!!,2)
                   }

               },{
                    hideProgressDialog()
                   Log.e("Error",it.message!!)
               },{
                    hideProgressDialog()

               })
    }

    @SuppressLint("CheckResult")
    private fun checkPass():Boolean{
        var isPassCorrect=false
        viewModel.checkConfPass(etOldPass,etNewPass,etConfPass)!!.subscribe {
            if (!it) {
                continue_btn.disable()
            }

            else {
                continue_btn.enable()
            }
            isPassCorrect=it
        }
        return isPassCorrect
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

}