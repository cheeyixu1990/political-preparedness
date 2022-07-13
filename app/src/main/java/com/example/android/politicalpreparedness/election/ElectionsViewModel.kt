package com.example.android.politicalpreparedness.election

import android.graphics.Color
import android.util.Log
import android.widget.Button
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch


private const val TAG = "PPElectionsVM"

class ElectionsViewModel(private val dataSource: ElectionDao) : ViewModel() {

//    val nameQueryLiveData = MutableLiveData<List<Election>>()
//
//    fun getUsersWithNameLiveData(): LiveData<List<Election>> {
//        return Transformations.switchMap(
//            nameQueryLiveData
//        ) { name: Any? ->
//            myDataSource.getUsersWithNameLiveData(
//                name
//            )
//        }
//    }
//
//    fun setNameQuery(name: List<Election>?) {
//        this.nameQueryLiveData.setValue(name)
//    }

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    var savedElections = dataSource.getAllElections()

    private val _voterInfo = MutableLiveData<VoterInfoResponse?>()
    val voterInfo : LiveData<VoterInfoResponse?>
        get() = _voterInfo

    private val _showVotersInfoForUpcomingElection = MutableLiveData<Election?>()
    val showVotersInfoForUpcomingElection: LiveData<Election?>
        get() = _showVotersInfoForUpcomingElection

    private val _showVotersInfoForSavedElection = MutableLiveData<Election?>()
    val showVotersInfoForSavedElection: LiveData<Election?>
        get() = _showVotersInfoForSavedElection

    private val _upcomingElectionLoading = MutableLiveData<Boolean>()
    val upcomingElectionLoading: LiveData<Boolean>
        get() = _upcomingElectionLoading

    init {
        _upcomingElectionLoading.value = false
    }

    fun getUpcomingElections(){
        _upcomingElectionLoading.value = true
        viewModelScope.launch {
            try {
                val elections = CivicsApi.retrofitService.getAllElections().await().elections
                elections.removeAll { it.id == 2000 }
                _upcomingElections.value = elections

                _upcomingElectionLoading.value = false
            } catch (e: Exception) {
                Log.d(TAG, e.stackTraceToString())
                _upcomingElections.value = listOf()
            }
        }
    }

    fun getVoterInfoForSavedElections(electionId: Int, division: Division) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Getting voter info: election id: ${electionId}")
                Log.d(TAG, "Division state: ${division.state}")
                Log.d(TAG, "Division id: ${division.id}")
                val voterInfo = CivicsApi.retrofitService.getVoterInfo(division.id, electionId)
                _voterInfo.value = voterInfo
                savedElections.value?.find {
                    it.id == electionId
                }?.apply {
                    this.voterInfo = voterInfo
//                    Log.d(TAG, "Saved election this.expanded before change: ${expanded}")
                    savedElectionExpandSwitch = !savedElectionExpandSwitch!!
                    expanded = savedElectionExpandSwitch
                    dataSource.updateElection(this)
                    Log.d(TAG, "Saved election this.expanded: ${expanded}")
                }
                Log.d(TAG, "Number of savedElections: ${savedElections.value?.count()}")

                Log.d(TAG, "voterInfo: ${voterInfo.state}")

            } catch (e: Exception) {
                Log.d(TAG, "Error getting voter info: ${e.stackTraceToString()}")
                _voterInfo.value = null
            }
        }
    }

    fun getVoterInfoForUpcomingElections(electionId: Int, division: Division) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Getting voter info: election id: ${electionId}")
                Log.d(TAG, "Division state: ${division.state}")
                Log.d(TAG, "Division id: ${division.id}")
                val voterInfo = CivicsApi.retrofitService.getVoterInfo(division.id, electionId)
                _voterInfo.value = voterInfo
                _upcomingElections.value?.find {
                    it.id == electionId
                }?.apply {
                    this.voterInfo = voterInfo
                    expanded = !expanded!!
                    Log.d(TAG, "this.voterInfo: ${this.voterInfo}, expanded: ${expanded}")
                }

                val tempList = _upcomingElections.value
                _upcomingElections.value = tempList!!

                Log.d(TAG, "voterInfo: ${voterInfo.state}")

            } catch (e: Exception) {
                Log.d(TAG, "Error getting voter info: ${e.stackTraceToString()}")
                _voterInfo.value = null
            }
        }
    }

    fun showVotersInfoForUpcomingElection(election: Election) {
        _showVotersInfoForUpcomingElection.value = election
    }

    fun showVotersInfoForSavedElection(election: Election) {
        _showVotersInfoForSavedElection.value = election
    }

}

fun Button.setButtonState(electionId: Int) {
    val electionDao = ElectionDatabase.getInstance(context).electionDao
    val savedElection = electionDao.getElection(electionId)
    if (savedElection != null) {
        text = resources.getString(R.string.unfollow)
        setBackgroundColor(Color.RED)
    } else {
        text = resources.getString(R.string.follow)
        setBackgroundColor(Color.GREEN)
    }
    isClickable = true
}
