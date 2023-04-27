package org.metabrainz.android.ui.screens.release

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import org.metabrainz.android.application.App
import org.metabrainz.android.R
import org.metabrainz.android.model.mbentity.Release
import org.metabrainz.android.ui.screens.base.LookupActivity
import org.metabrainz.android.ui.screens.base.MusicBrainzFragment
import org.metabrainz.android.util.Constants
import org.metabrainz.android.ui.screens.links.LinksFragment
import org.metabrainz.android.ui.screens.links.LinksViewModel
import org.metabrainz.android.ui.screens.userdata.UserDataFragment
import org.metabrainz.android.ui.screens.userdata.UserViewModel
import org.metabrainz.android.util.Log

@AndroidEntryPoint
class ReleaseActivity : LookupActivity<Release>() {

    private val releaseViewModel: ReleaseViewModel by viewModels()
    private val linksViewModel: LinksViewModel by viewModels {
        SavedStateViewModelFactory(application, this)
    }
    private val userViewModel: UserViewModel by viewModels {
        SavedStateViewModelFactory(application, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mbid = intent.getStringExtra(Constants.MBID)
        if (mbid != null && mbid.isNotEmpty()) {
            releaseViewModel.mbid.value = mbid
            Log.d(mbid)
        }
        releaseViewModel.data.observe(this) { processData(it) }
    }

    override fun setData(data: Release) {
        supportActionBar?.title = data.title
        userViewModel.setUserData(data)
        linksViewModel.setData(data.relations)
    }

    override fun getTabsList(): List<Int> = listOf(R.string.tab_info, R.string.tab_mediums, R.string.tab_links, R.string.tab_edits)

    override fun getFragmentsList(): List<MusicBrainzFragment> = listOf(
        ReleaseInfoFragment,
            ReleaseTracksFragment, LinksFragment, UserDataFragment
    )

    override fun getBrowserURI(): Uri {
        val mbid = releaseViewModel.mbid.value ?: return Uri.EMPTY
        return Uri.parse(App.WEBSITE_BASE_URL + "release/" + mbid)
    }

}