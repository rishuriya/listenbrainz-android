package org.listenbrainz.android.model

import androidx.annotation.DrawableRes
import org.listenbrainz.android.R
import kotlin.random.Random

data class Playlist(
    var id: Long = Random.nextLong(),
    var title: String = "",
    var items: List<Song> = listOf(),
    var art: String = (R.drawable.ic_queue_music).toString()
) {
    companion object {
        val currentlyPlaying = Playlist(
            id = -1,
            title = "Currently Playing",
            items = emptyList(),
            art = (R.drawable.ic_queue_music_playing).toString()
        )

        val favourite = Playlist(
            id = 0,
            title = "Favourite",
            items = emptyList(),
            art = (R.drawable.ic_liked).toString()
        )

        val recentlyPlayed = Playlist(
            id = 1,
            title = "Recently Played",
            items = emptyList()
        )
    }
}