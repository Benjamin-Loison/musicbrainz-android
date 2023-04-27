package org.metabrainz.android.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.metabrainz.android.application.App
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MusicBrainzServiceGenerator {

    const val API_BASE_URL = "https://musicbrainz.org/ws/2/"
    const val AUTH_BASE_URL = "https://musicbrainz.org/oauth2/"
    const val ACOUST_ID_BASE_URL = "https://api.acoustid.org/v2/lookup"
    const val CLIENT_ID = "DfX8Xv3B5Cbco8nZgd6KYMS504F3gmJx"
    const val CLIENT_SECRET = "XypxMsphalFfTIh_IagjcsIlSKuUWoc-"
    const val OAUTH_REDIRECT_URI = "org.metabrainz.android://oauth"
    const val ACOUST_ID_KEY = "5mgEECwRkp"

    private var authenticator: OAuthAuthenticator? = null
    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private var headerInterceptor: HeaderInterceptor? = null

    private val httpClientBuilder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (hasNetwork(App.context!!)) {
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                }
                else {
                    request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                }
                chain.proceed(request)
            }

    private val builder = Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create())

    private var retrofit = builder.build()

    private fun hasNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    fun <S> createService(service: Class<S>, requiresAuthenticator: Boolean): S {
        headerInterceptor = HeaderInterceptor()
        addInterceptors(headerInterceptor)

        // Authenticator should not be added for requests to refresh token and gaining access token
        if (requiresAuthenticator) addAuthenticator()
        addInterceptors(loggingInterceptor)
        return retrofit.create(service)
    }

    private fun addAuthenticator() {
        authenticator = OAuthAuthenticator()
        httpClientBuilder.authenticator(authenticator!!)
        builder.client(httpClientBuilder.build())
        retrofit = builder.build()
    }

    private fun addInterceptors(interceptor: Interceptor?) {
        if (!httpClientBuilder.interceptors().contains(interceptor)) {
            httpClientBuilder.addInterceptor(interceptor!!)
            builder.client(httpClientBuilder.build())
            retrofit = builder.build()
        }
    }
}