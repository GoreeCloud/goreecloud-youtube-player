package com.goreecloud.youtubeplayer.domain

data class Video(
    val providerId: String,
    val providerVideoId: String,
    val title: String,
    val channelName: String? = null,
    val durationSeconds: Long? = null,
)
