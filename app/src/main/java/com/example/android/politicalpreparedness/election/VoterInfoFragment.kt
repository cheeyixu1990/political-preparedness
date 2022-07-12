package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentVoterInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_voter_info, container, false)

        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = VoterInfoViewModelFactory(
            ElectionDatabase.getInstance(
                activity.application
            ).electionDao, args.argElectionId, args.argDivision
        )

        val viewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner



        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        return binding.root

    }

    //TODO: Create method to load URL intents

}