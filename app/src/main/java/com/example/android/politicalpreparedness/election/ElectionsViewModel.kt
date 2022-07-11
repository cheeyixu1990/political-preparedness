package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(private val dataSource: ElectionDao) : ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections


    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _navToShowVotersInfo = MutableLiveData<Election?>()
    val navToShowVotersInfo: LiveData<Election?>
        get() = _navToShowVotersInfo

    fun getUpcomingElections(){
        viewModelScope.launch {
            try {
                _upcomingElections.value = CivicsApi.retrofitService.getAllElections().elections
            } catch (e: Exception) {
                _upcomingElections.value = listOf()
            }
        }
    }

    fun getSavedElections(){
        _savedElections.value = dataSource.getAllElections().value
    }

    fun showVotersInfo(election: Election) {
        _navToShowVotersInfo.value = election
    }

    fun navigatedToVotersInfo() {
        _navToShowVotersInfo.value = null
    }

}