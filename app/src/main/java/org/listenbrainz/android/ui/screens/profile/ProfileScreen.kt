package org.listenbrainz.android.ui.screens.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.listenbrainz.android.R
import org.listenbrainz.android.ui.screens.dashboard.ProfilePage
import org.listenbrainz.android.ui.screens.listens.ListensScreen
import org.listenbrainz.android.util.Constants.Strings.STATUS_LOGGED_IN
import org.listenbrainz.android.util.Utils
import org.listenbrainz.android.viewmodel.ProfileViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileScreen(
    context: Context = LocalContext.current,
    viewModel: ProfileViewModel = hiltViewModel(),
    shouldScrollToTop: MutableState<Boolean>
) {
    val scrollState = rememberScrollState()
    // Scroll to the top when shouldScrollToTop becomes true
    var _cover_img= MutableStateFlow("")
    val loginStatus = viewModel.getLoginStatusFlow()
            .collectAsState(initial = viewModel.appPreferences.loginStatus, context = Dispatchers.Default)
            .value
    LaunchedEffect(shouldScrollToTop.value) {
        if (shouldScrollToTop.value) {
            scrollState.animateScrollTo(0)
            shouldScrollToTop.value = false
        }
        viewModel.appPreferences.username.let { username ->
            if (username != null) {
                viewModel.userDetails(userName = username)
                viewModel.getPinnedSongs(userName = username)
            }
        }
    }
    val followers = viewModel.followersFlow.collectAsState().value.size
    val following = viewModel.followingFlow.collectAsState().value.size
    val totalListen = viewModel.listenCountFlow.collectAsState().value
    val pinnedSong = viewModel.pinnedFlow.collectAsState().value
    if(pinnedSong.size>0) {
        val cover_img = Utils.getCoverArtUrl(
                caaReleaseMbid = pinnedSong.first().track_metadata.mbid_mapping.caa_release_mbid,
                caaId = pinnedSong.first().track_metadata.mbid_mapping.caa_id
        )
        _cover_img.value=cover_img
    }
    when(loginStatus) {
        STATUS_LOGGED_IN -> {
            ProfilePage(
                    followers=followers,
                    following = following,
                    totalListen = totalListen,
                    background = _cover_img.value
            )
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                val comp by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.login))
                LottieAnimation(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f),
                    composition = comp,
                    iterations = LottieConstants.IterateForever,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSurface)
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Text(
                    text = stringResource(id = R.string.login_prompt),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp, start = 10.dp, end = 10.dp)
                )
            }
        }
    }
}