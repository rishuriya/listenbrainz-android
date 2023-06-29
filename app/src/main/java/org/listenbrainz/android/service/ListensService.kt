package org.listenbrainz.android.service

import okhttp3.ResponseBody
import org.listenbrainz.android.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ListensService {
    @GET("1/user/{user_name}/listens")
    suspend fun getUserListens(@Path("user_name") user_name: String, @Query("count") count: Int): Listens

    @GET("1/user/{user_name}/followers")
    suspend fun getUserFollowers(@Path("user_name") user_name: String): Follower

    @GET("1/user/{user_name}/following")
    suspend fun getUserFollowing(@Path("user_name") user_name: String): Following

    @GET("1/user/{user_name}/listen-count")
    suspend fun getUserListensCount(@Path("user_name") user_name: String): total_listen

    @GET("1/{user_name}/pins")
    suspend fun getPinnedSongs(@Path("user_name") user_name: String): pinned_song

    @GET("http://coverartarchive.org/release/{MBID}")
    suspend fun getCoverArt(@Path("MBID") MBID: String): CoverArt

    @GET("1/validate-token")
    suspend fun checkIfTokenIsValid(@Header("Authorization") token: String?): TokenValidation

    @POST("1/submit-listens")
    fun submitListen(@Header("Authorization") token: String?,
                     @Body body: ListenSubmitBody?): Call<ResponseBody?>?

    @GET("1/user/{user_name}/services")
    suspend fun getServicesLinkedToAccount(
        @Header("Authorization") authHeader: String?,
        @Path("user_name") user_name: String,
    ): ListenBrainzExternalServices
}