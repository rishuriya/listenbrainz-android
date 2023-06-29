package org.listenbrainz.android.model

data class Following(
    val following: List<String> = listOf(),
    val User: String
)
