package com.droibit.looking2.timeline.ui.content

import java.io.Serializable

sealed class TimelineSource : Serializable {
    object Home : TimelineSource()
    object Mentions : TimelineSource()
    class MyList(val listId: Long) : TimelineSource()
}