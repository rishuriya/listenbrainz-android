package org.listenbrainz.android.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.NotLoggedInException
import com.spotify.android.appremote.api.error.UserNotAuthorizedException
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerContext
import com.spotify.protocol.types.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.listenbrainz.android.BuildConfig
import org.listenbrainz.android.model.Listen
import org.listenbrainz.android.model.ListenBitmap
import org.listenbrainz.android.model.PinnedRecording
import org.listenbrainz.android.model.PlaylistItem
import org.listenbrainz.android.repository.AppPreferences
import org.listenbrainz.android.repository.ListensRepository
import org.listenbrainz.android.service.YouTubeApiService
import org.listenbrainz.android.util.Constants
import org.listenbrainz.android.util.LinkedService
import org.listenbrainz.android.util.Log.d
import org.listenbrainz.android.util.Log.e
import org.listenbrainz.android.util.Resource.Status.FAILED
import org.listenbrainz.android.util.Resource.Status.LOADING
import org.listenbrainz.android.util.Resource.Status.SUCCESS
import org.listenbrainz.android.util.Utils.getSHA1
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class ListensViewModel @Inject constructor(
    val repository: ListensRepository,
    val appPreferences: AppPreferences,
    private val application: Application
) : AndroidViewModel(application) {
    // TODO: remove dependency of this view-model on application
    //  by moving spotify app remote to a repository.

    private val _listensFlow = MutableStateFlow(listOf<Listen>())
    val listensFlow = _listensFlow.asStateFlow()
    private val _isSpotifyLinked = MutableStateFlow(appPreferences.linkedServices.contains(LinkedService.SPOTIFY))
    val isSpotifyLinked = _isSpotifyLinked.asStateFlow()
    private val _playlistFlow = MutableStateFlow(listOf<PlaylistItem>())
    val playlistFlow = _playlistFlow.asStateFlow()
    var isLoading: Boolean  by mutableStateOf(true)
    var isPaused=false
    var playerState: PlayerState? by mutableStateOf(null)
    private val _songDuration = MutableStateFlow(0L)
    private val _songCurrentPosition = MutableStateFlow(0L)
    private val _progress = MutableStateFlow(0F)
    var bitmap: ListenBitmap = ListenBitmap()
    val progress = _progress.asStateFlow()
    val songCurrentPosition = _songCurrentPosition.asStateFlow()

    private var playerStateSubscription: Subscription<PlayerState>? = null
    private var playerContextSubscription: Subscription<PlayerContext>? = null
    var spotifyAppRemote: SpotifyAppRemote? = null

    private val errorCallback = { throwable: Throwable -> logError(throwable) }
    private var isResumed = false

    init {
        SpotifyAppRemote.setDebugMode(BuildConfig.DEBUG)
        trackProgress()
    }

    suspend fun validateUserToken(token: String): Boolean? {
        return repository.validateUserToken(token).data?.valid
    }

    suspend fun retrieveUsername(token: String): String? {
        return repository.validateUserToken(token).data?.user_name
    }
    
    fun fetchLinkedServices() {
        viewModelScope.launch {
            val token = appPreferences.lbAccessToken
            val userName = appPreferences.username
            if (!token.isNullOrEmpty() && !userName.isNullOrEmpty()){
                val result = repository.getLinkedServices(token = token, username = userName)
                _isSpotifyLinked.emit(result.contains(LinkedService.SPOTIFY))
                appPreferences.linkedServices = result
            }
        }
    }

    fun fetchUserListens(userName: String) {
        viewModelScope.launch {
            val response = repository.fetchUserListens(userName)
            isLoading = when(response.status){
                SUCCESS -> {
                    // Updating listens
                    _listensFlow.update { response.data ?: emptyList() }
                    false
                }
                LOADING -> true
                FAILED -> false
            }
        }
    }
    fun fetchUserPlaylist(userName: String) {
        viewModelScope.launch {
            val response = repository.fetchUserplaylist(userName)
            isLoading = when(response.status){
                SUCCESS -> {
                    // Updating playlist
                    for (data in response.data!!) {
                        Log.d("mbid",data.playlist.identifier.substring(34))
                        val details = repository.fetchPlaylistInfo(data.playlist.identifier.substring(34))
                        isLoading= when(details.status){
                            SUCCESS -> {
                                //Log.d("OnlinePlaylist",details.data.toString())
                                data.playlist.track = details.data!!
                                false
                            }
                            LOADING -> true
                            FAILED -> false
                        }
                    }
                    Log.d("OnlinePlaylist",response.data.toString())
                    _playlistFlow.update { response.data ?: emptyList() }
                    false
                }
                LOADING -> true
                FAILED -> false
            }
        }
    }

    suspend fun searchYoutubeMusicVideoId(context: Context, trackName: String, artist: String, apiKey: String): String? {
        val packageName = context.packageName
        val sha1 = getSHA1(context, packageName)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Android-Package", packageName)
                    .addHeader("X-Android-Cert", sha1 ?: "")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(YouTubeApiService::class.java)

        return try {
            val response = service.searchVideos(
                "snippet",
                "$trackName $artist",
                "video",
                "10",
                apiKey
            )

            if (response.isSuccessful) {
                val items = response.body()?.items ?: emptyList()
                if (items.isNotEmpty()) {
                    items[0].id.videoId
                } else {
                    null
                }
            } else {
                Log.e("YouTube API Error", response.errorBody()?.string() ?: "")
                null
            }
        } catch (e: Exception) {
            Log.e("YouTube API Error", "Error occurred while searching for video ID", e)
            null
        }
    }

    private fun updateTrackCoverArt(playerState: PlayerState) {
        // Get image from track
        assertAppRemoteConnected()?.imagesApi?.getImage(playerState.track.imageUri, com.spotify.protocol.types.Image.Dimension.LARGE)?.setResultCallback { bitmapHere ->
            bitmap =ListenBitmap(
                bitmap=bitmapHere,
                id = playerState.track.uri
            )
        }
    }

    private fun onConnected() {
        onSubscribedToPlayerStateButtonClicked()
        onSubscribedToPlayerContextButtonClicked()
    }

    fun connect(spotifyClientId: String) {
        SpotifyAppRemote.disconnect(spotifyAppRemote)
        viewModelScope.launch {
            try {
                spotifyAppRemote = connectToAppRemote(true, spotifyClientId = spotifyClientId)
                onConnected()
            } catch (error: Throwable) {
                logError(error)
            }
        }
    }

    private val playerContextEventCallback = Subscription.EventCallback<PlayerContext> { playerContext ->

    }

    private val playerStateEventCallback = Subscription.EventCallback<PlayerState> { playerStateHere ->
        playerState = playerStateHere
    }

    private suspend fun connectToAppRemote(showAuthView: Boolean, spotifyClientId: String): SpotifyAppRemote =
        suspendCoroutine { cont: Continuation<SpotifyAppRemote> ->
            SpotifyAppRemote.connect(
                application,
                ConnectionParams.Builder(spotifyClientId)
                    .setRedirectUri(Constants.SPOTIFY_REDIRECT_URI)
                    .showAuthView(showAuthView)
                    .build(),
                object : Connector.ConnectionListener {
                    override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                        if (!isResumed) {
                            d("App remote Connected!")
                            cont.resume(spotifyAppRemote)
                            isResumed = true
                        }
                    }

                    override fun onFailure(error: Throwable) {
                        if (error is CouldNotFindSpotifyApp) {
                            // TODO: Tell user that they need to install the spotify app on the phone
                        }

                        if (error is NotLoggedInException) {
                            // TODO: Tell user that they need to login in the spotify app
                        }

                        if (error is UserNotAuthorizedException) {
                            // TODO: Explicit user authorization is required to use Spotify.
                            //  The user has to complete the auth-flow to allow the app to use Spotify on their behalf
                        }
                        if (!isResumed) {
                            cont.resumeWithException(error)
                            isResumed = true
                        }
                    }
                }
            )
        }

    fun playUri(uri: String) {
        assertAppRemoteConnected()?.playerApi?.play(uri)?.setResultCallback {
            logMessage("play command successful!")      //getString(R.string.command_feedback, "play"))
        }?.setErrorCallback(errorCallback)
        trackProgress()
        isPaused=false
    }
    fun play(){
        assertAppRemoteConnected()?.playerApi?.resume()?.setResultCallback {
            logMessage("play command successful!")      //getString(R.string.command_feedback, "play"))
        }?.setErrorCallback(errorCallback)
        isPaused=false
        trackProgress()
    }

    fun pause(){
        assertAppRemoteConnected()?.playerApi?.pause()?.setResultCallback {
            logMessage("pause command successful!")      //getString(R.string.command_feedback, "play"))
        }?.setErrorCallback(errorCallback)
        isPaused=true
        trackProgress()
    }
    fun trackProgress() {
        var state: PlayerState? = null
        assertAppRemoteConnected()?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            if(bitmap.id!=playerState.track.uri) {
                updateTrackCoverArt(playerState)
                state=playerState
            }
        }?.setErrorCallback(errorCallback)
        viewModelScope.launch(Dispatchers.Default) {
            do {
                state = assertAppRemoteConnected()?.playerApi?.playerState?.await()?.data
                val pos = state?.playbackPosition?.toFloat() ?: 0f
                val duration=state?.track?.duration ?: 1
                if (progress.value != pos) {
                    _progress.emit(pos / duration.toFloat())
                    _songDuration.emit(duration ?: 0)
                    _songCurrentPosition.emit(((pos / duration) * duration).toLong())
                }
                delay(900L)
            }while (!isPaused)
        }
    }

    fun seekTo(pos:Float,state: PlayerState?){
        val duration=state?.track?.duration ?: 1
        val position=(pos*duration).toLong()
        assertAppRemoteConnected()?.playerApi?.seekTo(position)?.setResultCallback {
            logMessage("seek command successful!")      //getString(R.string.command_feedback, "play"))
        }?.setErrorCallback(errorCallback)
        viewModelScope.launch(Dispatchers.Default) {
            if (progress.value != pos) {
                _progress.emit(pos / duration.toFloat())
                _songDuration.emit(duration ?: 0)
                _songCurrentPosition.emit(((pos / duration) * duration).toLong())
            }
        }
    }

    private fun onSubscribedToPlayerContextButtonClicked() {
        playerContextSubscription = cancelAndResetSubscription(playerContextSubscription)
        playerContextSubscription = assertAppRemoteConnected()?.playerApi?.subscribeToPlayerContext()?.setEventCallback(playerContextEventCallback)?.setErrorCallback { throwable ->
            logError(throwable)
        } as Subscription<PlayerContext>
    }

    private fun onSubscribedToPlayerStateButtonClicked() {
        playerStateSubscription = cancelAndResetSubscription(playerStateSubscription)
        playerStateSubscription = assertAppRemoteConnected()?.playerApi?.subscribeToPlayerState()?.setEventCallback(playerStateEventCallback)?.setLifecycleCallback(
            object : Subscription.LifecycleCallback {
                override fun onStart() {
                    logMessage("Event: start")
                }

                override fun onStop() {
                    logMessage("Event: end")
                }
            })?.setErrorCallback {

        } as Subscription<PlayerState>
    }

    private fun <T : Any?> cancelAndResetSubscription(subscription: Subscription<T>?): Subscription<T>? {
        return subscription?.let {
            if (!it.isCanceled) {
                it.cancel()
            }
            null
        }
    }

    private fun assertAppRemoteConnected(): SpotifyAppRemote? {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                return it
            }
        }
        logMessage("Spotify is not Connected. Use one of the 'connect' buttons")        //getString(R.string.err_spotify_disconnected))
        return null
    }

    private fun logError(throwable: Throwable) {
        throwable.message?.let { e(it) }
    }

    private fun logMessage(msg: String) {
        d(msg)
    }
}
