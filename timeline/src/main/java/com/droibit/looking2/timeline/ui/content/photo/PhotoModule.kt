package com.droibit.looking2.timeline.ui.content.photo

import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Provider

@Module
object PhotoModule {

    @Named("fragment")
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
        @Named("fragment") lifecycleOwner: Provider<LifecycleOwner>,
        @Named("photoUrls") photoUrls: List<String>
    ): PhotoListAdapter {
        return PhotoListAdapter(
            LayoutInflater.from(fragment.requireContext()),
            lifecycleOwner,
            photoUrls
        )
    }
}
