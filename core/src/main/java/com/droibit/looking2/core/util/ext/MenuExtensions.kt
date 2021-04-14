package com.droibit.looking2.core.util.ext

import android.view.Menu
import android.view.MenuItem

fun Menu.add(srcItem: MenuItem): MenuItem {
    return add(
        srcItem.groupId,
        srcItem.itemId,
        srcItem.order,
        srcItem.title
    ).also {
        it.icon = srcItem.icon
    }
}
