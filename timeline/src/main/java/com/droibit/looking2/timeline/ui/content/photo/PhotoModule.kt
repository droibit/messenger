package com.droibit.looking2.timeline.ui.content.photo

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object PhotoModule {

    @Named("photoUrls")
    @Provides
    @JvmStatic
    fun providePhotoUrls(fragment: PhotoFragment): List<String> {
        return fragment.args.urls.toList()
    }

    @Provides
    @JvmStatic
    fun providePhotListAdapter(
        fragment: PhotoFragment,
        @Named("photoUrls") photoUrls: List<String>): PhotoListAdapter {
        return PhotoListAdapter(fragment.requireContext(), photoUrls)
    }
}