package org.listenbrainz.android.model

data class PlaylistResponse(
        val count: Int,
        val offset: Int,
        val playlist_count: Int,
        val playlists: List<PlaylistItem>
)

data class PlaylistItem(
        val playlist: PlaylistDetails
)

data class PlaylistDetails(
        val annotation: String?,
        val creator: String,
        val date: String,
        val extension: PlaylistExtension,
        val identifier: String,
        val title: String,
        var track: List<Track>
)
