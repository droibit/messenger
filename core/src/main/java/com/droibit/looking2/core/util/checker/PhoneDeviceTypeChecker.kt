package com.droibit.looking2.core.util.checker

import android.content.Context
import androidx.wear.phone.interactions.PhoneTypeHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneDeviceTypeChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun checkPairedWithAndroidDevice(): Boolean {
        val deviceType = PhoneTypeHelper.getPhoneDeviceType(context)
        return deviceType == PhoneTypeHelper.DEVICE_TYPE_ANDROID
    }
}
