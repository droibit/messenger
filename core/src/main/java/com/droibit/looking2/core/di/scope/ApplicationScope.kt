package com.droibit.looking2.core.di.scope

import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME

@Deprecated("Migrate to dagger hilt.")
@Scope
@Retention(RUNTIME)
annotation class ApplicationScope
