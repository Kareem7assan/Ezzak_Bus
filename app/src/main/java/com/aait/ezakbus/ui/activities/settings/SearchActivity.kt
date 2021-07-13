package com.aait.ezakbus.ui.activities.settings

import android.content.Intent
import android.os.Handler
import com.aait.ezakbus.R
import com.aait.ezakbus.base.ParentActivity
import com.aait.ezakbus.ui.activities.core.MapPreviewActivity

class SearchActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_search

    override fun init() {
        Handler().postDelayed({
            startActivity(Intent(this@SearchActivity,
                MapPreviewActivity::class.java))
            finish()
        },1500)
    }


}
