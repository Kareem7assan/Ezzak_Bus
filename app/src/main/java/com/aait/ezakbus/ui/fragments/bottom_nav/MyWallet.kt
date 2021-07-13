package com.aait.ezakbus.ui.fragments.bottom_nav

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.Window
import android.widget.TextView
import com.aait.ezakbus.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange

class MyWallet(val price:String,val isChecked:Boolean
               ,val onCheckChange:(checked:Boolean)->Unit): BottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, R.layout.my_wallet, null)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        dialog.setContentView(view)
        val switch: SwitchMaterial = view.find(R.id.check_balance)
        val balance: TextView = view.find(R.id.blance)
/*
        val marker: ImageView = view.find(R.id.marker)
*/
        switch.isChecked=isChecked
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            balance.text=Html.fromHtml(String.format(getString(R.string.use_mycharge), price), Html.FROM_HTML_MODE_LEGACY)
        }
        else{
            balance.text=
                Html.fromHtml(String.format(getString(R.string.use_mycharge), price))
        }

        switch.onCheckedChange { buttonView, isChecked ->
                onCheckChange(isChecked)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
/*
        sendRequest()
        setActions()
*/
    }

/*    private fun setActions() {
        check_balance.onCheckedChange { buttonView, isChecked ->
            addDisposable(repo.useBalance(isChecked,mPrefs.token!!).subscribeWithLoading({
            },{
                if (it.value=="1"){

                }
                else{
                    activity!!.toasty(it.msg!!,2)
                }
            },{
                activity!!.toasty(getString(R.string.error_connection),2)
                Log.e("error",it.message!!)
            },{
            }))
        }

    }

    private fun sendRequestCard(
        code: String,
        btn: CircularProgressButton
    ) {
        addDisposable(repo.chargeCode(code,mPrefs.token!!).subscribeWithLoading({
            btn.setProgress(100f)
            btn.revertAnimation()
            card.isCancelable=false
        },{

            if(it.value=="1"){
                activity!!.toasty(it.msg!!)
                card.dismiss()
            }
            else{
                activity!!.toasty(it.msg!!,2)
            }

        },{
            card.isCancelable=true
            btn.initialCorner=16f
            btn.finalCorner=16f
            Log.e("error",it.message)
            activity!!.toasty(getString(R.string.error_connection),2)
            btn.setProgress(100f)
            btn.revertAnimation()

        },{
            btn.initialCorner=16f
            btn.finalCorner=16f
            btn.revertAnimation()

            card.isCancelable=true
        }))
    }





    private fun sendRequest() {
            repo.getBalance(mPrefs.token!!).subscribeWithLoading(
                {
                if (it.value=="1"){
                    check_balance.isChecked=it.data?.balance!!.toBoolean()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        blance.text=Html.fromHtml(String.format(getString(R.string.use_mycharge),"${it.data!!.balance}"), Html.FROM_HTML_MODE_LEGACY)
                    }
                    else{
                            blance.text=
                                Html.fromHtml(String.format(getString(R.string.use_mycharge),"${it.data!!.balance}"))
                        }
                    check_balance.isChecked=it.data!!.use_balance_first!!.toBoolean()

                }
                else{
                    activity!!.toasty(it.msg!!,2)
                }
            },{
                Log.e("error",it.message)
                activity!!.toasty(getString(R.string.error_connection),2)
            })
    }*/
}