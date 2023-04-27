package org.metabrainz.android.model.mbentity

import com.google.gson.annotations.SerializedName
import org.metabrainz.android.data.sources.api.entities.Alias
import org.metabrainz.android.data.sources.api.entities.LifeSpan
import java.util.*

class Area : org.metabrainz.android.model.mbentity.MBEntity() {
    //TODO: ISO codes field to be added
    var type: String? = null
    var name: String? = null

    @SerializedName("sort-name")
    var sortName: String? = null
    private val aliases: MutableList<Alias> = ArrayList()

    @SerializedName("life-span")
    var lifeSpan: LifeSpan? = null
    override fun toString(): String {
        return "Area{" +
                "id='" + mbid + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", sortName='" + sortName + '\'' +
                ", aliases=" + aliases +
                ", disambiguation='" + disambiguation + '\'' +
                ", lifeSpan=" + lifeSpan +
                '}'
    }

    fun getAliases(): List<Alias> {
        return aliases
    }

    fun setAliases(aliases: List<Alias>?) {
        this.aliases.addAll(aliases!!)
    }
}