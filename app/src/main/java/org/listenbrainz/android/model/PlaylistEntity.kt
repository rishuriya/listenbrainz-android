package org.listenbrainz.android.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.listenbrainz.android.R

@Entity(tableName = "PLAYLISTS")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val items: List<SongEntity>,
    var art: String = (R.drawable.ic_queue_music).toString()
)