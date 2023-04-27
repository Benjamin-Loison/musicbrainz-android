package org.metabrainz.android.repository

import org.metabrainz.android.data.sources.api.entities.CoverArt
import org.metabrainz.android.data.sources.api.entities.RecordingItem
import org.metabrainz.android.data.sources.api.entities.WikiSummary
import org.metabrainz.android.model.mbentity.Release
import org.metabrainz.android.util.Resource

interface LookupRepository {

    suspend fun fetchData(entity: String, MBID: String, params: String?): Resource<String>

    suspend fun fetchWikiSummary(string: String, method: Int): Resource<WikiSummary>

    suspend fun fetchCoverArt(MBID: String?): Resource<CoverArt>

    suspend fun fetchRecordings(artist: String?, title: String?): Resource<List<RecordingItem>>

    suspend fun fetchMatchedRelease(MBID: String?): Resource<Release>

    companion object {
        const val METHOD_WIKIPEDIA_URL = 0
        const val METHOD_WIKIDATA_ID = 1
    }
}