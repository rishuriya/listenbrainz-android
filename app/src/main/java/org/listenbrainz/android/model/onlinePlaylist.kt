package org.listenbrainz.android.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.google.gson.annotations.SerializedName

data class onlinePlaylist(
    val creator: String,
    val date: String,
    val extension: PlaylistExtension,
    val identifier: String,
    val title: String,
    val track: List<Track>
)

data class PlaylistExtension(
        @SerializedName("https://musicbrainz.org/doc/jspf#playlist")
        val jspf: PlaylistJspf
)
data class PlaylistJspf(
        val creator: String,
        val last_modified_at: String,
        val public: Boolean
)

data class Track(
        val creator: String,
        val extension: TrackExtension,
        val identifier: String,
        val title: String,
        var img: ImageBitmap
)

data class TrackExtension(
        @SerializedName("https://musicbrainz.org/doc/jspf#track")
        val jspf: TrackJspf
)

data class TrackJspf(
        val added_at: String,
        val added_by: String,
        val additional_metadata: AdditionalMetadata?,
        val artist_identifiers: List<String>
)

data class AdditionalMetadata(
        val caa_id: Long,
        val caa_release_mbid: String
)
data class playlistData(
        val playlist: onlinePlaylist
)
