package com.droibit.looking2.core.data.source.local.twitter

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.twitter.sdk.android.core.SessionManager
import com.twitter.sdk.android.core.TwitterSession
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class LocalTwitterSourceTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var sessionManager: SessionManager<TwitterSession>

    @InjectMocks
    private lateinit var localSource: LocalTwitterSource

    @Test
    fun activeSession() {
        val session = mock<TwitterSession>()
        whenever(sessionManager.activeSession)
            .thenReturn(null)
            .thenReturn(session)

        assertThat(localSource.activeSession).isNull()
        assertThat(localSource.activeSession).isEqualTo(session)
    }

    @Test
    fun sessions() {
        val session1 = mock<TwitterSession>()
        val session2 = mock<TwitterSession>()
        val sessionMap = mapOf(
            1L to session1,
            2L to session2
        )
        whenever(sessionManager.sessionMap)
            .thenReturn(emptyMap())
            .thenReturn(sessionMap)

        assertThat(localSource.sessions).isEmpty()
        assertThat(localSource.sessions).containsExactly(session1, session2)
    }

    @Test
    fun getSessionBy_sessionId() {
        val session1 = mock<TwitterSession>()
        val sessionMap = mapOf(1L to session1)
        whenever(sessionManager.sessionMap).doReturn(sessionMap)

        assertThat(localSource.getSessionBy(0L)).isNull()
        assertThat(localSource.getSessionBy(1L)).isEqualTo(session1)
    }

    @Test
    fun setActiveSession() {
        val session = mock<TwitterSession>()
        localSource.setActiveSession(session)

        verify(sessionManager).activeSession = session
    }

    @Test
    fun add() {
        val sessionId = 1L
        val session = mock<TwitterSession> {
            on { this.id } doReturn sessionId
        }

        localSource.add(session)

        verify(sessionManager).setSession(sessionId, session)
    }

    @Test
    fun remove() {
        val sessionId = 1L
        localSource.remove(sessionId)

        verify(sessionManager).clearSession(sessionId)
    }
}