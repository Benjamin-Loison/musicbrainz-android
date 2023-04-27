package org.metabrainz.android.model.mbentity

import org.metabrainz.android.data.sources.api.entities.Alias
import java.util.*

class Instrument : org.metabrainz.android.model.mbentity.MBEntity() {
    var type: String? = null
    var name: String? = null
    var description: String? = null
    private val aliases: MutableList<Alias> = ArrayList()
    override fun toString(): String {
        return "Instrument{" +
                "mbid='" + mbid + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", aliases=" + aliases +
                '}'
    }

    fun getAliases(): List<Alias> {
        return aliases
    }

    fun setAliases(aliases: List<Alias>?) {
        this.aliases.addAll(aliases!!)
    }
}