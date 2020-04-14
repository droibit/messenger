package com.droibit.looking2.core.util.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController

fun <T> Fragment.setResult(key: String, value: T) {
    val previousBackStackEntry = findNavController().requirePreviousBackStackEntry()
    previousBackStackEntry.savedStateHandle.set(key, value)
}

fun <T> Fragment.getResultLiveData(key: String, removeOnChanged: Boolean = true): LiveData<T> {
    val currentBackStackEntry = findNavController().requireCurrentBackStackEntry()
    val resultLiveData = currentBackStackEntry.savedStateHandle.getLiveData<T>(key)
    if (!removeOnChanged) {
        return resultLiveData
    }

    return MediatorLiveData<T>().apply {
        addSource(resultLiveData) {
            this.value = it
            currentBackStackEntry.savedStateHandle.remove<T>(key)
        }
    }
}