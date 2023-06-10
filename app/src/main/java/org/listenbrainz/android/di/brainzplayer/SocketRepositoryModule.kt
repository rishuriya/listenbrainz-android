package org.listenbrainz.android.di.brainzplayer

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.listenbrainz.android.repository.SocketRepository
import org.listenbrainz.android.repository.SocketRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class SocketRepositoryModule {
    @Binds
    abstract fun bindsSocketRepository(repository: SocketRepositoryImpl?) : SocketRepository?
}