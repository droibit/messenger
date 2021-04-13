package com.droibit.looking2.core.util.checker

import android.content.Context
import android.support.wearable.phone.PhoneDeviceType
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PhoneDeviceTypeChecker @Inject constructor(
    @Named("appContext") private val context: Context
) {
    fun checkPairedWithAndroidDevice(): Boolean {
        val deviceType = PhoneDeviceType.getPhoneDeviceType(context)
        return deviceType == PhoneDeviceType.DEVICE_TYPE_ANDROID
    }
}