package kr.co.baress.octavecheck.utility

import android.app.Application

class FrequencyRepository(application: Application) {
    private val frequencyDao: FrequencyDao by lazy {
        val db = FrequencyDB.getInstance(application)!!
        db.FrequencyDao()
    }
}