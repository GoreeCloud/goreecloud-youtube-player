package com.goreecloud.youtubeplayer.provider

sealed interface ProviderResult<out T> {
    data class Success<T>(val value: T) : ProviderResult<T>

    data class Failure(
        val code: String,
        val message: String,
        val retryable: Boolean,
    ) : ProviderResult<Nothing>
}
