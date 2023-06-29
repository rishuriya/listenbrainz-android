package org.listenbrainz.android.model

data class Follower(
       val follower:List<String> = listOf(),
       val User:String
)

data class total_listen(
       val payload: Count,
)
data class Count(
       val count:Int
)