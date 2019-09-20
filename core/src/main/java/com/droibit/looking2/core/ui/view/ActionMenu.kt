package com.droibit.looking2.core.ui.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import androidx.annotation.StringRes

class ActionMenu(private val context: Context) : Menu {

    private val items: MutableList<ActionMenuItem> = arrayListOf()

    override fun clear() {
        items.clear()
    }

    override fun removeItem(id: Int) {
        val index = items.indexOfFirst { it.itemId == id }
        items.removeAt(index)
    }

    override fun setGroupCheckable(
        group: Int,
        checkable: Boolean,
        exclusive: Boolean
    ) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun performIdentifierAction(
        id: Int,
        flags: Int
    ): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setGroupEnabled(
        group: Int,
        enabled: Boolean
    ) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getItem(index: Int): MenuItem = items[index]

    override fun performShortcut(
        keyCode: Int,
        event: KeyEvent?,
        flags: Int
    ): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun removeGroup(groupId: Int) {
    }

    override fun setGroupVisible(
        group: Int,
        visible: Boolean
    ) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun add(title: CharSequence) = add(0, 0, 0, title)

    override fun add(@StringRes titleRes: Int) = add(0, 0, 0, context.getString(titleRes))

    override fun add(
        groupId: Int,
        itemId: Int,
        order: Int,
        title: CharSequence
    ): MenuItem {
        return ActionMenuItem(context, itemId, title).also {
            items.add(it)
        }
    }

    override fun add(
        groupId: Int,
        itemId: Int,
        order: Int,
        @StringRes titleRes: Int
    ) = add(groupId, itemId, order, context.getString(titleRes))

    override fun isShortcutKey(
        keyCode: Int,
        event: KeyEvent
    ) = false

    override fun setQwertyMode(isQwerty: Boolean) {
    }

    override fun hasVisibleItems() = false

    override fun addSubMenu(title: CharSequence?): SubMenu {
        throw UnsupportedOperationException("not implemented")
    }

    override fun addSubMenu(titleRes: Int): SubMenu {
        throw UnsupportedOperationException("not implemented")
    }

    override fun addSubMenu(
        groupId: Int,
        itemId: Int,
        order: Int,
        title: CharSequence
    ): SubMenu {
        throw UnsupportedOperationException("not implemented")
    }

    override fun addSubMenu(
        groupId: Int,
        itemId: Int,
        order: Int,
        titleRes: Int
    ): SubMenu {
        throw UnsupportedOperationException("not implemented")
    }

    override fun addIntentOptions(
        groupId: Int,
        itemId: Int,
        order: Int,
        caller: ComponentName?,
        specifics: Array<Intent>?,
        intent: Intent?,
        flags: Int,
        outSpecificItems: Array<MenuItem>?
    ): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun findItem(id: Int): MenuItem {
        val index = items.indexOfFirst { it.itemId == id }
        return items[index]
    }

    override fun size() = items.size

    override fun close() {
        throw UnsupportedOperationException("not implemented")
    }
}