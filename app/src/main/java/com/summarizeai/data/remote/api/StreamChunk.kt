package com.summarizeai.data.remote.api

data class StreamChunk(
    val content: String,
    val done: Boolean
)
