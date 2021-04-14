package com.droibit.looking2.core.util.checker

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PlayServicesChecker @Inject constructor(
    @Named("appContext") private val context: Context,
    private val googleApiAvailability: GoogleApiAvailability
) {

    sealed class Status {
        object Available : Status()
        class Error(
            val errorCode: Int,
            val isUserResolvableError: Boolean
        ) : Status()
    }

    fun checkStatus(): Status {
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        // ref. https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability.html#isGooglePlayServicesAvailable(android.content.Context)
        return if (resultCode == ConnectionResult.SUCCESS) {
            Status.Available
        } else {
            Status.Error(
                resultCode,
                googleApiAvailability.isUserResolvableError(resultCode)
            )
        }
    }

    fun checkStatusCode(code: Int): Status {
        return if (code == ConnectionResult.SUCCESS) {
            Status.Available
        } else {
            Status.Error(
                code,
                googleApiAvailability.isUserResolvableError(code)
            )
        }
    }

    fun showErrorResolutionDialog(
        activity: Activity,
        errorCode: Int,
        requestCode: Int,
        cancelListener: (DialogInterface) -> Unit
    ): Boolean {
        return googleApiAvailability.showErrorDialogFragment(
            activity,
            errorCode,
            requestCode,
            cancelListener
        )
    }
}
