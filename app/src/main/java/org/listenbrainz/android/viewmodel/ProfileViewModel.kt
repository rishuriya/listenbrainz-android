package org.listenbrainz.android.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.listenbrainz.android.model.PinnedRecording
import org.listenbrainz.android.repository.AppPreferences
import org.listenbrainz.android.repository.ListensRepository
import org.listenbrainz.android.util.Resource
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
        val appPreferences: AppPreferences,
        val repository: ListensRepository
) : ViewModel() {
    var isLoading: Boolean  by mutableStateOf(true)
    var isPaused=false
    fun getLoginStatusFlow(): Flow<Int> {
        return flow {
            while (true){
                delay(200)
                emit(appPreferences.loginStatus)
            }
        }.distinctUntilChanged()
    }
    private val _pinnedFlow = MutableStateFlow(listOf<PinnedRecording>())
    val pinnedFlow = _pinnedFlow.asStateFlow()
    private val _followersFlow = MutableStateFlow(listOf<String>())
    val followersFlow = _followersFlow.asStateFlow()
    private var _followingFlow = MutableStateFlow(listOf<String>())
    var followingFlow = _followingFlow.asStateFlow()
    private var _listenCountFlow = MutableStateFlow(0)
    var listenCountFlow = _listenCountFlow.asStateFlow()
    fun userDetails(userName: String){
        viewModelScope.launch {
            val follower = repository.fetchUserFollowers(userName)
            val following = repository.fetchUserFollowing(userName)
            val totalListen= repository.fetchUserTotalListens(userName)
            Log.d("UserDetails", "Followers: ${follower.data?.size}, Following: ${following.data?.size}, Total Listens: ${totalListen.data}")
            isLoading = when(follower.status){
                Resource.Status.SUCCESS -> {
                    _followersFlow.update { follower.data ?: emptyList() }
                    false
                }
                Resource.Status.LOADING -> true
                Resource.Status.FAILED -> false
            }
            isLoading = when(following.status){
                Resource.Status.SUCCESS -> {
                    _followingFlow.update { following.data ?: emptyList() }
                    false
                }
                Resource.Status.LOADING -> true
                Resource.Status.FAILED -> false
            }
            isLoading = when(totalListen.status){
                Resource.Status.SUCCESS -> {
                    _listenCountFlow.update { totalListen.data ?: 0 }
                    false
                }
                Resource.Status.LOADING -> true
                Resource.Status.FAILED -> false
            }
        }
    }

    fun getPinnedSongs(userName: String){
        viewModelScope.launch {
            val pinnedSongs = repository.fetchPinnedSongs(userName)
            Log.d("PinnedSongs", "Pinned Songs: ${pinnedSongs.data?.size}")
            isLoading = when(pinnedSongs.status){
                Resource.Status.SUCCESS -> {
                    _pinnedFlow.update { pinnedSongs.data ?: emptyList() }
                    false
                }
                Resource.Status.LOADING -> true
                Resource.Status.FAILED -> false
            }
        }
    }
    
    
    fun logoutUser(context: Context) {
        appPreferences.logoutUser()
        Toast.makeText(context, "User has successfully logged out.", Toast.LENGTH_SHORT).show()
    }
}