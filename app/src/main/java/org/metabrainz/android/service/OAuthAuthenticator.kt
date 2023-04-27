package org.metabrainz.android.service

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.metabrainz.android.service.MusicBrainzServiceGenerator.createService
import org.metabrainz.android.ui.screens.login.LoginSharedPreferences
import org.metabrainz.android.util.Log.d
import java.io.IOException

internal class OAuthAuthenticator : Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        val service = createService(LoginService::class.java, false)
        d("OkHttp : " + response.body.string())
        val refreshToken = LoginSharedPreferences.refreshToken
        val call = service.refreshAccessToken(refreshToken,
                "refresh_token",
            MusicBrainzServiceGenerator.CLIENT_ID,
            MusicBrainzServiceGenerator.CLIENT_SECRET
        )
        val newResponse = call!!.execute()
        val token = newResponse.body()
        if (token != null) {
            val editor = LoginSharedPreferences.preferences.edit()
            editor.putString(LoginSharedPreferences.REFRESH_TOKEN, token.refreshToken)
            editor.putString(LoginSharedPreferences.ACCESS_TOKEN, token.accessToken)
            editor.apply()
        }
        return when {
            token?.accessToken != null -> response.request
                .newBuilder()
                .header("Authorization", "Bearer " + token.accessToken)
                .build()
            else -> null
        }
    }
}