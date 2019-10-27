package com.droibit.looking2.core.data.source.local

import android.content.SharedPreferences

abstract class PreferenceKey<T>(
    val key: String
) {
    abstract val defaultValue: T
}

class StringPreferenceKey(
    key: String,
    override val defaultValue: String
) : PreferenceKey<String>(key)

class IntPreferenceKey(
    key: String,
    override val defaultValue: Int
) : PreferenceKey<Int>(key)

class IntConvertiblePreferenceKey(
    key: String,
    override val defaultValue: Int
) : PreferenceKey<Int>(key)

fun SharedPreferences.getInt(prefKey: PreferenceKey<Int>): Int {
    if (prefKey is IntConvertiblePreferenceKey) {
        val value = getString(prefKey.key, null) ?: return prefKey.defaultValue
        return value.toInt()
    }
    return getInt(prefKey.key, prefKey.defaultValue)
}

fun SharedPreferences.getString(prefKey: PreferenceKey<String>): String {
    return requireNotNull(getString(prefKey.key, prefKey.defaultValue))
}