package org.metabrainz.android.model.entities

import com.google.gson.annotations.SerializedName
import org.metabrainz.android.model.mbentity.Recording

class Track {
    @SerializedName("id")
    var mbid: String? = null
    var title: String? = null
    var position = 0
    var recording: Recording? = null
    var length: Long = 0
    val duration: String
        get() {
            val builder = StringBuilder()
            var seconds = length / 1000
            val minutes = seconds / 60
            seconds %= 60
            builder.append(minutes).append(':')
            if (seconds < 10) builder.append('0')
            builder.append(seconds)
            return builder.toString()
        }
}