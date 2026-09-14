package com.goreecloud.youtubeplayer.provider

import com.goreecloud.youtubeplayer.domain.CapabilityState
import com.goreecloud.youtubeplayer.domain.ProviderCapability
import com.goreecloud.youtubeplayer.provider.local.LocalDemoProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CapabilityResolverTest {
    private val provider = LocalDemoProvider()
    private val resolver = CapabilityResolver()

    @Test
    fun declaredSupportedCapabilityIsActionable() {
        val decision = resolver.resolve(provider, ProviderCapability.SEARCH)

        assertEquals(CapabilityState.SUPPORTED, decision.state)
        assertTrue(decision.isActionable)
    }

    @Test
    fun declaredUnsupportedCapabilityStaysUnavailable() {
        val decision = resolver.resolve(provider, ProviderCapability.PLAYBACK)

        assertEquals(CapabilityState.UNSUPPORTED, decision.state)
        assertFalse(decision.isActionable)
    }

    @Test
    fun undeclaredCapabilityFailsToUnknown() {
        val decision = resolver.resolve(provider, ProviderCapability.CASTING)

        assertEquals(CapabilityState.UNKNOWN, decision.state)
        assertFalse(decision.isActionable)
    }
}
