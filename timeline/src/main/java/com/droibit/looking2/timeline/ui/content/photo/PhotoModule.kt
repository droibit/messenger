package com.droibit.looking2.timeline.ui.content.photo

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
object PhotoModule {

    @Provides
    fun providePhotoListAdapter(fragment: Fragment) =
        PhotoListAdapter(lifecycleOwner = { fragment.viewLifecycleOwner })
}
