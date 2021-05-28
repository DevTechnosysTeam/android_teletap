package com.teletap.utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.*

class LanguagePreference(context: Context) {
    private var languagePreference1 : LanguagePreference? = null
    private var shared_Preferences12: SharedPreferences? = null

    fun getInstance(context: Context): LanguagePreference {
        if (languagePreference1 == null)
            languagePreference1 = LanguagePreference(context)
        return languagePreference1 as LanguagePreference
    }

    init {
        shared_Preferences12 = context.getSharedPreferences("language_preference", Context.MODE_PRIVATE)
    }

    /*private fun LanguagePreference1(context: Context) {
        shared_Preferences12 = context.getSharedPreferences("language_preference", Context.MODE_PRIVATE)
    }*/

    fun saveLanguageType(value: Boolean) {
        val prefsEditor = shared_Preferences12!!.edit()
        //prefsEditor.putBoolean("isThai", value)
        prefsEditor.putBoolean("isArabic", value)
        prefsEditor.apply()
    }

     fun getLanguageType(): Boolean {
        return if (shared_Preferences12 != null) {
            shared_Preferences12!!.getBoolean("isArabic", false)
            //return shared_Preferences12.getBoolean("isArabic", false);
        } else false
    }

    //Changing Language
    fun ChooseLanguage(Language: String, activity: Activity, intent: Intent?) {
        val locale = Locale(Language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity.baseContext.resources.updateConfiguration(config,
                activity.baseContext.resources.displayMetrics)
        activity.startActivity(intent)
        activity.finish()
    }

    fun languageType(context: Context): String? {
        //boolean status = SharedPreferenceUtility.getInstance().get(ConstantValues.IS_ARABIC, false);
        val status = getInstance(context).getLanguageType()
        val mStatus: String
        mStatus = if (status) "ar" else "en"
        return mStatus
    }
}