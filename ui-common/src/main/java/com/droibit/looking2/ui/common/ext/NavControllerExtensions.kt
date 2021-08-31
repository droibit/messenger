package com.droibit.looking2.ui.common.ext

import androidx.navigation.NavBackStackEntry
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

fun NavController.requireCurrentBackStackEntry(): NavBackStackEntry {
    return requireNotNull(currentBackStackEntry) {
        "NavController($this) does not have any currentBackStackEntry."
    }
}

fun NavController.requirePreviousBackStackEntry(): NavBackStackEntry {
    return requireNotNull(previousBackStackEntry) {
        "NavController($this) does not have any previousBackStackEntry."
    }
}
