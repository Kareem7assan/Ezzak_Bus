package com.aait.ezakbus.network.repository.countries_repo

import android.annotation.SuppressLint
import com.aait.ezakbus.models.countries_model.Country
import com.aait.ezakbus.network.remote_db.RetroWeb
import com.aait.ezakbus.utils.Util
import io.reactivex.Observable

class RemoteRepo : RepoCountries {



    @SuppressLint("CheckResult")
    override fun getCountries(): Observable<List<Country>> {

        val map = RetroWeb.serviceApi.getCountries(Util.language).map {
            val countries = it.data!!.countries
            countries
        }
        return map
    }

    override fun saveCountries(countries: List<Country>) {

    }


}

