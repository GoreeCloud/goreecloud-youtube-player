package com.goreecloud.youtubeplayer.provider

import com.goreecloud.youtubeplayer.domain.CapabilityDeclaration
import com.goreecloud.youtubeplayer.domain.Video

interface ContentProvider {
    val id: String
    val displayName: String

    fun capabilityDeclarations(): Set<CapabilityDeclaration>

    fun search(query: String): ProviderResult<List<Video>>
}
