package com.aait.ezakbus.ui.dialogs

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.aait.ezakbus.R
import kotlinx.android.synthetic.main.rate_dialog.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import com.aait.ezakbus.models.order_details_model.Data
import com.squareup.picasso.Picasso

class RateDialog(val data: Data,val onRate:(rate:Float)->Unit,val onCancel:()->Unit) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.rate_dialog, container, false)
        // Set transparent background and no title
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            name.text=
                Html.fromHtml(String.format(getString(R.string.ride_review),"${data.captin_name}"), Html.FROM_HTML_MODE_LEGACY)
        }
        else{
            name.text=
                Html.fromHtml(String.format(getString(R.string.ride_review),"${data.captin_name}"))
        }
        rate.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser){
                captin_btn.alpha=1.0f
                captin_btn.isEnabled=true
            }
            else{
                captin_btn.alpha=0.5f
                captin_btn.isEnabled=false
            }
        }
        period.text=data.date
        //rate.rating=data.captin_rate!!
        cash_type.text=data.paymentType
        ride_price.text=data.total.toString()+" "+data.currency
        Picasso.get().load(data.captin_img).into(iv_captin)

        captin_btn.onClick {
            dialog?.dismiss()
            onRate(rate.rating)
        }

    }



    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
        onCancel()


    }
}