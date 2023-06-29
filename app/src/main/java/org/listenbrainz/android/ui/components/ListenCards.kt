package org.listenbrainz.android.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import org.listenbrainz.android.R
import org.listenbrainz.android.model.Listen
import org.listenbrainz.android.model.Playlist
import org.listenbrainz.android.ui.theme.lb_purple
import org.listenbrainz.android.ui.theme.offWhite
import org.listenbrainz.android.ui.theme.onScreenUiModeIsDark

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListenCard(listen: Listen, coverArtUrl: String, onItemClicked: (listen: Listen) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onItemClicked(listen) },
        elevation = 0.dp,
        backgroundColor = if (onScreenUiModeIsDark()) Color.Black else offWhite,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            GlideImage(
                model = coverArtUrl,
                modifier = Modifier.size(80.dp, 80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentDescription = null
            ){
                it.placeholder(R.drawable.ic_coverartarchive_logo_no_text).override(250)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = listen.track_metadata.track_name,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = if (onScreenUiModeIsDark()) Color.White else lb_purple,
                    fontWeight = FontWeight.Bold,
                    style = typography.subtitle1
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildString {
                        append(listen.track_metadata.artist_name)
                    },
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = if (onScreenUiModeIsDark()) Color.White else lb_purple.copy(alpha = 0.7f),
                    style = typography.caption
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = listen.track_metadata.release_name ?: "",
                        modifier = Modifier.padding(0.dp, 12.dp, 12.dp, 0.dp),
                        color = if (onScreenUiModeIsDark()) Color.White else lb_purple.copy(alpha = 0.7f),
                        style = typography.caption
                    )
                }
            }
/*  Love/Hate Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_heart_broken_24),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp, 16.dp),
                    tint = Color.Red
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_heart_broken_24),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp, 16.dp),
                    tint = Color.Red
                )
            }
*/
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ListenCardSmall(
    modifier: Modifier = Modifier,
    releaseName: String,
    artistName: String,
    coverArtUrl: String,
    /** Default is 75 as it consume less internet if images are being fetched from a URL.
     *
     *  Best is 200*/
    imageLoadSize: Int = 75,
    useSystemTheme: Boolean = false,    // TODO: remove this when YIM is removed
    @DrawableRes
    errorAlbumArt: Int = R.drawable.ic_coverartarchive_logo_no_text,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = true) { onClick() },
        shape = RoundedCornerShape(5.dp),
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            // Album cover art
            GlideImage(
                model = coverArtUrl,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Fit,
                contentDescription = "Album Cover Art"
            ) {
                it.placeholder(errorAlbumArt)
                    .override(imageLoadSize)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier) {
                androidx.compose.material3.Text(
                    text = releaseName,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                        .copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (onScreenUiModeIsDark() && useSystemTheme) Color.White else lb_purple,
                            lineHeight = 14.sp
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                androidx.compose.material3.Text(
                    text = artistName,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        .copy(
                            fontWeight = FontWeight.Bold,
                            color = (if (onScreenUiModeIsDark() && useSystemTheme) Color.White else lb_purple).copy(alpha = 0.7f)
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ProfileCardSmall(
        modifier: Modifier = Modifier,
        releaseName: String,
        artistName: String,
        coverArtUrl: String,
        imageLoadSize: Int = 75,
        useSystemTheme: Boolean = false,    // TODO: remove this when YIM is removed
        @DrawableRes
        errorAlbumArt: Int = R.drawable.ic_coverartarchive_logo_no_text,
        onClick: () -> Unit,
) {
    Surface(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable(enabled = true) { onClick() },
            shape = RoundedCornerShape(5.dp),
            shadowElevation = 5.dp
    ) {
        Row(
                modifier = Modifier
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
        ) {

            // Album cover art
            GlideImage(
                    model = coverArtUrl,
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Fit,
                    contentDescription = "Album Cover Art"
            ) {
                it.placeholder(errorAlbumArt)
                        .override(imageLoadSize)
            }

            Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
            ) {
                Column(modifier = Modifier
                        .width(230.dp)) {
                    androidx.compose.material3.Text(
                            text = releaseName,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                                    .copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (onScreenUiModeIsDark() && useSystemTheme) Color.White else lb_purple,
                                            lineHeight = 14.sp
                                    ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                    )
                    androidx.compose.material3.Text(
                            text = artistName,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                                    .copy(
                                            fontWeight = FontWeight.Bold,
                                            color = (if (onScreenUiModeIsDark() && useSystemTheme) Color.White else lb_purple).copy(alpha = 0.7f)
                                    ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                                .fillMaxHeight()
                                .padding(top=3.dp)
                ){
                    Icon(
                            painter = painterResource(id = R.drawable.ic_not_liked),
                            contentDescription = "Like Icon",
                            modifier = Modifier.size(25.dp),
                            tint = Color(0xFFB2B2B2)
                    )
                    Icon(
                            imageVector = Icons.Outlined.HeartBroken,
                            contentDescription = "Dislike Icon",
                            modifier = Modifier.size(25.dp),
                            tint = Color(0xFFB2B2B2)
                    )
                    Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "More Icon",
                            modifier = Modifier.size(35.dp),
                            tint = Color(0xFFB2B2B2)
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ProfileCardBig(
        playlist: Playlist,
) {
    Box(
            modifier = Modifier
                    .padding(2.dp)
                    .padding(top = 5.dp, bottom = 5.dp, start = 2.dp, end = 2.dp)
    ) {
        Row {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 5.dp,
                        modifier = Modifier
                                .size(130.dp)
                ) {
                    AsyncImage(
                            modifier = Modifier
                                    .fillMaxSize()
                                    .background(colorResource(id = R.color.bp_bottom_song_viewpager), RoundedCornerShape(10.dp)),
                            model = playlist.art,
                            contentDescription = "",
                            error = forwardingPainter(
                                    painter = painterResource(id = R.drawable.ic_queue_music_playing)
                            ) { info ->
                                inset(25f, 25f) {
                                    with(info.painter) {
                                        draw(size, info.alpha, info.colorFilter)
                                    }
                                }
                            },
                            contentScale = ContentScale.Fit
                    )
                }
                androidx.compose.material3.Text(
                        text = playlist.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
                Text(
                        text = playlist.items.size.toString() + " Songs",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ShimmerAnimation() {
    val ShimmerColorShades = listOf(
            Color.LightGray.copy(0.9f),
            Color.LightGray.copy(0.2f),
            Color.LightGray.copy(0.9f)
    )
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(


                    // Tween Animates between values over specified [durationMillis]
                    tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
            )
    )

    val brush = Brush.linearGradient(
            colors = ShimmerColorShades,
            start = Offset(10f, 10f),
            end = Offset(translateAnim, translateAnim)
    )
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Surface(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            shape = RoundedCornerShape(5.dp),
            shadowElevation = 5.dp
    ) {
        Row(
                modifier = Modifier
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                    modifier = Modifier.size(70.dp)
                            .padding(10.dp)
                            .background(brush = brush),
            ) {
            }
            Box(modifier = Modifier
                    .width(screenWidth-70.dp)
                    .height(70.dp)
                    .padding(10.dp)
                    .background(brush = brush)
            )
            {

            }
        }
    }
}
