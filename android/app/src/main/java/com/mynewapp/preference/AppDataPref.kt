package com.mynewapp.preference

import android.content.Context
import android.content.SharedPreferences

class AppDataPref (context: Context){
    private val APP_PREFERENCE = "STEP_APP_PREFERENCE"
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var STEPS ="step"
    private var HEART_RATE = "heart_rate"

    init {
        sharedPreferences = context.getSharedPreferences(
            APP_PREFERENCE,
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences?.edit()
    }

    fun updateStep(value: String?) {
        editor!!.putString(STEPS, value)
        editor!!.apply()
    }

    fun getSteps( ): String? {
        return sharedPreferences!!.getString(STEPS, "0")
    }
    fun updateHeartRate(value: String?) {
        editor!!.putString(HEART_RATE, value)
        editor!!.apply()
    }

    fun getHeartRate(): String? {
        return sharedPreferences!!.getString(HEART_RATE, "0")
    }


}