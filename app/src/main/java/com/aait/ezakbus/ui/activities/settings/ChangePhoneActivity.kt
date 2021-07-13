package com.aait.ezakbus.ui.activities.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.models.register_model.RegisterModel
import com.aait.ezakbus.ui.activities.auth.RegisterActivity
import com.aait.ezakbus.utils.TextDrawable
import com.aait.ezakbus.utils.Util
import com.aait.ezakbus.utils.toasty
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_register_phone.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ChangePhoneActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_register_phone

    private var countryIso: String?=""

    override fun init() {
        countryIso=country_code.selectedCountryNameCode
        setViews()
        setActions()
        checkPhone()
    }
    @SuppressLint("CheckResult")
    private fun checkPhone():Boolean{
        var isCorrect=false
        checkPhone(etPhone)!!.subscribe {
            if (!it) {
                continue_btn.disable()

            }
            else {
                error_txt.visibility= View.GONE
                isCorrect=true
                continue_btn.enable()
            }
            //activity!!.v_pager.setPagingEnabled(it)
        }
        return isCorrect
    }

    private fun setActions() {
        continue_btn.onClick {
            sendRequest()
        }



        country_code.setOnCountryChangeListener {
            countryIso=country_code.selectedCountryNameCode
            RegisterActivity.country_iso=countryIso!!

            etPhone.setCompoundDrawables(
                TextDrawable(etPhone, country_code.selectedCountryCode + " "),
                null,
                null,
                null)
        }
    }

    private fun sendRequest() {
        mPrefs?.token?.let { repo.editProfile(country_iso = countryIso,phone = etPhone.text.toString(),token = mPrefs?.token!!)
            .subscribeWithLoading({showProgressDialog()},{handleData(it)},{handleError(it.message)},{})
        }
    }

    private fun handleData(it: RegisterModel) {
        hideProgressDialog()
        if (it.value=="1") {
            toasty(getString(R.string.phone_updated))
            mPrefs?.storeUser(it.data!!)
            mPrefs?.token="Bearer "+it.data?.token
            onBackPressed()

        }
        else{
            it.msg?.let { it1 -> toasty(it1,2) }
        }

    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        Log.e("error",message!!+",")
        message?.let { toasty(it,2) }
    }

    private fun setViews() {
        continue_btn.disable()
        etPhone.setCompoundDrawables(
            TextDrawable(
                etPhone,
                country_code.selectedCountryCode + " "
            ), null, null, null
        )
    }

    fun checkPhone(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .map { inputText -> Util.isPhone(inputText.toString())}
            .distinctUntilChanged()

    }
}