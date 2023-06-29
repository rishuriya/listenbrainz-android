package org.listenbrainz.android.ui.screens.dashboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.listenbrainz.android.R
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import coil.compose.AsyncImage
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.listenbrainz.android.model.Playlist
import org.listenbrainz.android.ui.components.*
import org.listenbrainz.android.util.Utils
import org.listenbrainz.android.viewmodel.PlaylistViewModel
import org.listenbrainz.android.viewmodel.*


class TriangleShape : Shape {
    override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)//Top Right corner
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, 300f)
            close()
        }
        return Outline.Generic(path)
    }
}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfilePage(
        viewModel: ListensViewModel = hiltViewModel(),
        followers:Int =0,
        following:Int = 0,
        totalListen: Int =0,
        background: String=""
) {
    Log.d("ProfilePage", "ProfilePage: $background")
    LaunchedEffect(Unit){
        viewModel.appPreferences.username.let {username ->
            if (username != null) {
                viewModel.fetchUserListens(userName = username)
            }
        }
    }
    val navHostController = rememberNavController()
    val playlistViewModel = hiltViewModel<PlaylistViewModel>()
    val playlists by playlistViewModel.playlists.collectAsState(initial = listOf())
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.White) }
    val recentlyPlayed = Playlist.recentlyPlayed
    val listens = viewModel.listensFlow.collectAsState().value
    val recentSong=recentlyPlayed.items + listens
    val imageResId = R.drawable.cover_img

    LaunchedEffect(background) {
        try {
            val bitmap = withContext(Dispatchers.IO) {
                Glide.with(context)
                        .asBitmap()
                        .load(background)
                        .submit()
                        .get()
            }
            val palette = Palette.Builder(bitmap).generate()
            dominantColor = Color(palette.getDominantColor(Color.White.toArgb()))
        } catch (e: Exception) {
            // Handle the exception, e.g., show an error message
        }
    }
    LazyColumn  (
            modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                            Brush.verticalGradient(
                                    colors = listOf(
                                            dominantColor,
                                            dominantColor.compositeOver(Color.White),
                                            Color.White
                                    ),
                                    startY = 0f,
                            )
                    )
    ) {
        item {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(TriangleShape())
                ) {
                    // Content inside the box
                    GlideImage(
                            model = background,
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "Album Cover Art"
                    ) {
                        it.placeholder(imageResId)
                                .override(200)
                    }
                }
                Surface(
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 5.dp,
                        modifier = Modifier
                                .width(330.dp)
                                .height(200.dp)
                                .zIndex(1f)
                                .align(Alignment.BottomCenter)
                                .background(Color.White, RoundedCornerShape(20.dp))
                ) {
                    Column {
                        Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 60.dp, start = 20.dp, end = 20.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Text(
                                    text = viewModel.appPreferences.username.toString(),
                                    color = Color.Black,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                            )
                            Icon(
                                    painter = painterResource(id = R.drawable.ic_user),
                                    contentDescription = "Edit",
                                    tint = Color.Black,
                                    modifier = Modifier
                                            .padding(start = 10.dp)
                                            .size(30.dp)
                            )
                        }
                        Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                        text = followers.toString(),
                                        color = Color.Black,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        text = "Followers",
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Light
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                        text = following.toString(),
                                        color = Color.Black,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        text = "Following",
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Light
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                        text = totalListen.toString(),
                                        color = Color.Black,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        text = "Listens",
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Row(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                        text = "TASTE",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                                .padding(end = 8.dp, start = 8.dp)
                )
                Text(
                        text = "PLAYLISTS",
                        color = Color(0xFFFFA500),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                                .padding(end = 8.dp, start = 8.dp)
                )
            }
            LazyRow {
                items(playlists.filter {
                    it.id != (-1).toLong() && it.id != (1).toLong()
                }) {
                    ProfileCardBig(
                            playlist=it
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.End,
            modifier = Modifier
                    .fillMaxWidth()) {
                Text(
                        text = "RECENTLY PLAYED",
                        color = Color(0xFFFFA500),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                                .padding(top = 4.dp, start = 20.dp, end = 20.dp)
                )
            }
        }
        if (listens.isEmpty()) {
            items(3) {
                ShimmerAnimation()
            }
        } else {
            items(listens.take(10)) { song ->
                ProfileCardSmall(
                        modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 0.dp)
                                .fillMaxWidth(),
                        releaseName = song.track_metadata.track_name,
                        artistName = song.track_metadata.artist_name,
                        coverArtUrl = Utils.getCoverArtUrl(
                                caaReleaseMbid = song.track_metadata.mbid_mapping?.caa_release_mbid,
                                caaId = song.track_metadata.mbid_mapping?.caa_id
                        ),
                        imageLoadSize = 200,
                        useSystemTheme = true,
                        errorAlbumArt = R.drawable.ic_erroralbumart
                ) {}
            }
        }
    }
}

@Preview
@Composable
fun ShowCom(){
    ProfilePage()
}