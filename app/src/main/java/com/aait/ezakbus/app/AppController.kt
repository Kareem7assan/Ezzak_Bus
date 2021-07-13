package com.aait.ezakbus.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.aait.ezakbus.modules.appModule
import com.aait.ezakbus.utils.GlobalPreferences
import com.aait.ezakbus.utils.Util
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.Locale

class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        App = this
        mPrefs = GlobalPreferences(this)
        startKoin {
            androidContext(this@AppController)
            modules(appModule)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun attachBaseContext(newBase: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
               var mPrefs = GlobalPreferences(newBase)
            val context = Util.changeLang(mPrefs.lang!!, newBase)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }

    }

    companion object {

        val TAG = AppController::class.java.simpleName
        lateinit var App: AppController
        @get:Synchronized
        var instance: AppController? = null
            private set

        lateinit var mPrefs: GlobalPreferences
        lateinit var lang: String
        val language: String
            get() {
                val language = Locale.getDefault().displayLanguage
                if (language.equals("English", ignoreCase = true)) {
                    lang = "en"
                } else {
                    lang = "ar"
                }
                return lang

            }
    }

}
