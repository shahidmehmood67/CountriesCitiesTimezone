package com.sm.android.countries.cities.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.sm.android.countries.cities.countries.LocationDetails
import com.sm.android.countries.cities.utils.Constants.DEFAULT_APP_LANGUAGE_CODE
import java.util.Locale
import com.sm.android.countries.cities.utils.Keys.CITY_NAME
import com.sm.android.countries.cities.utils.Keys.DEFAULT_APP_LANGUAGE
import com.sm.android.countries.cities.utils.Keys.HIJRI_CORRECTION
import com.sm.android.countries.cities.utils.Keys.LOCATION_DETAILS
import com.sm.android.countries.cities.utils.Keys.QURAN_LAST_RAED

class PreferenceHelper(context: Context) {
    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences("IslamicApp", Context.MODE_PRIVATE)
    }

    fun putInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return preferences.getInt(key, defaultValue)
    }

    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String? {
        return preferences.getString(key, defaultValue)
    }

    fun setLanguageKey(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getLanguageKey(key: String, defaultValue: String): String? {
        return preferences.getString(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun putFloat(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return preferences.getFloat(key, defaultValue)
    }

    fun setDefaultAppLanguage(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getDefaultAppLanguage(key: String, defaultValue: String): String? {
        return preferences.getString(key, defaultValue)
    }

    fun setLocale(c: Context): Context {
        return updateResources(c, getDefaultLanguage())
    }

    private fun getDefaultLanguage(): String {
        return preferences.getString(DEFAULT_APP_LANGUAGE, DEFAULT_APP_LANGUAGE_CODE).toString()
    }

    fun setNewLocale(context: Context, language: String): Context {
        setDefaultLanguage(language)
        return updateResources(context, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        var mContext = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = mContext.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocaleForApi24(config, locale)
            mContext = mContext.createConfigurationContext(config)
        } else
            config.setLocale(locale)
        mContext = mContext.createConfigurationContext(config)
        return mContext
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocaleForApi24(configuration: Configuration, locale: Locale) {
        val set: MutableSet<Locale> = LinkedHashSet()
        set.add(locale)
        val all = LocaleList.getDefault()
        for (i in 0 until all.size()) {
            set.add(all[i])
        }
        val locales = set.toTypedArray()
        configuration.setLocales(LocaleList(*locales))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getLocale(res: Resources): Locale? {
        val config: Configuration = res.configuration
        return config.locales.get(0)
    }

    fun setDefaultLanguage(language: String) {
        preferences.edit().putString(DEFAULT_APP_LANGUAGE, language.split("-")[0]).apply()
    }

    fun saveLocationDetails(locationDetails: LocationDetails) {
        preferences.edit().putString(
            LOCATION_DETAILS, Gson().toJson(locationDetails)
        ).apply()
    }

    fun getLocationDetails(): String? {
        return preferences.getString(LOCATION_DETAILS, "")
    }

    fun saveCityName(cityName: String) {
        preferences.edit().putString(
            CITY_NAME, cityName
        ).apply()
    }

    fun getCityName(): String? {
        return preferences.getString(CITY_NAME, "")
    }

    fun saveHijriCorrection(hijriCorrection: Int) {
        preferences.edit().putInt(HIJRI_CORRECTION, hijriCorrection).apply()
    }

    fun getHijriCorrection(): Int {
        return preferences.getInt(HIJRI_CORRECTION, 0)
    }

//    fun saveLastRead(quranLastRead: FavoriteVerse) {
//        preferences.edit().putString(
//            QURAN_LAST_RAED, Gson().toJson(quranLastRead)
//        ).apply()
//    }

    fun getLastRead(): String? {
        return preferences.getString(QURAN_LAST_RAED, "")
    }
}