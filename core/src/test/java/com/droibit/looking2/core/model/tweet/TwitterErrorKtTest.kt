package com.droibit.looking2.core.model.tweet

import androidx.work.ListenableWorker
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import androidx.work.ListenableWorker.Result as WorkResult

class TwitterErrorKtTest {

    @Test
    fun retryIfNeeded_retry() {
        val maxRunAttemptCount = 2
        val cause = mock<TwitterError.Network>()

        val worker = mock<ListenableWorker>() {
            on { runAttemptCount } doReturn 0 doReturn 1 doReturn 2
        }

        kotlin.run {
            val result = worker.retryIfNeeded(cause, maxRunAttemptCount)
            assertThat(result).isEqualTo(WorkResult.retry())
        }

        kotlin.run {
            val result = worker.retryIfNeeded(cause, maxRunAttemptCount)
            assertThat(result).isEqualTo(WorkResult.retry())
        }

        kotlin.run {
            val result = worker.retryIfNeeded(cause, maxRunAttemptCount)
            assertThat(result).isEqualTo(WorkResult.failure())
        }
    }

    @Test
    fun retryIfNeeded_failure() {
        val worker = mock<ListenableWorker>()

        kotlin.run {
            val cause = mock<TwitterError.UnExpected>()
            val result = worker.retryIfNeeded(cause)
            assertThat(result).isEqualTo(WorkResult.failure())
        }

        kotlin.run {
            val cause = mock<TwitterError.Limited>()
            val result = worker.retryIfNeeded(cause)
            assertThat(result).isEqualTo(WorkResult.failure())
        }
    }
}