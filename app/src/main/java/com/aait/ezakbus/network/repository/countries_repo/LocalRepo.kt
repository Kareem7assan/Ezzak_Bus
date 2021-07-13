package com.aait.ezakbus.network.repository.countries_repo

import android.annotation.SuppressLint
import com.aait.ezakbus.app.AppController.Companion.App
import com.aait.ezakbus.models.countries_model.Country
import com.aait.ezakbus.network.local_db.AppDB
import io.reactivex.Observable

class LocalRepo : RepoCountries {
    @SuppressLint("CheckResult")
    override fun getCountries(): Observable<List<Country>> {
      return  Observable.fromCallable {
            AppDB.getInstance(App).appDao().getCountries()
        }
    }

    override fun saveCountries(countries: List<Country>) {
        AppDB.getInstance(App).appDao().addCountries(countries)
    }

}
