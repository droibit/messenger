package com.droibit.looking2.core.util.ext

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import timber.log.Timber

fun NavController.navigateSafely(directions: NavDirections) {
    if (currentDestination?.getAction(directions.actionId) == null) {
        Timber.w("Action corresponding to Directions($directions) could not be found.")
        return
    }
    navigate(directions)
}