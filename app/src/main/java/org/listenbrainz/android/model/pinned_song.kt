package org.listenbrainz.android.model

data class pinned_song(
        val count: Int,
        val offset: Int,
        val pinned_recordings:List<PinnedRecording>,
        val total_count: Int,
        val user_name: String
)

data class PinnedRecording(
        val blurb_content: String?,
        val created: Long,
        val pinned_until: Long,
        val recording_mbid: String,
        val recording_msid: String?,
        val row_id: Int,
        val track_metadata: TrackMetadata_pinned
)

data class TrackMetadata_pinned(
        val artist_name: String,
        val mbid_mapping: MbidMapping_pinned,
        val release_name: String,
        val track_name: String
)

data class MbidMapping_pinned(
        val artist_mbids: List<String>,
        val artists: List<Artist_pinned>,
        val caa_id: Long,
        val caa_release_mbid: String,
        val recording_mbid: String,
        val release_mbid: String
)

data class Artist_pinned(
        val artist_credit_name: String,
        val artist_mbid: String,
        val join_phrase: String
)

