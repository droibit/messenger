package com.droibit.looking2.core.ui.dialog

import android.content.DialogInterface
import androidx.annotation.IdRes
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.util.Event
import javax.inject.Inject

inline class DialogId(@IdRes val value: Int)

class DialogViewModel(private val eventSink: MutableLiveData<Event<DialogAction>>) : ViewModel() {

    val event: LiveData<Event<DialogAction>> = eventSink

    @Inject
    constructor() : this(eventSink = MutableLiveData())

    @UiThread
    fun doAction(
        id: DialogId,
        which: Int,
        arg: Any? = null
    ) {
        eventSink.value = Event(DialogAction(id, which, arg))
    }

    data class DialogAction(
        val id: DialogId,
        val which: Int,
        val arg: Any? = null
    ) {
        val isOk = which == DialogInterface.BUTTON_POSITIVE
    }
}