package org.listenbrainz.android.ui.screens.yim

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideLazyListPreloader
import org.listenbrainz.android.R
import org.listenbrainz.android.model.YimScreens
import org.listenbrainz.android.ui.components.ListenCardSmall
import org.listenbrainz.android.ui.components.SimilarUserCard
import org.listenbrainz.android.ui.components.YimLabelText
import org.listenbrainz.android.ui.components.YimNavigationStation
import org.listenbrainz.android.ui.theme.LocalYimPaddings
import org.listenbrainz.android.ui.theme.YearInMusicTheme
import org.listenbrainz.android.ui.theme.YimPaddings
import org.listenbrainz.android.util.Utils.getCoverArtUrl
import org.listenbrainz.android.viewmodel.YimViewModel

@Composable
fun YimDiscoverScreen(
    yimViewModel: YimViewModel,
    navController: NavController,
    paddings: YimPaddings = LocalYimPaddings.current
){
    YearInMusicTheme(redTheme = false) {
        var startAnim by remember{
            mutableStateOf(false)
        }
    
        LaunchedEffect(Unit) {
            startAnim = true
        }
        
        // Main Content
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(state = rememberScrollState())
            .testTag(stringResource(id = R.string.tt_yim_discover_parent)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            YimLabelText(heading = "Discover", subHeading = "The year's over, but there's still more to uncover!")
    
            // New Albums from your top artists Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 600.dp)
                    .padding(
                        start = paddings.defaultPadding,
                        end = paddings.defaultPadding,
                        bottom = 50.dp
                    ),
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                // Inside Card Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Magnifier Image
                    Image(
                        painter = painterResource(id = R.drawable.yim_magnifier),
                        modifier = Modifier
                            .padding(top = paddings.defaultPadding)
                            .size(100.dp),
                        contentDescription = null
                    )
            
                    // Heading Text
                    Text(
                        text = "New albums from top artists",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = paddings.defaultPadding)
                            .padding(vertical = paddings.smallPadding)
                    )
            
                    AnimatedVisibility(
                        visible = startAnim,
                        enter = expandVertically(animationSpec = tween(durationMillis = 700, delayMillis = 1200))
                    ) {
                        YimTopAlbumsFromArtistsList(viewModel = yimViewModel)
                    }
            
                }
            }
            
            // Music Buddies
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 600.dp)
                    .padding(
                        horizontal = paddings.defaultPadding
                    ),
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                // Inside Card Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Buddies Image
                    Image(
                        painter = painterResource(id = R.drawable.yim_buddy),
                        modifier = Modifier
                            .padding(top = paddings.defaultPadding)
                            .size(100.dp),
                        contentDescription = null
                    )
            
                    // Heading Text
                    Text(
                        text = "Music buddies",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = paddings.defaultPadding)
                            .padding(vertical = paddings.smallPadding)
                    )
            
                    AnimatedVisibility(
                        visible = startAnim,
                        enter = expandVertically(animationSpec = tween(durationMillis = 700, delayMillis = 1900))
                    ) {
                        YimSimilarUsersList(yimViewModel = yimViewModel)
                    }
            
                }
            }
    
            // Share Button and next
            YimNavigationStation(
                navController = navController,
                viewModel = yimViewModel,
                typeOfImage = arrayOf(),        // No share button here
                modifier = Modifier.padding(vertical = 40.dp),
                route = YimScreens.YimEndgameScreen
            )
            
        }
    }
}

@Composable
private fun YimSimilarUsersList(
    yimViewModel: YimViewModel,
    paddings: YimPaddings = LocalYimPaddings.current
) {
    val similarUsers = remember {
        yimViewModel.getSimilarUsers() ?: listOf()
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(
            horizontal = paddings.smallPadding,
            vertical = paddings.smallPadding
        )
    ) {
        itemsIndexed(similarUsers) { index, item ->
            SimilarUserCard(
                index = index,
                userName = item.first,
                similarity = item.second.toFloat(),
                cardBackGround = MaterialTheme.colorScheme.surface,
                uiModeIsDark = false
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun YimTopAlbumsFromArtistsList(
    viewModel: YimViewModel,
    paddings: YimPaddings = LocalYimPaddings.current,
) {
    val uriList = arrayListOf<String>()
    val newReleasesOfTopArtist = remember {
        viewModel.getNewReleasesOfTopArtists()
    }
    
    newReleasesOfTopArtist!!.forEach { item ->
        uriList.add(getCoverArtUrl(
            caaReleaseMbid = item.caaReleaseMbid,
            caaId = item.caaId
        ))
    }
    
    // Pre-loading images
    val listState = rememberLazyListState()
    GlideLazyListPreloader(
        state = listState,
        data = uriList,
        size = Size(75f,75f),
        numberOfItemsToPreload = 15,
        fixedVisibleItemCount = 5
    ){ item, requestBuilder ->
        requestBuilder.load(item).placeholder(R.drawable.ic_coverartarchive_logo_no_text).override(75)
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(
            horizontal = paddings.smallPadding,
            vertical = paddings.smallPadding
        )
    ) {
        items(newReleasesOfTopArtist.size) { index ->
            ListenCardSmall(
                releaseName = newReleasesOfTopArtist[index].title,
                artistName = newReleasesOfTopArtist[index].artistCreditName,
                coverArtUrl = getCoverArtUrl(
                    caaReleaseMbid = newReleasesOfTopArtist[index].caaReleaseMbid,
                    caaId = newReleasesOfTopArtist[index].caaId
                ),
                onClick = {},
                errorAlbumArt = R.drawable.ic_erroralbumart
            )
        }
    }
}