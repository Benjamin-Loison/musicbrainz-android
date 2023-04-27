package org.metabrainz.android.repository

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.metabrainz.android.util.Constants
import org.metabrainz.android.service.LookupService
import org.metabrainz.android.data.sources.api.entities.CoverArt
import org.metabrainz.android.data.sources.api.entities.RecordingItem
import org.metabrainz.android.data.sources.api.entities.WikiDataResponse
import org.metabrainz.android.data.sources.api.entities.WikiSummary
import org.metabrainz.android.model.mbentity.Release
import org.metabrainz.android.util.Resource
import org.metabrainz.android.util.Resource.Status.SUCCESS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LookupRepositoryImpl @Inject constructor(private val service: LookupService) : LookupRepository {

    @WorkerThread
    override suspend fun fetchData(entity: String, MBID: String, params: String?): Resource<String> {
        return try {
            val data = service.lookupEntityData(entity, MBID, params!!)
            Resource(SUCCESS, data.string())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.failure()
        }
    }

    @WorkerThread
    override suspend fun fetchWikiSummary(string: String, method: Int): Resource<WikiSummary> {
        return when (method) {
            LookupRepository.METHOD_WIKIPEDIA_URL -> fetchWiki(string)
            else -> fetchWikiData(string)
        }
    }

    @WorkerThread
    private suspend fun fetchWiki(title: String): Resource<WikiSummary> {
        return try {
            val data = service.getWikipediaSummary(title)
            Resource(SUCCESS, data)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.failure()
        }
    }

    @WorkerThread
    private suspend fun fetchWikiData(id: String): Resource<WikiSummary> {
        return try {
            val responseBody = service.getWikipediaLink(id)
            val jsonResponse = responseBody.string()
            val result = JsonParser.parseString(jsonResponse).asJsonObject.getAsJsonObject("entities").getAsJsonObject(id)
            val wikiDataResponse = Gson().fromJson(result, WikiDataResponse::class.java)
            val title = wikiDataResponse.sitelinks!!["enwiki"]?.title
            if (title != null) {
                fetchWiki(title)
            }
            else {
                Resource.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.failure()
        }
    }

    @WorkerThread
    override suspend fun fetchCoverArt(MBID: String?): Resource<CoverArt> {
        return try {
            val coverArt = service.getCoverArt(MBID)
            Resource(SUCCESS, coverArt)
        }
        catch (e: Exception) {
            e.printStackTrace()
            Resource.failure()
        }
    }

    @WorkerThread
    override suspend fun fetchRecordings(artist: String?, title: String?): Resource<List<RecordingItem>> {
        return try {
            val data = service.searchRecording(artist, title)
            Resource(SUCCESS, data)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.failure()
        }
    }

    @WorkerThread
    override suspend fun fetchMatchedRelease(MBID: String?): Resource<Release> {
        return try {
            val data = service.lookupRecording(MBID, Constants.TAGGER_RELEASE_PARAMS)
            Resource(SUCCESS, data)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.failure()
        }
    }
}