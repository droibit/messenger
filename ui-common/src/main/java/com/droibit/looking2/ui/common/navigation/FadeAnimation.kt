package com.droibit.looking2.ui.common.navigation

import androidx.fragment.R
import androidx.navigation.AnimBuilder

fun AnimBuilder.default() {
    enter = R.animator.fragment_open_enter
    exit = R.animator.fragment_open_exit
    popEnter = R.animator.fragment_fade_enter
    popExit = R.animator.fragment_fade_exit
}
