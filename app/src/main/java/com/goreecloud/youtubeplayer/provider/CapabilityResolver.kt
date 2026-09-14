package com.goreecloud.youtubeplayer.provider

import com.goreecloud.youtubeplayer.domain.CapabilityDecision
import com.goreecloud.youtubeplayer.domain.CapabilityState
import com.goreecloud.youtubeplayer.domain.ProviderCapability

class CapabilityResolver {
    fun resolve(provider: ContentProvider, capability: ProviderCapability): CapabilityDecision {
        val declaration = provider.capabilityDeclarations().firstOrNull {
            it.capability == capability
        }

        if (declaration == null) {
            return CapabilityDecision(
                capability = capability,
                state = CapabilityState.UNKNOWN,
                reason = "Provider ${provider.id} did not declare this capability.",
            )
        }

        return CapabilityDecision(
            capability = capability,
            state = declaration.state,
            reason = declaration.reason
                ?: "Provider ${provider.id} declared ${declaration.state.name.lowercase()}.",
        )
    }
}
