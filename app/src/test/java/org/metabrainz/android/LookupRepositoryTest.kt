package org.metabrainz.android

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.metabrainz.android.AssertionUtils.checkArtistAssertions
import org.metabrainz.android.AssertionUtils.checkLabelAssertions
import org.metabrainz.android.AssertionUtils.checkRecordingAssertions
import org.metabrainz.android.AssertionUtils.checkReleaseAssertions
import org.metabrainz.android.AssertionUtils.checkReleaseGroupAssertions
import org.metabrainz.android.EntityTestUtils.loadResourceAsString
import org.metabrainz.android.EntityTestUtils.testArtist
import org.metabrainz.android.EntityTestUtils.testLabel
import org.metabrainz.android.EntityTestUtils.testRecording
import org.metabrainz.android.EntityTestUtils.testRelease
import org.metabrainz.android.EntityTestUtils.testReleaseGroup
import org.metabrainz.android.RetrofitUtils.createTestService
import org.metabrainz.android.repository.LookupRepository
import org.metabrainz.android.repository.LookupRepositoryImpl
import org.metabrainz.android.util.Constants.LOOKUP_ARTIST_PARAMS
import org.metabrainz.android.util.Constants.LOOKUP_LABEL_PARAMS
import org.metabrainz.android.util.Constants.LOOKUP_RECORDING_PARAMS
import org.metabrainz.android.util.Constants.LOOKUP_RELEASE_GROUP_PARAMS
import org.metabrainz.android.util.Constants.LOOKUP_RELEASE_PARAMS
import org.metabrainz.android.service.LookupService
import org.metabrainz.android.model.mbentity.MBEntityType
import org.metabrainz.android.model.mbentity.Recording
import org.metabrainz.android.model.mbentity.Release
import org.metabrainz.android.model.mbentity.ReleaseGroup
import org.metabrainz.android.util.Resource.Status.SUCCESS

class LookupRepositoryTest {

    private lateinit var webServer: MockWebServer
    private lateinit var repository: LookupRepository

    @Before
    fun setup() {
        webServer = MockWebServer()
        webServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val endpoint = request.path?.substring(1, request.path!!.indexOf('/', 1))
                val file = endpoint + "_lookup.json"
                return MockResponse().setResponseCode(200).setBody(loadResourceAsString(file))

                // Head to https://github.com/square/okhttp/tree/master/mockwebserver for documentation
                // Equivalent to
                /*
                    when (request.path) {
                       "/v1/check/version/" -> {
                           return MockResponse().setResponseCode(200).setBody(loadResourceAsString(file))
                       }
                   }
                 */
            }
        }
        webServer.start()
        val service = createTestService(LookupService::class.java, webServer.url("/"))
        repository = LookupRepositoryImpl(service)
    }

    @Test
    fun testArtistLookup() = runBlocking {
        val expected = testArtist
        val resource = repository.fetchData(MBEntityType.ARTIST.entity, expected.mbid!!, LOOKUP_ARTIST_PARAMS)
        assertEquals(SUCCESS, resource.status)
        val actual = Gson().fromJson(resource.data, org.metabrainz.android.model.mbentity.Artist::class.java)
        checkArtistAssertions(expected, actual)
    }

    @Test
    fun testReleaseLookup() = runBlocking {
        val expected = testRelease
        val resource = repository.fetchData(MBEntityType.RELEASE.entity, expected.mbid!!, LOOKUP_RELEASE_PARAMS)
        assertEquals(SUCCESS, resource.status)
        val actual = Gson().fromJson(resource.data, Release::class.java)
        checkReleaseAssertions(expected, actual)
    }

    @Test
    fun testReleaseGroupLookup() = runBlocking {
        val expected = testReleaseGroup
        val resource = repository.fetchData(MBEntityType.RELEASE_GROUP.entity, expected.mbid!!, LOOKUP_RELEASE_GROUP_PARAMS)
        assertEquals(SUCCESS, resource.status)
        val actual = Gson().fromJson(resource.data, ReleaseGroup::class.java)
        checkReleaseGroupAssertions(expected, actual)
    }

    @Test
    fun testLabelLookup() = runBlocking {
        val expected = testLabel
        val resource = repository.fetchData(MBEntityType.LABEL.entity, expected.mbid!!, LOOKUP_LABEL_PARAMS)
        assertEquals(SUCCESS, resource.status)
        val label = Gson().fromJson(resource.data, org.metabrainz.android.model.mbentity.Label::class.java)
        checkLabelAssertions(expected, label)
    }

    @Test
    fun testRecordingLookup() = runBlocking {
        val expected = testRecording
        val resource = repository.fetchData(MBEntityType.RECORDING.entity, expected.mbid!!, LOOKUP_RECORDING_PARAMS)
        assertEquals(SUCCESS, resource.status)
        val recording = Gson().fromJson(resource.data, Recording::class.java)
        checkRecordingAssertions(expected, recording)
    }

    @After
    fun teardown() {
        webServer.close()
    }
}