package com.example.echelon

import android.app.Application
import com.cloudinary.android.MediaManager

class EchelonApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = mapOf(
            "cloud_name" to "dwinwjlhl",
            "secure" to true
        )
        MediaManager.init(this, config)
    }
}
