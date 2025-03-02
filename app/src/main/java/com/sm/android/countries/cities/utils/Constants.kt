package com.sm.android.countries.cities.utils

import android.content.Context
import android.graphics.Typeface
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.sm.android.countries.cities.R

val Context.prefManager: PreferenceHelper get() = PreferenceHelper(this)


object Constants {
    const val BASE_URL = "https://fakestoreapi.com/"
    const val MAKKAH_LATITUDE = 21.4241
    const val MAKKAH_LONGITUDE = 39.8173
    const val MAKKAH_CITY_NAME = "Mecca"
    const val MAKKAH_COUNTRY_NAME = "Saudi Arabia"

    const val DEFAULT_APP_LANGUAGE_CODE = "en"

    const val IS_FROM_SPLASH = "IS_FROM_SPLASH"

    const val LOCATION_REQUEST_INTERVAL = 3000L
    const val LOCATION_REQUEST_FASTEST_INTERVAL = 1500L
    const val LOCATION_REQUEST_MAX_WAIT_TIME = 4000L

    const val DEFAULT_ISLAMIC_MONTH_DATE = 0

    const val RANDOM_NOTIFICATION_KEY = "random_notification"

    const val FAJR_PRAYER = "fajr"
    const val SUNRISE_PRAYER = "sunrise"
    const val DHUHR_PRAYER = "dhuhr"
    const val ASR_PRAYER = "asr"
    const val SUNSET_PRAYER = "sunset"
    const val MAGHRIB_PRAYER = "maghrib"
    const val ISHA_PRAYER = "isha"
    const val IMSAK_PRAYER = "imsak"
    const val FIRST_THIRD_PRAYER = "firstthird"
    const val MIDNIGHT_PRAYER = "midnight"
    const val LAST_THIRD_PRAYER = "lastthird"

    const val TRANSLATION_FOLDER = "translations"
    const val DEFAULT_SURAH_VERSE = 0

    const val QIBLA_ANIMATION_X_Y_VALUE = 0.5f
    const val QIBLA_ANIMATION_DURATION = 500L

    const val ARABIC_FONT_SIZE = 24
    const val ENGLISH_FONT_SIZE = 14
    const val TRANSLATION_FONT_SIZE = 14
    const val SHARE_ARABIC_FONT_SIZE = 12
    const val SHARE_TRANSLATION_FONT_SIZE = 10
    const val DIFFERENCE_OF_SURAH_SIZE = 2

    const val NOTIFICATION_CHANNEL_ID = "almimin_001"

    const val MESSAGE = "Wow I found a beautiful Islamic App on PlayStore. Hurry up download, Install, and Share it with others"
    const val APP_LINK = "https://play.google.com/store/apps/details?id="

    fun saveHijriCorrection(context: Context, hijriCorrection: Int) {
        context.prefManager.saveHijriCorrection(hijriCorrection)
    }

    fun getHijriCorrection(context: Context): Int {
        return context.prefManager.getHijriCorrection()
    }

    fun getListAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.list_anim)

    fun getSurahTypeFace(context: Context): Typeface =
        Typeface.createFromAsset(context.assets, "Font/surah.ttf")
}