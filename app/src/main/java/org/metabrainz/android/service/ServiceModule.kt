package org.metabrainz.android.service

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.metabrainz.android.service.MusicBrainzServiceGenerator.createService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @get:Provides
    @get:Singleton
    val lookupService: LookupService = createService(LookupService::class.java, true)

    @get:Provides
    @get:Singleton
    val blogService: BlogService = Retrofit.Builder()
        .baseUrl("https://public-api.wordpress.com/rest/v1.1/sites/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(BlogService::class.java)

    @get:Provides
    @get:Singleton
    val collectionService: CollectionService = createService(CollectionService::class.java, true)

    @get:Provides
    @get:Singleton
    val loginService: LoginService = createService(LoginService::class.java, false)
}