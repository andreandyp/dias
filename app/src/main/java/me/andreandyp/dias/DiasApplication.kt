package me.andreandyp.dias

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class DiasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}