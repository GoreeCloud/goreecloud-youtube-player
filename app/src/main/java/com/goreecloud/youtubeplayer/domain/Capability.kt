package com.goreecloud.youtubeplayer.domain

enum class CapabilityState {
    AVAILABLE,
    SUPPORTED,
    UNSUPPORTED,
    OFFLINE,
    RESTRICTED,
    PERMISSION_REQUIRED,
    TEMPORARILY_UNAVAILABLE,
    DEGRADED,
    DISABLED,
    UNKNOWN,
}

enum class ProviderCapability {
    SEARCH,
    CHANNELS,
    RSS_SUBSCRIPTIONS,
    PLAYBACK,
    CAPTIONS,
    QUALITY_SELECTION,
    LIVE_STREAMS,
    CASTING,
    OFFLINE_MEDIA,
    LOCAL_LIBRARY,
}

data class CapabilityDeclaration(
    val capability: ProviderCapability,
    val state: CapabilityState,
    val reason: String? = null,
)

data class CapabilityDecision(
    val capability: ProviderCapability,
    val state: CapabilityState,
    val reason: String,
) {
    val isActionable: Boolean
        get() = state == CapabilityState.AVAILABLE || state == CapabilityState.SUPPORTED
}
