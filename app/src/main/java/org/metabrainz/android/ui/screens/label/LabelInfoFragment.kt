package org.metabrainz.android.ui.screens.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateViewModelFactory
import org.metabrainz.android.databinding.FragmentLabelInfoBinding
import org.metabrainz.android.ui.screens.base.MusicBrainzFragment
import org.metabrainz.android.util.Resource

class LabelInfoFragment : Fragment() {
    private var binding: FragmentLabelInfoBinding? = null
    private val labelViewModel: LabelViewModel by activityViewModels {
        SavedStateViewModelFactory(requireActivity().application, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLabelInfoBinding.inflate(inflater, container, false)
        labelViewModel.data.observe(viewLifecycleOwner) { setLabelInfo(it) }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setLabelInfo(resource: Resource<org.metabrainz.android.model.mbentity.Label>) {
        if (resource.status == Resource.Status.SUCCESS) {
            val label = resource.data
            if (label!!.type != null && label.type!!.isNotEmpty())
                binding!!.labelType.text = label.type
            if (label.lifeSpan != null && label.lifeSpan!!.begin!!.isNotEmpty())
                binding!!.labelFounded.text = label.lifeSpan!!.begin
            if (label.area != null && label.area!!.name!!.isNotEmpty())
                binding!!.labelArea.text = label.area!!.name
            if (label.code != null && label.code!!.isNotEmpty())
                binding!!.labelCode.text = label.code
        }
    }

    companion object : MusicBrainzFragment {
        override fun newInstance(): Fragment {
            return LabelInfoFragment()
        }
    }
}