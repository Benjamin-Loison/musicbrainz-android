package org.metabrainz.android.ui.screens.collection

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.metabrainz.android.R
import org.metabrainz.android.databinding.ActivityCollectionDetailsBinding
import org.metabrainz.android.model.mbentity.MBEntityType
import org.metabrainz.android.ui.adapters.ResultAdapter
import org.metabrainz.android.ui.adapters.ResultItem
import org.metabrainz.android.ui.screens.base.MusicBrainzActivity
import org.metabrainz.android.util.Constants
import org.metabrainz.android.util.Resource
import java.util.*

/**
 * Activity to display a list of collection results to the user and support intents
 * to info Activity types based on the selection.
 */
@AndroidEntryPoint
class CollectionDetailsActivity : MusicBrainzActivity() {
    private lateinit var binding: ActivityCollectionDetailsBinding
    private var viewModel: CollectionViewModel? = null
    private var adapter: ResultAdapter? = null
    private var id: String? = null
    private var entity: MBEntityType? = null
    private var collectionResults: MutableList<ResultItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.app_bg)))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.noResult.root.visibility = View.GONE
        binding.progressSpinner.root.visibility = View.VISIBLE
        entity = intent.getSerializableExtra(Constants.TYPE) as MBEntityType?
        id = intent.getStringExtra(Constants.MBID)
        viewModel = ViewModelProvider(this).get(CollectionViewModel::class.java)
        collectionResults = ArrayList()
        adapter = ResultAdapter(collectionResults!!, entity!!)
        adapter!!.resetAnimation()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.visibility = View.GONE
        viewModel!!.fetchCollectionDetails(entity!!, id!!).observe(this) { resource: Resource<List<ResultItem>>? ->
            setResults(
                resource
            )
        }
    }

    private fun refresh() {
        adapter!!.notifyDataSetChanged()
        binding.progressSpinner.root.visibility = View.GONE
        checkHasResults()
    }

    private fun checkHasResults() {
        if (adapter!!.itemCount == 0) {
            binding.recyclerView.visibility = View.GONE
            binding.noResult.root.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noResult.root.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.menu_open_website).isVisible = false
        return true
    }

    override fun getBrowserURI(): Uri {
        return Uri.EMPTY
    }

    private fun setResults(resource: Resource<List<ResultItem>>?) {
        if (resource != null && resource.status === Resource.Status.SUCCESS) {
            collectionResults!!.clear()
            collectionResults!!.addAll(resource.data!!)
        }
        refresh()
    }
}