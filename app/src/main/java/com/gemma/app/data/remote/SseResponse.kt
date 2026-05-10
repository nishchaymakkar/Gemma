package com.gemma.app.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SseResponse(
    val id: String?,
    val choices: List<Choice>?
)

@JsonClass(generateAdapter = true)
data class Choice(
    val delta: Delta?
)

@JsonClass(generateAdapter = true)
data class Delta(
    val content: String?
)
