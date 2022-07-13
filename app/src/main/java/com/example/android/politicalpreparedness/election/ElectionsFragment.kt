package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentElectionSavedBinding
import com.example.android.politicalpreparedness.databinding.FragmentElectionUpcomingBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionClickListener
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val TAB_TITLES = arrayOf(
    R.string.tab_text_upcoming,
    R.string.tab_text_saved
)

private const val TAG = "PPElectionsFragment"

class ElectionsFragment: Fragment() {

    lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentElectionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_election, container, false)


        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        val viewModelFactory = ElectionsViewModelFactory(
            ElectionDatabase.getInstance(
                activity.application
            ).electionDao
        )

        val viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)


        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        val viewPager: ViewPager2 = binding.viewPager

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> UpcomingElectionsFragment()
                    else -> SavedElectionsFragment()
                }

            }

            override fun getItemCount(): Int {
                return 2
            }
        }
        val tabs: TabLayout = binding.tabs

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = getString(TAB_TITLES[position])
        }.attach()

        return binding.root

    }

    //TODO: Refresh adapters when fragment loads

}

class UpcomingElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionUpcomingBinding
    private lateinit var electionListAdapter: ElectionListAdapter

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "In UpcomingElectionsFragment onDetach")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "In UpcomingElectionsFragment onViewStateRestored")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "In UpcomingElectionsFragment onResume")
        electionListAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "In UpcomingElectionsFragment onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "In UpcomingElectionsFragment onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "In UpcomingElectionsFragment onDestroy")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentElectionUpcomingBinding.inflate(layoutInflater)

        val sharedElectionsViewModel: ElectionsViewModel by viewModels({ requireParentFragment() })


        electionListAdapter = ElectionListAdapter(
            ElectionClickListener{
                Log.d(TAG, "In election onclick: ${it.id}")
                sharedElectionsViewModel.showVotersInfoForUpcomingElection(it)
            }
        )

        binding.upcomingElectionsRecycler.adapter = electionListAdapter
        sharedElectionsViewModel.getUpcomingElections()
        sharedElectionsViewModel.showVotersInfoForUpcomingElection.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d(TAG, "Change observed, Election id: ${it.id}")
                sharedElectionsViewModel.getVoterInfoForUpcomingElections(it.id, it.division)
            }
        }
        sharedElectionsViewModel.upcomingElections.observe(viewLifecycleOwner) {
            it?.let {
                Log.d(TAG, "Upcoming election list change observed, list count: ${it.count()}")
                electionListAdapter.submitList(it)
                electionListAdapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }
}

class SavedElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionSavedBinding
    private lateinit var electionListAdapter: ElectionListAdapter

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "In SavedElectionsFragment onDetach")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "In SavedElectionsFragment onViewStateRestored")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "In SavedElectionsFragment onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "In SavedElectionsFragment onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "In SavedElectionsFragment onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "In SavedElectionsFragment onDestroy")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentElectionSavedBinding.inflate(layoutInflater)

        val sharedElectionsViewModel: ElectionsViewModel by viewModels({ requireParentFragment() })


        electionListAdapter = ElectionListAdapter(
            ElectionClickListener{
                Log.d(TAG, "In election onclick: ${it.id}")
                sharedElectionsViewModel.showVotersInfoForSavedElection(it)
            }
        )
        binding.savedElectionsRecycler.adapter = electionListAdapter
        sharedElectionsViewModel.showVotersInfoForSavedElection.observe(viewLifecycleOwner) {
            if (it != null) {
                sharedElectionsViewModel.getVoterInfoForSavedElections(it.id, it.division)
            }
        }
        sharedElectionsViewModel.savedElections.observe(viewLifecycleOwner) {
            it?.let {
                Log.d(TAG, "Saved election list change observed, Election id: ${it.count()}")
                it.forEach {
                    Log.d(TAG, "id: ${it.id}, expanded: ${it.expanded}, switch: ${it.savedElectionExpandSwitch}")
                }
                electionListAdapter.notifyDataSetChanged()
                electionListAdapter.submitList(it)
            }
        }

        return binding.root
    }
}