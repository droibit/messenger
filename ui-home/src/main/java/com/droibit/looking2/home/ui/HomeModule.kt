package com.droibit.looking2.home.ui

import androidx.fragment.app.Fragment
import com.droibit.looking2.home.R
import com.droibit.looking2.ui.common.widget.ActionItemListAdapter
import com.droibit.looking2.ui.common.widget.OnActionItemClickListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import javax.inject.Named

@InstallIn(FragmentComponent::class)
@Module
object HomeModule {

    @Named("home")
    @Provides
    fun provideOnActionItemClickListener(fragment: Fragment) =
        fragment as OnActionItemClickListener

    @Named("home")
    @Provides
    fun provide(
        fragment: Fragment,
        @Named("home") itemClickListener: OnActionItemClickListener
    ) = ActionItemListAdapter(
        fragment.requireContext(),
        menuRes = R.menu.home,
        itemClickListener = itemClickListener
    )
}
