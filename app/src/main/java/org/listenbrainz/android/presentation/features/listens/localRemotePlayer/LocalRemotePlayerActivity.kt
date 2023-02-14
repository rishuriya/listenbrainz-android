package org.listenbrainz.android.presentation.features.listens.localRemotePlayer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import org.listenbrainz.android.data.sources.brainzplayer.PlayableType
import org.listenbrainz.android.data.sources.brainzplayer.Song
import org.listenbrainz.android.presentation.features.brainzplayer.ui.BrainzPlayerViewModel
import javax.inject.Inject

@AndroidEntryPoint
class localRemotePlayerActivity: AppCompatActivity(){
    lateinit var brainzPlayerViewModel: BrainzPlayerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent() {
            val intent = intent
            val uri = intent.data

            if (uri != null) {
                brainzPlayerViewModel= hiltViewModel<BrainzPlayerViewModel>()
                var data = parseSongFromUri(this, uri)
                var SongList= mutableListOf<Song>(data)
                brainzPlayerViewModel.changePlayable(SongList, PlayableType.ALL_SONGS, data.mediaID, SongList.sortedBy { data.discNumber }.indexOf(data))
                brainzPlayerViewModel.playOrToggleSong(data, true)
                Log.d("data", data.toString())
            }
        }
    }

    fun parseSongFromUri(context: Context, uri: Uri): Song {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DISC_NUMBER
        )
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val mediaID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val trackNumber = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
            val year = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
            val artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID))
            val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
            val albumID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
            val discNumber = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISC_NUMBER))

            cursor.close()
            var data= Song(
                mediaID = mediaID,
                title = title,
                trackNumber = trackNumber,
                year = year,
                duration = duration,
                dateModified = dateModified,
                artistId = artistId,
                artist = artist,
                uri = uri.toString(),
                albumID = albumID,
                album = album,
                albumArt = "",
                discNumber = discNumber
            )
            return data
        }

        cursor?.close()
        return Song.emptySong
    }
}