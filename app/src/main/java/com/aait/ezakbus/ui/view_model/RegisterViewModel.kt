package com.aait.ezakbus.ui.view_model

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.aait.ezakbus.R
import com.aait.ezakbus.base.BaseViewModel
import com.aait.ezakbus.network.remote_db.Resource
import com.aait.ezakbus.network.repository.countries_repo.RepoCountriesImp
import com.aait.ezakbus.network.repository.other_repos.RemoteRepos
import com.aait.ezakbus.ui.activities.auth.RegisterActivity
import com.aait.ezakbus.utils.Util
import com.chaos.view.PinView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3

class RegisterViewModel(private val repoImp: RepoCountriesImp, private val registerRepo: RemoteRepos?=null, val context: Context): BaseViewModel() {


    override var states: MutableLiveData<Resource<Any>>? = MutableLiveData()
     var states_resend: MutableLiveData<Resource<Any>>? = MutableLiveData()
    var citiesStates: MutableLiveData<Resource<Any>>? = MutableLiveData()
    //return countries from API or DB
    fun getCountries(){
        addDisposable(repoImp.getCountries()
                .subscribeWithLoading({
                    states!!.value= Resource.success(it)
                },{
                    Log.e("error",it.message)
                    states!!.value= Resource.error(it.message!!,it)
                },{
                    Log.e("complete","end")
                })
        )
    }
    fun signIn(phone: String,password: String){
        addDisposable(registerRepo!!.sigIn(phone,password,RegisterActivity.socialId)
            .subscribeWithLoading({
                states!!.value= Resource.success(it)
            },
                {
                    Log.e("error",it.message)
                    states!!.value= Resource.error(it.message!!,it)
                },{
                    Log.e("complete","end")
                })
        )
    }

    fun sendVerification(code:String,header:String){
        addDisposable(registerRepo!!.verification(code,header).subscribeWithLoading({
            states!!.value=Resource.success(it)
        },{
            states!!.value= Resource.error(it.message!!,it)
        },{
            Log.e("completed","done")
        }
        )


        )
    }

    fun forgetPass(phone: String){
        addDisposable(registerRepo!!.forgetPass(phone).subscribeWithLoading({
            states!!.value=Resource.success(it)
        },{
            states!!.value= Resource.error(it.message!!,it)
        },{
            Log.e("completed","done")
        })


        )
    }

    fun resetPass(pass: String,header: String){
        addDisposable(registerRepo!!.resetPass(pass, header).subscribeWithLoading({
            states!!.value=Resource.success(it)
        },{
            states!!.value= Resource.error(it.message!!,it)
        },{
            Log.e("completed","done")
        })


        )
    }

    fun getCities(city_id: Int){
        addDisposable(registerRepo!!.getCities(city_id.toString())
            .subscribeWithLoading({
                citiesStates!!.value= Resource.success(it)
            },{
                Log.e("error11",it.message)
                citiesStates!!.value= Resource.error(it.message!!,it)
            },{
                Log.e("completed","done")
            })
        )
    }

    @SuppressLint("CheckResult")
    fun register(phone:String, country_iso:String,
                 mail:String,
                 password:String, name:String,friend_code:String?=null,social_id:String?=null,auth:String){

        addDisposable(registerRepo!!.getUser(phone, country_iso,mail, password, name, friend_code,social_id,auth)
            .subscribeWithLoading({
                states!!.value= Resource.success(it)
            },{
                states!!.value= Resource.error(it.message!!,it)
            },{
                Log.e("completed","done")
            })
        )

    }

    @SuppressLint("CheckResult")
    fun resendCode(header: String){

        addDisposable(registerRepo!!.resendCode(header)
            .subscribeWithLoading({
                states_resend!!.value= Resource.success(it)
            },{
                states_resend!!.value= Resource.error(it.message!!,it)
            },{
                Log.e("completed","done")
            })
        )

    }
    //check phone
    fun checkPhone(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .map { inputText -> Util.isPhone(inputText.toString())}
            .distinctUntilChanged()

    }
    //check email
    fun checkMail(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .map { inputText -> Util.isEmailValid(inputText.toString())}
            .distinctUntilChanged()
    }
    //check name
    fun checkName(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .map { inputText -> inputText.trim().isNotBlank()}
            .distinctUntilChanged()
    }
    //check terms
    fun checkTerms(checkBox: CheckBox): Observable<Boolean>? {
         return RxCompoundButton.checkedChanges(checkBox)
             .distinctUntilChanged()
    }
    //check pass
    fun checkPass(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 5}
            .distinctUntilChanged()
    }

    fun checkConfPass(currentPass: TextInputEditText,newPass:TextInputEditText,confirmNew:TextInputEditText): Observable<Boolean>? {
        return Observable.combineLatest(
            checkPass(currentPass),
            checkPass(newPass),
            checkMatchObsservalbe(newPass,confirmNew),
            Function3<Boolean, Boolean,Boolean,Boolean> { t1, t2,t3 ->
                val isCorrect = when{
                    // all accepted
                    t1 && t2 && t3 -> true

                    else->false
                }
                isCorrect
            }
        )
            .distinctUntilChanged()
    }
    //check pattern
    fun checkPattern(pattern: PinView):Observable<Boolean>?{
        return RxTextView.textChanges(pattern)
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 3}
            .distinctUntilChanged()
    }



    //check password matching
    fun checkMatch(etPass:TextInputEditText,etPassConf:TextInputEditText,confLay:TextInputLayout):Boolean{
        return Util.checkMatch(etPass,etPassConf,confLay,context.getString(R.string.error_match))
    }

    fun checkMatchObsservalbe(etPass:TextInputEditText,etPassConf:TextInputEditText):Observable<Boolean>?{
        return RxTextView.textChanges(etPassConf)
            .map { inputText -> inputText.trim().isNotBlank() && inputText.toString() == etPass.text.toString() }
            .distinctUntilChanged()
    }

    @SuppressLint("CheckResult")
    fun checkAllTerms(etFname:EditText, etLname:EditText,check_terms:CheckBox): Observable<Int>? {
        return Observable.combineLatest(
            checkName(etFname)!!,
            /*checkName(etLname)!!,*/
            checkTerms(check_terms)!!,
            BiFunction<Boolean/*, Boolean*/,Boolean,Int> { t1/*, t2*/,t3 ->
                val isCorrect = when{
                    // terms not accepted
                    t1 && !t3 -> 1
                    // first not accepted
                    !t1 /*&& t2*/ && t3 -> 2
                    // second not accepted
                        // t1 /*&& !t2*/ && t3 -> 3
                    // both not accepted
                   // !t1 /*&& !t2*/ && t3 -> 4
                    // all not accepted
                    !t1 /*&& !t2*/ && !t3 -> 5
                    // all accepted
                    else->0
                }
                isCorrect
            }
        )
            .distinctUntilChanged()
    }

    fun clearTxt(etlay: TextInputEditText) {
        etlay.setText("")
    }

}