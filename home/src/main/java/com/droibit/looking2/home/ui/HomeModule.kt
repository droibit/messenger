package com.droibit.looking2.home.ui

import androidx.fragment.app.Fragment
import com.droibit.looking2.core.ui.widget.ActionItemListAdapter
import com.droibit.looking2.core.ui.widget.OnActionItemClickListener
import com.droibit.looking2.home.R
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
        menuRes = R.menu.navigation,
        itemClickListener = itemClickListener
    )
}
