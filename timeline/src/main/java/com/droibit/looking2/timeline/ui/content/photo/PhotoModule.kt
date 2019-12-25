package com.droibit.looking2.timeline.ui.content.photo

import androidx.lifecycle.LifecycleOwner
import dagger.Lazy
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object PhotoModule {

    @Provides
    fun provideLifecycleOwner(fragment: PhotoFragment): LifecycleOwner {
        return fragment.viewLifecycleOwner
    }

    @Named("photoUrls")
    @Provides
    fun providePhotoUrls(fragment: PhotoFragment): List<String> {
        return fragment.args.urls.toList()
    }

    @Provides
    fun providePhotoListAdapter(
        fragment: PhotoFragment,
        lifecycleOwner: Lazy<LifecycleOwner>,
        @Named("photoUrls") photoUrls: List<String>
    ): PhotoListAdapter {
        return PhotoListAdapter(fragment.requireContext(), lifecycleOwner, photoUrls)
    }
}