package com.goreecloud.youtubeplayer.provider.local

import com.goreecloud.youtubeplayer.domain.CapabilityDeclaration
import com.goreecloud.youtubeplayer.domain.CapabilityState
import com.goreecloud.youtubeplayer.domain.ProviderCapability
import com.goreecloud.youtubeplayer.domain.Video
import com.goreecloud.youtubeplayer.provider.ContentProvider
import com.goreecloud.youtubeplayer.provider.ProviderResult

/**
 * Deterministic, network-free provider used only to exercise GoreeCloud-owned
 * application architecture during development. It is not a YouTube provider.
 */
class LocalDemoProvider : ContentProvider {
    override val id: String = "local-demo"
    override val displayName: String = "Local Development Provider"

    private val videos = listOf(
        Video(
            providerId = id,
            providerVideoId = "architecture-tour",
            title = "Native architecture foundation",
            channelName = "GoreeCloud Development",
            durationSeconds = 420,
        ),
        Video(
            providerId = id,
            providerVideoId = "capability-model",
            title = "Capability-aware provider boundaries",
            channelName = "GoreeCloud Development",
            durationSeconds = 360,
        ),
    )

    override fun capabilityDeclarations(): Set<CapabilityDeclaration> = setOf(
        CapabilityDeclaration(
            capability = ProviderCapability.SEARCH,
            state = CapabilityState.SUPPORTED,
            reason = "Deterministic local search is available for development.",
        ),
        CapabilityDeclaration(
            capability = ProviderCapability.LOCAL_LIBRARY,
            state = CapabilityState.SUPPORTED,
            reason = "Local development content is available without a network.",
        ),
        CapabilityDeclaration(
            capability = ProviderCapability.PLAYBACK,
            state = CapabilityState.UNSUPPORTED,
            reason = "Playback is intentionally not implemented in the bootstrap provider.",
        ),
    )

    override fun search(query: String): ProviderResult<List<Video>> {
        val normalized = query.trim()
        val matches = if (normalized.isEmpty()) {
            videos
        } else {
            videos.filter { video ->
                video.title.contains(normalized, ignoreCase = true) ||
                    video.channelName?.contains(normalized, ignoreCase = true) == true
            }
        }
        return ProviderResult.Success(matches)
    }
}
