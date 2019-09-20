package com.droibit.looking2.core.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.ActionProvider
import android.view.ContextMenu
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

class ActionMenuItem(
    private val context: Context,
    private val id: Int,
    private var title: CharSequence
) : MenuItem {

    private var icon: Drawable? = null

    override fun expandActionView(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun hasSubMenu() = false

    override fun getMenuInfo(): ContextMenu.ContextMenuInfo? = null

    override fun getItemId() = id

    override fun getAlphabeticShortcut() = '0'

    override fun setEnabled(enabled: Boolean): MenuItem = this

    override fun setTitle(title: CharSequence): MenuItem {
        this.title = title
        return this
    }

    override fun setTitle(@StringRes title: Int) = setTitle(context.getString(title))

    override fun setChecked(checked: Boolean): MenuItem = this

    override fun getActionView(): View? = null

    override fun getTitle() = title

    override fun getOrder() = 0

    override fun setOnActionExpandListener(listener: MenuItem.OnActionExpandListener): MenuItem {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getIntent(): Intent? = null

    override fun setVisible(visible: Boolean): MenuItem = this

    override fun isEnabled() = false

    override fun isCheckable() = false

    override fun setShowAsAction(actionEnum: Int) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getGroupId() = 0

    override fun setActionProvider(actionProvider: ActionProvider?): MenuItem {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setTitleCondensed(title: CharSequence?): MenuItem = this

    override fun getNumericShortcut(): Char = '0'

    override fun isActionViewExpanded(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun collapseActionView(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun isVisible() = false

    override fun setNumericShortcut(numericChar: Char): MenuItem = this

    override fun setActionView(view: View?): MenuItem {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setActionView(resId: Int): MenuItem {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setAlphabeticShortcut(alphaChar: Char): MenuItem = this

    override fun setIcon(icon: Drawable): MenuItem {
        this.icon = icon
        return this
    }

    override fun setIcon(@DrawableRes iconRes: Int) = setIcon(
        requireNotNull(ContextCompat.getDrawable(context, iconRes))
    )

    override fun isChecked() = false

    override fun setIntent(intent: Intent?): MenuItem {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setShortcut(
        numericChar: Char,
        alphaChar: Char
    ): MenuItem {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getIcon(): Drawable? = icon

    override fun setShowAsActionFlags(actionEnum: Int): MenuItem {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setOnMenuItemClickListener(
        menuItemClickListener: MenuItem.OnMenuItemClickListener
    ): MenuItem = this

    override fun getActionProvider(): ActionProvider? = null

    override fun setCheckable(checkable: Boolean): MenuItem = this

    override fun getSubMenu(): SubMenu? = null

    override fun getTitleCondensed(): CharSequence? = null
}