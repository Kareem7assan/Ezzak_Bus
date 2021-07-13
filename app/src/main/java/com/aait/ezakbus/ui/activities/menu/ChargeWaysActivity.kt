package com.aait.ezakbus.ui.activities.menu

import android.util.Log
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.ui.dialogs.BottomSheetCard
import com.aait.ezakbus.utils.Constant
import com.aait.ezakbus.utils.Util
import com.aait.ezakbus.utils.toasty
import kotlinx.android.synthetic.main.my_wallet.check_balance
import kotlinx.android.synthetic.main.my_wallet_full.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick

class ChargeWaysActivity : ParentActivity() {
    private lateinit var card: BottomSheetCard

    override val layoutResource: Int
        get() = R.layout.my_wallet_full

    override fun init() {
        setToolbar(getString(R.string.charge))
        setActions()
    }

    private fun setActions() {
        charge_lay.onClick {
            Util.openUrl(this@ChargeWaysActivity, Constant.PAYMENT_URL)
        }

        check_balance.onCheckedChange { buttonView, isChecked ->
            addDisposable(repo.useBalance(isChecked,mPrefs?.token!!).subscribeWithLoading({
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
            }))
        }

        cupon.onClick {
             card= BottomSheetCard(getString(R.string.shipping_cupon)){ code, btn->
                sendRequestCard(code,btn)
            }
            card.show(supportFragmentManager,"wallet")

        }
    }

    private fun sendRequestCard(
        code: String,
        btn: CircularProgressButton
    ) {
        addDisposable(repo.chargeCode(code,mPrefs!!.token!!).subscribeWithLoading({
         //   btn.setProgress(100f)
           // btn.revertAnimation()
            card.isCancelable=false
        },{

            if(it.value=="1"){
                toasty(it.msg!!)
                card.dismiss()
            }
            else{
                toasty(it.msg!!,2)
            }

        },{
            card.isCancelable=true
          //  btn.initialCorner=16f
           // btn.finalCorner=16f
            Log.e("error",it.message)
            toasty(getString(R.string.error_connection),2)
           // btn.setProgress(100f)
           // btn.revertAnimation()

        },{
           // btn.initialCorner=16f
            //btn.finalCorner=16f
            //btn.revertAnimation()

            card.isCancelable=true
        }))
    }




}
